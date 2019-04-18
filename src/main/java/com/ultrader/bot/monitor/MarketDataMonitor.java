package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.service.TradingService;
import com.ultrader.bot.service.alpaca.AlpacaMarketDataService;
import com.ultrader.bot.service.alpaca.AlpacaTradingService;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
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
    private final AlpacaMarketDataService alpacaMarketDataService;
    private final SettingDao settingDao;


    private boolean firstRun = true;
    private Date lastUpdateDate;
    private Map<String, Set<String>> availableStocks = null;
    public static Map<String, TimeSeries> timeSeriesMap = new HashMap<>();
    private MarketDataMonitor(final long interval, final TradingService tradingService, final AlpacaMarketDataService alpacaMarketDataService, final SettingDao settingDao) {
        super(interval);
        Validate.notNull(tradingService, "tradingService is required");
        Validate.notNull(alpacaMarketDataService, "alpacaMarketDataService is required");
        Validate.notNull(settingDao, "settingDao is required");

        this.settingDao = settingDao;
        this.alpacaMarketDataService = alpacaMarketDataService;
        this.tradingService = tradingService;
    }

    @Override
    void scan() {
        try {
            String platform = RepositoryUtil.getSetting(settingDao, SettingConstant.MARKET_DATA_PLATFORM_NAME.getName(), "IEX");
            LOGGER.info(String.format("Update market data, platform: %s", platform));
            boolean marketStatusChanged = false;
            //Check if the market is open
            if(tradingService.isMarketOpen()) {
                if(!marketOpen) {
                    marketStatusChanged = true;
                }
                marketOpen = true;
                LOGGER.info("Market is opened now.");
            } else {
                if(marketOpen) {
                    marketStatusChanged = true;
                }
                marketOpen = false;
                LOGGER.info("Market is closed now.");
            }
            //Get all stocks info, only do this once in a same trading day or on the first run
            if(marketStatusChanged || firstRun) {
                availableStocks = tradingService.getAvailableStocks();
            }
            //Get the list of stocks need to update
            Set<String> stockInExchange = new HashSet<>();
            //Filter by exchange
            String[] setExchanges = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_EXCHANGE_NAME.getName(), "NASDAQ,NYSE").split(DELIMITER);
            for(String exchange : setExchanges) {
                if(availableStocks.containsKey(exchange)) {
                    stockInExchange.addAll(availableStocks.get(exchange));
                }
            }
            LOGGER.debug(String.format("Found %d stocks in the exchanges.", stockInExchange.size()));
            //Filter by list
            boolean isWhiteList = Boolean.parseBoolean(RepositoryUtil.getSetting(settingDao, SettingConstant.WHITE_LIST_ENABLE_NAME.getName(), "true"));
            String[] customizedStockList = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_STOCK_LIST_NAME.getName(), "AMZN,AAPL,NVDA,GOOGL").split(DELIMITER);
            Set<String> watchList = new HashSet<>();
            for(String stock : customizedStockList) {
                if(stockInExchange.contains(stock)) {
                    watchList.add(stock);
                }
            }
            LOGGER.info(String.format("Found %d stocks need to update.", watchList.size()));
            //Update
            //TODO Currently we force all the indicator have to use same period, we should support different period for different indicators
            Map<String, TimeSeries> currentTimeSeries = new HashMap<>();
            int maxLength = Integer.parseInt(RepositoryUtil.getSetting(settingDao, SettingConstant.INDICATOR_MAX_LENGTH.getName(), "100")) * 2;
            maxLength = maxLength > 1000 ? 1000 : maxLength;
            List<TimeSeries> updateSeries = new ArrayList<>();
            for(String stock : watchList) {
                if(timeSeriesMap.containsKey(stock)) {
                    updateSeries.add(timeSeriesMap.get(stock));
                    timeSeriesMap.get(stock).setMaximumBarCount(maxLength);
                } else {
                    TimeSeries timeSeries = new BaseTimeSeries(stock);
                    timeSeries.setMaximumBarCount(maxLength);
                    updateSeries.add(timeSeries);
                }
            }
            updateSeries = alpacaMarketDataService.updateTimeSeries(updateSeries, getInterval());
            for (TimeSeries timeSeries : updateSeries) {
                currentTimeSeries.put(timeSeries.getName(), timeSeries);
                LOGGER.debug(String.format("Stock %s has %d bars", timeSeries.getName(), timeSeries.getBarCount()));
            }
            synchronized (TradingStrategyMonitor.lock) {
                this.timeSeriesMap = currentTimeSeries;
            }
            lastUpdateDate = new Date();
        } catch (Exception e) {
            LOGGER.error("Update market data failed. Your trading will be impacted", e);
        }
        firstRun = false;
    }

    public static boolean isMarketOpen() {
        return marketOpen;
    }

    public static void init(long interval, TradingService tradingService, AlpacaMarketDataService alpacaMarketDataService, SettingDao settingDao) {
        singleton_instance = new MarketDataMonitor(interval, tradingService, alpacaMarketDataService, settingDao);
    }

    public static MarketDataMonitor getInstance() throws IllegalAccessException {
        if(singleton_instance == null) {
            LOGGER.error("Instance is not initialized.");
            throw new IllegalAccessException("Instance is not initialized.");
        }
        return singleton_instance;
    }
}
