package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.Account;
import com.ultrader.bot.model.Position;
import com.ultrader.bot.service.TradingService;
import com.ultrader.bot.service.alpaca.AlpacaWebSocketHandler;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.ta4j.core.BaseBar;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.num.PrecisionNum;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Sync Account information
 * @author ytx1991
 */
public class TradingAccountMonitor extends Monitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradingAccountMonitor.class);
    private final TradingService tradingService;
    private final SettingDao settingDao;
    private static TradingAccountMonitor singleton_instance = null;
    private static Map<String, Position> positions = new HashMap<>();
    private static Account account = new Account();

    TradingAccountMonitor(long interval, final TradingService tradingService, final SettingDao settingDao) {
        super(interval);
        Validate.notNull(tradingService, "tradingService is required");
        Validate.notNull(settingDao, "settingDao is required");
        this.tradingService = tradingService;
        this.settingDao = settingDao;

    }

    public static void init(long interval, final TradingService tradingService, final SettingDao settingDao) {
        singleton_instance = new TradingAccountMonitor(interval, tradingService, settingDao);
    }

    public static TradingAccountMonitor getInstance() throws IllegalAccessException {
        if(singleton_instance == null) {
            LOGGER.error("Instance is not initialized.");
            throw new IllegalAccessException("Instance is not initialized.");
        }
        return singleton_instance;
    }

    private Map<String, Position> getAllPositions() throws RuntimeException {
        Map<String, Position> positionMap = tradingService.getAllPositions();
        if(positionMap == null) {
            throw new RuntimeException("Cannot get position info, skip executing trading strategies");
        }
        LOGGER.info(String.format("Found %d positions.", positionMap.size()));
        return positionMap;
    }
    public static Map<String, Position> getPositions() {
        return positions;
    }
    public static Account getAccount() {
        return account;
    }
    @Override
    void scan() {
        try {
            if(!LicenseMonitor.getInstance().isValidLicense()) {
                LOGGER.error("Invalid license or expired license");
                return;
            }
            //Get current position
            positions = getAllPositions();
            if(RepositoryUtil.getSetting(settingDao, SettingConstant.MARKET_DATA_PLATFORM.getName(), "IEX").equals("IEX")) {
                //Add inter-bar price to position stocks
                synchronized (MarketDataMonitor.lock) {
                    for (String stock : positions.keySet()) {
                        addRealtimePrice(stock);
                    }
                }
            }

            //Get current portfolio
            account = syncAccount();


        } catch (Exception e) {
            LOGGER.error("Update trading account failed.", e);
        }
    }

    /**
     * Add a realtime bar in the time series, this should be called in a synchronized block
     * @param stock
     */
    private void addRealtimePrice(String stock) {
        TimeSeries timeSeries = MarketDataMonitor.timeSeriesMap.get(stock);
        if(timeSeries == null || timeSeries.getBarCount() == 0) {
            return;
        }
        //Update the realtime bar
        timeSeries.getLastBar().addPrice(PrecisionNum.valueOf(positions.get(stock).getCurrentPrice()));
        LOGGER.debug("Updated real time price. {}", timeSeries.getLastBar());
    }

    /**
     * Sync user trading account with platform
     * @return
     * @throws RuntimeException
     */
    private Account syncAccount() throws RuntimeException{
        Account account = tradingService.getAccountInfo();
        if(account == null) {
            throw new RuntimeException("Cannot get account info, skip executing trading strategies");
        }
        if(account.isTradingBlocked()) {
            throw new RuntimeException("Your api account is blocked trading, please check the trading platform account.");
        }
        return account;
    }

}
