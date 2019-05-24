package com.ultrader.bot.service.polygon;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.polygon.AggResponse;
import com.ultrader.bot.model.polygon.Aggv2;
import com.ultrader.bot.service.MarketDataService;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingUtil;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.num.PrecisionNum;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Polygon Market Data Service
 * @author ytx1991
 */
@Service
public class PolygonMarketDataService implements MarketDataService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PolygonMarketDataService.class);
    private final static int MIN_PER_TRADING_DAY = 390;
    private final String polygonKey;
    private final String frequency = "A.";
    private RestTemplate client;
    private Dispatcher dispatcher;
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    public PolygonMarketDataService(SettingDao settingDao, RestTemplateBuilder restTemplateBuilder) {
        Validate.notNull(restTemplateBuilder, "restTemplateBuilder is required");
        Validate.notNull(settingDao, "settingDao is required");

        this.polygonKey = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_KEY.getName(),
                RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_PAPER_KEY.getName(), ""));
        if(polygonKey.isEmpty()) {
            LOGGER.error("Cannot find Alpaca key, please set up and reboot.");
        }
        client = restTemplateBuilder.rootUri("https://api.polygon.io").build();
        threadPoolTaskExecutor =new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(200);
        threadPoolTaskExecutor.setMaxPoolSize(500);
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        threadPoolTaskExecutor.setQueueCapacity(5000);
        threadPoolTaskExecutor.initialize();
        //Init Websocket
        try {
            Options options = new Options.Builder()
                    .server("nats1.polygon.io:31101")
                    .server("nats2.polygon.io:31102")
                    .server("nats3.polygon.io:31103")
                    .token(RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_KEY.getName(),
                            RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_PAPER_KEY.getName(), "")))
                    .maxReconnects(-1).build();
            Connection connection = Nats.connect(options);
            LOGGER.info("Connect to Polygon. Status {}, {}", connection.getStatus(), connection.getConnectedUrl());
            dispatcher = connection.createDispatcher(new PolygonMessageHandler(this));
        } catch (Exception e) {
            LOGGER.error("Failed to connect to Polygon.", e);
        }

    }
    @Override
    public List<TimeSeries> updateTimeSeries(List<TimeSeries> stocks, Long interval) throws InterruptedException {
        if(interval < 60000) {
            //Trading period is smaller than 1 min.Don't call aggregate API
            return stocks;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String endDate = ZonedDateTime.now(ZoneId.of(TradingUtil.TIME_ZONE)).plusDays(1).format(formatter);
        String startDate = getStartDate(stocks.get(0), endDate, interval, formatter);
        if(startDate == null) {
            LOGGER.error("Cannot get the start date");
            return new ArrayList<>();
        }
        int newStockCount = 0;
        LOGGER.debug("Start date {}, End Date {}", startDate, endDate);
        for(TimeSeries timeSeries : stocks) {
            if(timeSeries.getBarCount() == 0) {
                GetStockBarsTask task = new GetStockBarsTask(timeSeries, client, startDate, endDate, getPeriodUnit(interval), getPeriodLength(interval), interval, polygonKey);
                threadPoolTaskExecutor.execute(task);
                newStockCount++;

            }
            //subscribe
            dispatcher.subscribe("AM." + timeSeries.getName());
        }
        LOGGER.info("Submitted {} update tasks", newStockCount);
        Thread.sleep(10000);
        return stocks;
    }



    private String getStartDate(TimeSeries timeSeries, String endDate, long interval, DateTimeFormatter formatter){
        try {
            long days = interval * timeSeries.getMaximumBarCount() / 60000 / MIN_PER_TRADING_DAY + 1;
            ZonedDateTime startDate = ZonedDateTime.now(ZoneId.of(TradingUtil.TIME_ZONE)).minusDays(days);
            AggResponse response = null;
            int lastSize = 0;
            do {
                if(response != null) {
                    lastSize = response.getResults().size();
                }
                String url = String.format("/v2/aggs/ticker/%s/range/%s/%s/%s/%s?apiKey=%s",
                        "AAPL", getPeriodLength(interval), getPeriodUnit(interval), startDate.format(formatter), endDate, polygonKey);
                response = client.getForObject(url, AggResponse.class);
                if(response.getResults().size() == lastSize) {
                    //Cannot get more results, throw exception
                    throw new Exception("Cannot get more results for stock " + timeSeries.getName() + " size: " + response.getResults().size());
                }
                startDate = startDate.minusDays(1);
            } while (response.getResults().size() < timeSeries.getMaximumBarCount());
            return startDate.plusDays(1).format(formatter);
        } catch (Exception e) {
            LOGGER.error("Cannot figure out the start date", e);
            return null;
        }

    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public String getFrequency() {
        return frequency;
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
        if(interval / 1000 >= 24 * 3600) {
            return "day";
        } else if (interval / 1000 >= 3600) {
            return "hour";
        } else {
            return "minute";
        }
    }

    private static final class GetStockBarsTask implements Runnable {
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
            String url = String.format("/v2/aggs/ticker/%s/range/%s/%s/%s/%s?apiKey=%s",
                    timeSeries.getName(), timeLength, timeUnit, startDate, endDate, key);
            try {
                AggResponse response = client.getForObject(url, AggResponse.class);
                if(response.getResults() == null) {
                    return ;
                }
                for (Aggv2 bar : response.getResults()) {
                    int barSize = timeSeries.getBarCount();
                    if( barSize == 0 || timeSeries.getLastBar().getBeginTime().toEpochSecond() <= bar.getT() / 1000) {
                        Instant i = Instant.ofEpochSecond(bar.getT() / 1000);
                        ZonedDateTime endDate = ZonedDateTime.ofInstant(i, ZoneId.of(TradingUtil.TIME_ZONE));
                        Bar newBar = new BaseBar(Duration.ofMillis(interval),endDate, PrecisionNum.valueOf(bar.getO()), PrecisionNum.valueOf(bar.getH()), PrecisionNum.valueOf(bar.getL()), PrecisionNum.valueOf(bar.getC()), PrecisionNum.valueOf(bar.getV()), PrecisionNum.valueOf(bar.getV() * bar.getC()));
                        timeSeries.addBar(newBar);
                    }
                }
                LOGGER.debug("Loaded stock {}, {} bars", timeSeries.getName(), response.getResults().size());
            } catch (Exception e) {
                LOGGER.error("Initial stock {} time series failed", timeSeries.getName());
            }

        }
    }
}
