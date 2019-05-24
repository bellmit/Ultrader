package com.ultrader.bot.service;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.service.alpaca.AlpacaMarketDataService;
import com.ultrader.bot.service.alpaca.AlpacaPaperTradingService;
import com.ultrader.bot.service.alpaca.AlpacaTradingService;
import com.ultrader.bot.service.polygon.PolygonMarketDataService;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingPlatformConstant;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Trading platform, include all services
 * @author ytx1991
 */
@Service
public class TradingPlatform {
    private final static Logger LOGGER = LoggerFactory.getLogger(TradingPlatform.class);
    private TradingService tradingService;
    private MarketDataService marketDataService;
    private SettingDao settingDao;

    @Autowired
    public TradingPlatform(SettingDao settingDao,
                           AlpacaTradingService alpacaTradingService,
                           AlpacaPaperTradingService alpacaPaperTradingService,
                           AlpacaMarketDataService alpacaMarketDataService,
                           PolygonMarketDataService polygonMarketDataService) {
        Validate.notNull(settingDao, "SettingDao is required");
        this.settingDao = settingDao;
        String tradingPlatform = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADING_PLATFORM.getName(), "AlpacaPaper");
        String marketDataPlatform = RepositoryUtil.getSetting(settingDao, SettingConstant.MARKET_DATA_PLATFORM.getName(), "IEX");
        if(tradingPlatform.equals(TradingPlatformConstant.ALPACA)) {
            tradingService = alpacaTradingService;
        } else if(tradingPlatform.equals(TradingPlatformConstant.ALPACA_PAPER)) {
            tradingService = alpacaPaperTradingService;
        } else {
            LOGGER.error("Unknown trading platform, please check your config.");
        }
        if(marketDataPlatform.equals("IEX")) {
            marketDataService = alpacaMarketDataService;
        } else if (marketDataPlatform.equals("POLYGON")) {
            marketDataService = polygonMarketDataService;
        } else {
            LOGGER.error("Unknown market data platform, please check your config.");
        }
    }

    public MarketDataService getMarketDataService() {
        return marketDataService;
    }

    public TradingService getTradingService() {
        return tradingService;
    }
}
