package com.ultrader.bot.service.polygon;

import com.google.common.util.concurrent.RateLimiter;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.ProgressMessage;
import com.ultrader.bot.model.polygon.AggResponse;
import com.ultrader.bot.model.polygon.Aggv2;
import com.ultrader.bot.monitor.MarketDataMonitor;
import com.ultrader.bot.service.MarketDataService;
import com.ultrader.bot.util.MarketDataUtil;
import com.ultrader.bot.util.MarketTrend;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingUtil;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.ta4j.core.*;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.InSlopeRule;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Polygon Market Data Service
 *
 * @author ytx1991
 */
public class PolygonMarketDataService implements MarketDataService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PolygonMarketDataService.class);
    private final static int MIN_PER_TRADING_DAY = 390;
    private final static int MAX_DATA_PER_REQUEST = 5000;
    private final static LocalTime MARKET_OPEN_TIME = LocalTime.parse("09:30:01");
    private final static LocalTime MARKET_CLOSE_TIME = LocalTime.parse("16:00:00");
    private String polygonKey;
    private RestTemplate client;
    private RestTemplateBuilder restTemplateBuilder;
    private WebSocketConnectionManager connectionManager;
    private PolygonWebSocketHandler handler;
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final SettingDao settingDao;
    private final SimpleClientHttpRequestFactory clientHttpRequestFactory;
    private RateLimiter rateLimiter;
    private int maxGap = 0;

    public PolygonMarketDataService(SettingDao settingDao, RestTemplateBuilder restTemplateBuilder) {
        Validate.notNull(restTemplateBuilder, "restTemplateBuilder is required");
        Validate.notNull(settingDao, "settingDao is required");
        rateLimiter = RateLimiter.create(150);
        this.settingDao = settingDao;
        this.maxGap = Integer.parseInt(RepositoryUtil.getSetting(settingDao, SettingConstant.MARKET_DATA_MAX_GAP.getName(), "3"));
        this.restTemplateBuilder = restTemplateBuilder;
        clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactory.setConnectTimeout(60_000);
        //Read timeout
        clientHttpRequestFactory.setReadTimeout(60_000);
        initService();
    }

    private void initService() {
        destroy();
        this.polygonKey = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_KEY.getName(), "");
        if (polygonKey.isEmpty()) {
            polygonKey = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_PAPER_KEY.getName(), "");
        }
        if (polygonKey.isEmpty()) {
            LOGGER.error("Cannot find Alpaca key, please set up and reboot.");
            return;
        }
        client = restTemplateBuilder.rootUri("https://api.polygon.io").build();
        client.setRequestFactory(clientHttpRequestFactory);
        threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(400);
        threadPoolTaskExecutor.setMaxPoolSize(1000);
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        threadPoolTaskExecutor.setQueueCapacity(10000);
        threadPoolTaskExecutor.initialize();

        //Init Websocket
        try {
            //Init Websocket
            if (!polygonKey.isEmpty()) {
                handler = new PolygonWebSocketHandler(polygonKey, this, Long.parseLong(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_PERIOD_SECOND.getName(), "60")));
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                container.setDefaultMaxBinaryMessageBufferSize(1024 * 1024);
                container.setDefaultMaxTextMessageBufferSize(10 * 1024 * 1024);
                connectionManager = new WebSocketConnectionManager(
                        new StandardWebSocketClient(container),
                        handler,
                        "wss://alpaca.socket.polygon.io/stocks");
                connectionManager.start();
            }
            LOGGER.info("Connect to Polygon.");
        } catch (Exception e) {
            LOGGER.error("Failed to connect to Polygon.", e);
            return;
        }

    }

    @Override
    public List<TimeSeries> updateTimeSeries(List<TimeSeries> stocks, Long interval) throws InterruptedException {
        if (connectionManager == null || !connectionManager.isRunning()) {
            initService();
        }
        interval = interval < 60000 ? 60000 : interval;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String endDate = ZonedDateTime.now(ZoneId.of(TradingUtil.TIME_ZONE)).plusDays(1).format(formatter);
        String startDate = getStartDate(endDate, interval, formatter);
        if (startDate == null) {
            int index = Integer.parseInt(RepositoryUtil.getSetting(settingDao, SettingConstant.INDICATOR_MAX_LENGTH.getName(), "100")) * 2;
            long day =  index / (MIN_PER_TRADING_DAY / (interval / 60000)) +1;
            startDate = ZonedDateTime.now(ZoneId.of(TradingUtil.TIME_ZONE)).minusDays(day).format(formatter);
        }
        int newStockCount = 0;
        LOGGER.debug("Start date {}, End Date {}", startDate, endDate);
        for (TimeSeries timeSeries : stocks) {
            if (timeSeries.getBarCount() == 0 && interval >= 60000 || connectionManager == null || !connectionManager.isRunning()) {
                rateLimiter.acquire();
                GetStockBarsTask task = new GetStockBarsTask(timeSeries, client, startDate, endDate, getPeriodUnit(interval), getPeriodLength(interval), interval, polygonKey);
                threadPoolTaskExecutor.execute(task);
                newStockCount++;

            }
        }
        LOGGER.info("Submitted {} update tasks", newStockCount);
        while (threadPoolTaskExecutor.getActiveCount() != 0) {
            LOGGER.debug("Remain {} stocks to update", threadPoolTaskExecutor.getThreadPoolExecutor().getQueue().size());
            Thread.sleep(1000);
        }
        LOGGER.info("Completed {} update tasks", newStockCount);
        return stocks;
    }

    @Override
    public void getTimeSeries(List<TimeSeries> stocks, Long interval, LocalDateTime startDate, LocalDateTime endDate, SimpMessagingTemplate notifier, String topic) throws InterruptedException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        long sectionLength = MAX_DATA_PER_REQUEST * interval / 60000 / MIN_PER_TRADING_DAY;
        LocalDateTime currentDate = null;
        do {
            int newStockCount = 0;
            currentDate = startDate.plusDays(sectionLength);
            if (currentDate.isAfter(endDate)) {
                currentDate = endDate;
            }
            String msg = String.format("Loading history data from %s to %s.", startDate.format(formatter), currentDate.format(formatter));
            LOGGER.info(msg);
            if (notifier != null) {
                long progress = 50 * (currentDate.toEpochSecond(ZoneOffset.UTC) - startDate.toEpochSecond(ZoneOffset.UTC)) / (endDate.toEpochSecond(ZoneOffset.UTC) - startDate.toEpochSecond(ZoneOffset.UTC));
                notifier.convertAndSend(topic, new ProgressMessage("InProgress", msg, (int) progress));
            }
            for (TimeSeries timeSeries : stocks) {
                if (interval >= 60000) {
                    rateLimiter.acquire();
                    GetStockBarsTask task = new GetStockBarsTask(timeSeries, client, startDate.format(formatter), currentDate.format(formatter), getPeriodUnit(interval), getPeriodLength(interval), interval, polygonKey);
                    threadPoolTaskExecutor.execute(task);
                    newStockCount++;

                }
            }
            LOGGER.info("Submitted {} get stocks request", newStockCount);
            while (threadPoolTaskExecutor.getActiveCount() != 0) {
                LOGGER.debug("Remain {} stocks to update", threadPoolTaskExecutor.getThreadPoolExecutor().getQueue().size());
                Thread.sleep(1000);
            }
            LOGGER.info("Completed {} update tasks", newStockCount);
            startDate = currentDate;
        } while (currentDate.isBefore(endDate));
        for (TimeSeries timeSeries : stocks) {
            LOGGER.info("stock {} bar {}", timeSeries.getName(), timeSeries.getBarCount());
        }
    }

    @Override
    public void subscribe(String symbol) {
        if (handler != null) {
            handler.subscribe(getFrequency() + symbol);
        }
    }

    @Override
    public void unsubscribe(String symbol) {
        if (handler != null) {
            handler.unsubscribe(getFrequency() + symbol);
        }
    }

    @Override
    public MarketTrend getMarketTrend(Long interval) {
        TimeSeries index = new BaseTimeSeries("SPY");
        index.setMaximumBarCount(Integer.parseInt(RepositoryUtil.getSetting(settingDao, SettingConstant.INDICATOR_MAX_LENGTH.getName(), "100")) * 2);
        try {
            updateTimeSeries(Collections.singletonList(index), interval);
            double onePercent = index.getLastBar().getClosePrice().doubleValue() / 100;
            Rule bullRule = new InSlopeRule(new EMAIndicator(new ClosePriceIndicator(index), 50), 30, PrecisionNum.valueOf(0.5 * onePercent), PrecisionNum.valueOf(0.85 * onePercent));
            Rule superBullRule = new InSlopeRule(new EMAIndicator(new ClosePriceIndicator(index), 50), 30, PrecisionNum.valueOf(0.85 * onePercent), PrecisionNum.valueOf(100 * onePercent));
            Rule bearRule = new InSlopeRule(new EMAIndicator(new ClosePriceIndicator(index), 50), 30, PrecisionNum.valueOf(-0.85 * onePercent), PrecisionNum.valueOf(-0.5 * onePercent));
            Rule superBearRule = new InSlopeRule(new EMAIndicator(new ClosePriceIndicator(index), 50), 30, PrecisionNum.valueOf(-90 * onePercent), PrecisionNum.valueOf(-0.85 * onePercent));
            if (superBullRule.isSatisfied(index.getEndIndex())) {
                return MarketTrend.SUPER_BULL;
            } else if (bullRule.isSatisfied(index.getEndIndex())) {
                return MarketTrend.BULL;
            } else if (superBearRule.isSatisfied(index.getEndIndex())) {
                return MarketTrend.SUPER_BEAR;
            } else if (bearRule.isSatisfied(index.getEndIndex())) {
                return MarketTrend.BEAR;
            } else {
                return MarketTrend.NORMAL;
            }
        } catch (Exception e) {
            LOGGER.error("Update market index failed. You trading setting may not switch based on the market.", e);
            return MarketTrend.NORMAL;
        }
    }

    @Override
    public void restart() {
        initService();
    }

    @Override
    public void destroy() {
        if (threadPoolTaskExecutor != null) {
            threadPoolTaskExecutor.getThreadPoolExecutor().getQueue().clear();
            threadPoolTaskExecutor.shutdown();
        }
        //If already init, clean up
        if (connectionManager != null) {
            try {
                connectionManager.stopInternal();
            } catch (Exception e) {
                LOGGER.info("Restart Polygon Websocket.");
            }
        }
    }


    private String getStartDate(String endDate, long interval, DateTimeFormatter formatter) {
        try {
            int index = Integer.parseInt(RepositoryUtil.getSetting(settingDao, SettingConstant.INDICATOR_MAX_LENGTH.getName(), "100")) * 2;
            long days =  index / (MIN_PER_TRADING_DAY / (interval / 60000)) +1;
            ZonedDateTime startDate = ZonedDateTime.now(ZoneId.of(TradingUtil.TIME_ZONE)).minusDays(days);
            AggResponse response = null;
            int lastSize = 0;
            do {
                if (response != null) {
                    lastSize = response.getResults().size();
                }
                String url = String.format("/v2/aggs/ticker/%s/range/%s/%s/%s/%s?apiKey=%s",
                        "AAPL", getPeriodLength(interval), getPeriodUnit(interval), startDate.format(formatter), endDate, polygonKey);
                response = client.getForObject(url, AggResponse.class);
                if (response.getResults().size() == lastSize) {
                    //Cannot get more results, throw exception
                    throw new Exception("Cannot get more results for stock AAPL size: " + response.getResults().size());
                }
                startDate = startDate.minusDays(1);
            } while (response.getResults().size() < index);
            return startDate.plusDays(1).format(formatter);
        } catch (Exception e) {
            LOGGER.error("Cannot figure out the start date", e);
            return null;
        }

    }

    public String getFrequency() {
        String frequency = RepositoryUtil.getSetting(settingDao, SettingConstant.POLYGON_WS_FREQUENCY.getName(), "AUTO");
        switch (frequency) {
            case "MINUTE":
                return "AM.";
            case "SECOND":
                return "A.";
            default:
                return MarketDataMonitor.timeSeriesMap.size() < 1000 ? "A." : "AM.";
        }
    }

    private long getPeriodLength(long interval) {
        switch (getPeriodUnit(interval)) {
            case "day":
                return interval / 1000 / 24 / 3600;
            case "hour":
                return interval / 1000 / 3600;
            default:
                return interval / 1000 / 60;
        }
    }

    private String getPeriodUnit(long interval) {
        if (interval / 1000 >= 24 * 3600) {
            return "day";
        } else if (interval / 1000 >= 3600) {
            return "hour";
        } else {
            return "minute";
        }
    }

    private final class GetStockBarsTask implements Runnable {
        private TimeSeries timeSeries;
        private RestTemplate client;
        private final String startDate;
        private final String endDate;
        private final String timeUnit;
        private final long timeLength;
        private final long interval;
        private final String key;

        protected GetStockBarsTask(TimeSeries timeSeries,
                                   final RestTemplate client,
                                   final String startDate,
                                   final String endDate,
                                   final String timeUnit,
                                   final long timeLength,
                                   final long interval,
                                   final String key) {
            Validate.notNull(timeSeries, "timeSeries is required");
            this.timeSeries = timeSeries;
            this.client = client;
            this.startDate = startDate;
            this.endDate = endDate;
            this.timeUnit = timeUnit;
            this.timeLength = timeLength;
            this.interval = interval;
            this.key = key;
        }

        @Override
        public void run() {
            String url = String.format("/v2/aggs/ticker/%s/range/%s/%s/%s/%s?apiKey=%s&unadjusted=false",
                    timeSeries.getName(), timeLength, timeUnit, startDate, endDate, key);
            try {
                AggResponse response = client.getForObject(url, AggResponse.class);
                if (response.getResults() == null) {
                    return;
                }
                for (Aggv2 bar : response.getResults()) {
                    int barSize = timeSeries.getBarCount();
                    if (barSize == 0 || timeSeries.getLastBar().getBeginTime().toEpochSecond() <= bar.getT() / 1000) {
                        Instant i = Instant.ofEpochSecond((bar.getT() + interval) / 1000);
                        ZonedDateTime endDate = ZonedDateTime.ofInstant(i, ZoneId.of(TradingUtil.TIME_ZONE));
                        if (endDate.toLocalTime().isBefore(MARKET_OPEN_TIME) || endDate.toLocalTime().isAfter(MARKET_CLOSE_TIME)) {
                            //Filter out market closed hours data
                            continue;
                        }
                        Bar newBar = new BaseBar(
                                Duration.ofMillis(interval),
                                endDate,
                                PrecisionNum.valueOf(bar.getO()),
                                PrecisionNum.valueOf(bar.getH()),
                                PrecisionNum.valueOf(bar.getL()),
                                PrecisionNum.valueOf(bar.getC()),
                                PrecisionNum.valueOf(bar.getV()),
                                PrecisionNum.valueOf(bar.getV() * bar.getC()));
                        if (barSize > 0 && timeSeries.getLastBar().getEndTime().toEpochSecond() == endDate.toEpochSecond()) {
                            //Replace last bar
                            timeSeries.addBar(newBar, true);
                            LOGGER.debug("Replaced {} last bar {}", timeSeries.getName(), newBar);
                        } else {
                            if (barSize == 0 || endDate.getDayOfYear() != timeSeries.getLastBar().getEndTime().getDayOfYear() || (endDate.toEpochSecond() - timeSeries.getLastBar().getEndTime().toEpochSecond()) == interval) {
                                //Time series must be continuous
                                timeSeries.addBar(newBar);
                            } else {
                                //Auto filling the gap
                                while (timeSeries.getLastBar().getEndTime().isBefore(newBar.getBeginTime())) {
                                    //Copy previous bar
                                    timeSeries.addBar(MarketDataUtil.fillBarGap(timeSeries.getLastBar(), interval / 1000));
                                }
                                timeSeries.addBar(newBar);
                            }

                        }
                    }
                }
                LOGGER.debug("Loaded stock {}, {} bars", timeSeries.getName(), response.getResults().size());

            } catch (Exception e) {
                LOGGER.error("Initial stock {} time series failed, url {}", timeSeries.getName(), url, e);
            }

        }
    }
}
