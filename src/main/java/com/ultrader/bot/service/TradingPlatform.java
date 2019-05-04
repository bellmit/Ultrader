package com.ultrader.bot.service;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.service.alpaca.AlpacaMarketDataService;
import com.ultrader.bot.service.alpaca.AlpacaPaperTradingService;
import com.ultrader.bot.service.alpaca.AlpacaTradingService;
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
                           AlpacaMarketDataService alpacaMarketDataService) {
        Validate.notNull(settingDao, "SettingDao is required");
        this.settingDao = settingDao;
        String tradingPlatform = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADING_PLATFORM_NAME.getName(), "AlpacaPaper");

        if(tradingPlatform.equals(TradingPlatformConstant.ALPACA)) {
            tradingService = alpacaTradingService;
            marketDataService = alpacaMarketDataService;
        } else if(tradingPlatform.equals(TradingPlatformConstant.ALPACA_PAPER)) {
            tradingService = alpacaPaperTradingService;
            marketDataService = alpacaMarketDataService;
        } else {
            LOGGER.error("Unknown trading platform, please check your config.");
            return;
        }
    }

    public MarketDataService getMarketDataService() {
        return marketDataService;
    }

    public TradingService getTradingService() {
        return tradingService;
    }
}
