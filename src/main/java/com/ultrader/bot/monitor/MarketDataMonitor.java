package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.Setting;
import com.ultrader.bot.service.alpaca.AlpacaTradingService;
import com.ultrader.bot.util.RepositoryUtil;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Market data monitor
 * @author ytx1991
 */
public class MarketDataMonitor extends Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataMonitor.class);
    private static MarketDataMonitor singleton_instance = null;
    private static final String WHITE_LIST_ENABLE_NAME = "TRADE_WHITE_LIST_ENABLE";
    private static final String TRADE_STOCK_LIST_NAME = "TRADE_STOCK_LIST";
    private static final String MARKET_DATA_PLATFORM_NAME = "GLOBAL_MARKETDATA_PLATFORM";
    private static final String ALPACA_NAME = "Alpaca";

    private final AlpacaTradingService alpacaTradingService;
    private final SettingDao settingDao;

    private boolean marketOpen = false;

    private MarketDataMonitor(final long interval, final AlpacaTradingService alpacaTradingService, final SettingDao settingDao) {
        super(interval);
        Validate.notNull(alpacaTradingService, "alpacaTradingService is required");
        Validate.notNull(settingDao, "settingDao is required");

        this.settingDao = settingDao;
        this.alpacaTradingService = alpacaTradingService;
    }

    @Override
    void scan() {
        try {
            String platform = RepositoryUtil.getSetting(settingDao, MARKET_DATA_PLATFORM_NAME, "IEX");
            LOGGER.info(String.format("Update market data, platform: %s", platform));
            boolean marketStatusChanged = false;
            //Check if the market is open
            if(alpacaTradingService.isMarketOpen()) {
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
            //Get all stocks info, only do this once in a same trading day



            //Get the list of stocks need to update

            boolean isWhiteList = Boolean.parseBoolean(RepositoryUtil.getSetting(settingDao, WHITE_LIST_ENABLE_NAME, "true"));


            //Update
        } catch (Exception e) {
            LOGGER.error("Update market data failed. Your trading will be impacted", e);
        }


    }

    public boolean isMarketOpen() {
        return marketOpen;
    }

    public static void init(long interval, AlpacaTradingService alpacaTradingService, SettingDao settingDao) {
        singleton_instance = new MarketDataMonitor(interval, alpacaTradingService, settingDao);
    }

    public static MarketDataMonitor getInstance() throws IllegalAccessException {
        if(singleton_instance == null) {
            LOGGER.error("Instance is not initialized.");
            throw new IllegalAccessException("Instance is not initialized.");
        }
        return singleton_instance;
    }
}
