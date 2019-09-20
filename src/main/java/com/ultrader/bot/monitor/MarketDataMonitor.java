package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.NotificationDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.Notification;
import com.ultrader.bot.model.websocket.StatusMessage;
import com.ultrader.bot.service.MarketDataService;
import com.ultrader.bot.service.TradingService;
import com.ultrader.bot.util.NotificationUtil;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import java.util.*;

import static com.ultrader.bot.util.SettingConstant.DELIMITER;

/**
 * Market data monitor
 * @author ytx1991
 */
public class MarketDataMonitor extends Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataMonitor.class);
    private static MarketDataMonitor singleton_instance = null;
    private static boolean marketOpen = false;

    private final TradingService tradingService;
    private final MarketDataService marketDataService;
    private final SettingDao settingDao;
    private final SimpMessagingTemplate notifier;
    private final NotificationDao notificationDao;

    private boolean firstRun = true;
    private Date lastUpdateDate;
    private Map<String, Set<String>> availableStocks = null;
    public static Map<String, TimeSeries> timeSeriesMap = new HashMap<>();
    public static Object lock = new Object();
    private MarketDataMonitor(final long interval,
                              final TradingService tradingService,
                              final MarketDataService marketDataService,
                              final SettingDao settingDao,
                              final SimpMessagingTemplate notifier,
                              final NotificationDao notificationDao) {
        super(interval);
        Validate.notNull(tradingService, "tradingService is required");
        Validate.notNull(marketDataService, "marketDataService is required");
        Validate.notNull(settingDao, "settingDao is required");
        Validate.notNull(notifier, "notifier is required");
        Validate.notNull(notificationDao, "notificationDao is required");
        this.settingDao = settingDao;
        this.marketDataService = marketDataService;
        this.tradingService = tradingService;
        this.notifier = notifier;
        this.notificationDao = notificationDao;
    }

    @Override
    void scan() {
        long start = System.currentTimeMillis();
        try {
            String platform = RepositoryUtil.getSetting(settingDao, SettingConstant.MARKET_DATA_PLATFORM.getName(), "IEX");
            LOGGER.info(String.format("Update market data, platform: %s", platform));
            boolean marketStatusChanged = false;
            //Check if the market is open
            if(tradingService.isMarketOpen()) {
                if(!marketOpen) {
                    marketStatusChanged = true;
                }
                marketOpen = true;
                notifier.convertAndSend("/topic/status/market", new StatusMessage("opened", "Market is open"));
                LOGGER.info("Market is opened now.");
            } else {
                if(marketOpen) {
                    marketStatusChanged = true;
                }
                marketOpen = false;
                notifier.convertAndSend("/topic/status/market", new StatusMessage("closed", "Market is closed"));
                LOGGER.info("Market is closed now.");
            }
            //Get all stocks info, only do this once in a same trading day or on the first run
            if(marketStatusChanged || firstRun) {
                availableStocks = tradingService.getAvailableStocks();
            }
            //Get the list of stocks need to update
            Set<String> stockInExchange = new HashSet<>();
            //Filter by exchange
            String[] setExchanges = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_EXCHANGE.getName(), "NASDAQ,NYSE").split(DELIMITER);
            for(String exchange : setExchanges) {
                if(availableStocks.containsKey(exchange)) {
                    stockInExchange.addAll(availableStocks.get(exchange));
                }
            }
            LOGGER.debug(String.format("Found %d stocks in the exchanges.", stockInExchange.size()));
            //Filter by list
            boolean isWhiteList = Boolean.parseBoolean(RepositoryUtil.getSetting(settingDao, SettingConstant.WHITE_LIST_ENABLE.getName(), "true"));
            Set<String> customizedStockList = new HashSet<>(Arrays.asList(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_STOCK_LIST.getName(), "AMZN,AAPL,NVDA,GOOGL").split(DELIMITER)));
            Set<String> watchList = new HashSet<>();
            for(String stock : stockInExchange) {
                if(isWhiteList && customizedStockList.contains(stock)) {
                    watchList.add(stock);
                } else if (!isWhiteList && !customizedStockList.contains(stock)) {
                    watchList.add(stock);
                }
            }
            LOGGER.info(String.format("Found %d stocks need to update.", watchList.size()));
            synchronized (lock) {
                //Update
                if(marketStatusChanged) {
                    //Reload market data
                    timeSeriesMap.clear();
                    tradingService.restart();
                }
                //TODO Currently we force all the indicator have to use same period, we should support different period for different indicators
                Map<String, TimeSeries> currentTimeSeries = new HashMap<>();
                int maxLength = Integer.parseInt(RepositoryUtil.getSetting(settingDao, SettingConstant.INDICATOR_MAX_LENGTH.getName(), "100")) * 2;
                maxLength = maxLength > 1000 ? 1000 : maxLength;
                List<TimeSeries> updateSeries = new ArrayList<>();
                for (String stock : watchList) {
                    if (timeSeriesMap.containsKey(stock)) {
                        updateSeries.add(timeSeriesMap.get(stock));
                        timeSeriesMap.get(stock).setMaximumBarCount(maxLength);
                    } else {
                        TimeSeries timeSeries = new BaseTimeSeries(stock);
                        timeSeries.setMaximumBarCount(maxLength);
                        updateSeries.add(timeSeries);
                    }
                }

                updateSeries = marketDataService.updateTimeSeries(updateSeries, getInterval());
                //For max, If smaller than 0 then no limit
                double priceMax = Double.parseDouble(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_PRICE_LIMIT_MAX.getName(), "-1.0"));
                double priceMin = Double.parseDouble(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_PRICE_LIMIT_MIN.getName(), "0.0"));
                double volumeMax = Double.parseDouble(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_VOLUME_LIMIT_MAX.getName(), "-1.0"));
                double volumeMin = Double.parseDouble(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_VOLUME_LIMIT_MIN.getName(), "0.0"));
                for (TimeSeries timeSeries : updateSeries) {
                    //Don't filter positions
                    if(TradingAccountMonitor.getPositions().containsKey(timeSeries.getName())) {
                        currentTimeSeries.put(timeSeries.getName(), timeSeries);
                        continue;
                    }
                    //Filter by time series length
                    if (timeSeries.getBarCount() <= maxLength / 2) {
                        LOGGER.debug("Filter out {} because of missing data. Bar length {}", timeSeries.getName(), timeSeries.getBarCount());
                        continue;
                    }

                    //Filter by price max limit
                    if (priceMax > 0 && timeSeries.getLastBar().getClosePrice().doubleValue() > priceMax) {
                        LOGGER.debug("Filter out {} because of max price limit. Close price {}", timeSeries.getName(), timeSeries.getLastBar().getClosePrice());
                        continue;
                    }
                    //Filter by price min limit
                    if (timeSeries.getLastBar().getClosePrice().doubleValue() < priceMin) {
                        LOGGER.debug("Filter out {} because of min price limit. Close price {}", timeSeries.getName(), timeSeries.getLastBar().getClosePrice());
                        continue;
                    }
                    //Filter by volume max limit
                    if (volumeMax > 0 && timeSeries.getLastBar().getVolume().doubleValue() > volumeMax) {
                        LOGGER.debug("Filter out {} because of max volume limit. Volume {}", timeSeries.getName(), timeSeries.getLastBar().getVolume());
                        continue;
                    }
                    //Filter by volume min limit
                    if (timeSeries.getLastBar().getVolume().doubleValue() < volumeMin) {
                        LOGGER.debug("Filter out {} because of max price limit. Volume {}", timeSeries.getName(), timeSeries.getLastBar().getVolume());
                        continue;
                    }
                    currentTimeSeries.put(timeSeries.getName(), timeSeries);
                    LOGGER.debug(String.format("Stock %s has %d bars", timeSeries.getName(), timeSeries.getBarCount()));
                }
                LOGGER.info("{} stocks filtered out, {} stocks remains.", updateSeries.size() - currentTimeSeries.size(), currentTimeSeries.size());
                this.timeSeriesMap = currentTimeSeries;
                //Update subscribe
                for (TimeSeries timeSeries : updateSeries) {
                    if(timeSeriesMap.containsKey(timeSeries.getName())) {
                        marketDataService.subscribe(timeSeries.getName());
                    } else {
                        marketDataService.unsubscribe(timeSeries.getName());
                    }

                }
            }
            lastUpdateDate = new Date();
        } catch (Exception e) {
            LOGGER.error("Update market data failed. Your trading will be impacted", e);
            NotificationUtil.sendNotification(notifier, notificationDao, new Notification(
                    UUID.randomUUID().toString(),
                    "WARN",
                    "Market data updated failed. Try to reboot your bot or check the log.",
                    "Update Failure",
                    new Date()));
        }
        firstRun = false;
        long end = System.currentTimeMillis();
        if (end - start > getInterval() && marketOpen) {
            //Cannot update all asset in limit time
            LOGGER.error("Cannot update market data in time. Please reduce the monitoring assets number or increase trading period.");
            NotificationUtil.sendNotification(notifier, notificationDao, new Notification(
                    UUID.randomUUID().toString(),
                    "ERROR",
                    "The bot cannot update all the stocks in one trading period. Try to reduce the stocks you want to monitor or increase the trade period",
                    "Insufficient Computing Resource",
                    new Date()));
        }
    }

    public Map<String, Set<String>> getAvailableStock() {
        if (availableStocks == null) {
            availableStocks = tradingService.getAvailableStocks();
        }
        return availableStocks;
    }
    public static boolean isMarketOpen() {
        return marketOpen;
    }

    public static void init(long interval,
                            TradingService tradingService,
                            MarketDataService marketDataService,
                            SettingDao settingDao,
                            SimpMessagingTemplate notifier,
                            NotificationDao notificationDao) {
        singleton_instance = new MarketDataMonitor(
                interval,
                tradingService,
                marketDataService,
                settingDao,
                notifier,
                notificationDao);
    }

    public static MarketDataMonitor getInstance() throws IllegalAccessException {
        if(singleton_instance == null) {
            LOGGER.error("Instance is not initialized.");
            throw new IllegalAccessException("Instance is not initialized.");
        }
        return singleton_instance;
    }
}
