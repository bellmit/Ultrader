package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.AssetListDao;
import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.AssetList;
import com.ultrader.bot.service.NotificationService;
import com.ultrader.bot.service.TradingPlatform;
import com.ultrader.bot.util.*;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private static MarketTrend marketTrend = MarketTrend.NORMAL;

    private final TradingPlatform tradingPlatform;
    private final SettingDao settingDao;
    private final NotificationService notifier;
    private final AssetListDao assetListDao;
    private final JdbcTemplate jdbcTemplate;
    private final OrderDao orderDao;

    private boolean firstRun = true;
    private Date lastUpdateDate;
    private Map<String, Set<String>> availableStocks = null;
    public static Map<String, TimeSeries> timeSeriesMap = new HashMap<>();
    public static Object lock = new Object();
    private MarketDataMonitor(final long interval,
                              final TradingPlatform tradingPlatform,
                              final SettingDao settingDao,
                              final AssetListDao assetListDao,
                              final NotificationService notifier,
                              final JdbcTemplate jdbcTemplate,
                              final OrderDao orderDao) {
        super(interval);
        Validate.notNull(tradingPlatform, "tradingPlatform is required");
        Validate.notNull(settingDao, "settingDao is required");
        Validate.notNull(notifier, "notifier is required");
        Validate.notNull(assetListDao, "assetListDao is required");
        Validate.notNull(jdbcTemplate, "jdbcTemplate is required");
        Validate.notNull(orderDao, "orderDao is required");
        this.settingDao = settingDao;
        this.tradingPlatform = tradingPlatform;
        this.notifier = notifier;
        this.assetListDao = assetListDao;
        this.jdbcTemplate = jdbcTemplate;
        this.orderDao = orderDao;
    }

    @Override
    synchronized void scan() {
        long start = System.currentTimeMillis();
        try {
            String platform = RepositoryUtil.getSetting(settingDao, SettingConstant.MARKET_DATA_PLATFORM.getName(), "IEX");
            LOGGER.info(String.format("Update market data, platform: %s", platform));
            boolean marketStatusChanged = false;
            //Check if the market is open
            if(tradingPlatform.getTradingService().isMarketOpen()) {
                if(!marketOpen) {
                    marketStatusChanged = true;
                    TradingStrategyMonitor.getDayTradeCount().clear();
                }
                marketOpen = true;
                LOGGER.info("Market is opened now.");
            } else {
                if(marketOpen) {
                    marketStatusChanged = true;
                    //Backup database after market closed
                    DatabaseUtil.backup(jdbcTemplate);
                }
                marketOpen = false;
                LOGGER.info("Market is closed now.");
            }
            notifier.sendMarketStatus(marketOpen);
            //Get all stocks info, only do this once in a same trading day or on the first run
            if(marketStatusChanged || firstRun) {
                availableStocks = tradingPlatform.getTradingService().getAvailableStocks();
            }
            //Sync history orders
            try {
                OrderUtil.loadHistoryOrders(orderDao, tradingPlatform.getTradingService());
                notifier.sendProfitNotification(1, true);
                notifier.sendProfitNotification(7, true);
                notifier.sendProfitNotification(30, true);
                notifier.sendProfitNotification(365, true);
            } catch (Exception e) {
                LOGGER.error("Load history orders failed.");
            }
            //Update the market trend
            marketTrend = tradingPlatform.getMarketDataService().getMarketTrend(getInterval());
            LOGGER.info("Updated market trend {}", marketTrend.name());
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
            String assetList = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_STOCK_LIST.getName(), "");
            Set<String> customizedStockList = new HashSet<>();
            if (!assetList.isEmpty()) {
                Optional<AssetList> assets = assetListDao.findById(assetList);
                if (assets.isPresent()) {
                    customizedStockList = new HashSet<>(Arrays.asList(assets.get().getSymbols().split(DELIMITER)));
                }
            }
            Set<String> watchList = new HashSet<>();
            for(String stock : stockInExchange) {
                if(isWhiteList && customizedStockList.contains(stock)) {
                    watchList.add(stock);
                } else if (!isWhiteList && !customizedStockList.contains(stock)) {
                    watchList.add(stock);
                }
            }
            //Add current positions, ignore the filters
            TradingAccountMonitor.getPositions().keySet().stream().forEach(s -> watchList.add(s));
            LOGGER.info(String.format("Found %d stocks need to update.", watchList.size()));
            synchronized (lock) {
                //Update
                if(marketStatusChanged) {
                    //Reload market data
                    tradingPlatform.restart();
                }
                //TODO Currently we force all the indicator have to use same period, we should support different period for different indicators
                Map<String, TimeSeries> currentTimeSeries = new HashMap<>();
                int maxLength = Integer.parseInt(RepositoryUtil.getSetting(settingDao, SettingConstant.INDICATOR_MAX_LENGTH.getName(), "50")) * 2;
                maxLength = maxLength > 1000 ? 1000 : maxLength;
                List<TimeSeries> updateSeries = new ArrayList<>();
                for (String stock : watchList) {
                    if (timeSeriesMap.containsKey(stock) && timeSeriesMap.get(stock) != null) {
                        updateSeries.add(timeSeriesMap.get(stock));
                        timeSeriesMap.get(stock).setMaximumBarCount(maxLength);
                    } else {
                        TimeSeries timeSeries = new BaseTimeSeries(stock);
                        timeSeries.setMaximumBarCount(maxLength);
                        updateSeries.add(timeSeries);
                    }
                }

                updateSeries = tradingPlatform.getMarketDataService().updateTimeSeries(updateSeries, getInterval());
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
                        tradingPlatform.getMarketDataService().subscribe(timeSeries.getName());
                    } else {
                        tradingPlatform.getMarketDataService().unsubscribe(timeSeries.getName());
                    }

                }
            }
            lastUpdateDate = new Date();
        } catch (Exception e) {
            LOGGER.error("Update market data failed. Your trading will be impacted", e);
            notifier.sendNotification(
                    "MarketData Service Failure",
                    "Market data updated failed. Try to reboot your bot or check the log.",
                    NotificationType.ERROR);
        }
        firstRun = false;
        long end = System.currentTimeMillis();
        if (end - start > getInterval() && marketOpen) {
            //Cannot update all asset in limit time
            LOGGER.error("Cannot update market data in time. Please reduce the monitoring assets number or increase trading period.");
            notifier.sendNotification(
                    "MarketData Service Failure",
                    "The bot cannot update all the stocks in one trading period. Try to reduce the stocks you want to monitor or increase the trade period",
                    NotificationType.ERROR);

        }
    }

    public Map<String, Set<String>> getAvailableStock() {
        if (availableStocks == null) {
            availableStocks = tradingPlatform.getTradingService().getAvailableStocks();
        }
        return availableStocks;
    }
    public static boolean isMarketOpen() {
        return marketOpen;
    }

    public static MarketTrend getMarketTrend() {
        return marketTrend;
    }

    public static void init(long interval,
                            TradingPlatform tradingPlatform,
                            SettingDao settingDao,
                            AssetListDao assetListDao,
                            NotificationService notifier,
                            JdbcTemplate jdbcTemplate,
                            OrderDao orderDao) {
        singleton_instance = new MarketDataMonitor(
                interval,
                tradingPlatform,
                settingDao,
                assetListDao,
                notifier,
                jdbcTemplate,
                orderDao);
    }

    public static MarketDataMonitor getInstance() throws IllegalAccessException {
        if(singleton_instance == null) {
            LOGGER.error("Instance is not initialized.");
            throw new IllegalAccessException("Instance is not initialized.");
        }
        return singleton_instance;
    }
}
