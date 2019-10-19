package com.ultrader.bot.service;

import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.service.alpaca.AlpacaMarketDataService;
import com.ultrader.bot.service.alpaca.AlpacaTradingServiceV2;
import com.ultrader.bot.service.polygon.PolygonMarketDataService;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingPlatformConstant;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

/**
 * Trading platform, include all services
 * @author ytx1991
 */
@Service("TradingPlatform")
public class TradingPlatform {
    private final static Logger LOGGER = LoggerFactory.getLogger(TradingPlatform.class);
    private TradingService tradingService;
    private MarketDataService marketDataService;
    private final OrderDao orderDao;
    private final NotificationService notifier;
    private final SettingDao settingDao;
    private final RestTemplateBuilder restTemplate;

    @Autowired
    public TradingPlatform(SettingDao settingDao, OrderDao orderDao, NotificationService notifier, RestTemplateBuilder restTemplate) {
        Validate.notNull(settingDao, "SettingDao is required");
        Validate.notNull(orderDao, "orderDao is required");
        Validate.notNull(notifier, "notifier is required");
        Validate.notNull(restTemplate, "restTemplate is required");
        this.settingDao = settingDao;
        this.orderDao = orderDao;
        this.notifier = notifier;
        this.restTemplate = restTemplate;
        initAllPlatformServices();
    }

    private void initAllPlatformServices() {
        String tradingPlatform = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADING_PLATFORM.getName(), TradingPlatformConstant.ALPACA_PAPER);
        String marketDataPlatform = RepositoryUtil.getSetting(settingDao, SettingConstant.MARKET_DATA_PLATFORM.getName(), "IEX");
        tradingService = new AlpacaTradingServiceV2(settingDao, restTemplate, orderDao, notifier, tradingPlatform);

        if(marketDataPlatform.equals(TradingPlatformConstant.POLYGON)) {
            marketDataService = new PolygonMarketDataService(settingDao, restTemplate);
        } else  {
            marketDataService = new AlpacaMarketDataService(settingDao, restTemplate);
        }
    }
    public MarketDataService getMarketDataService() {
        return marketDataService;
    }

    public TradingService getTradingService() {
        return tradingService;
    }

    public void restart() {
       destroy();
       initAllPlatformServices();
    }

    public void destroy() {
        tradingService.destroy();
        marketDataService.destroy();
    }
}
