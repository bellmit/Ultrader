package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.dao.StrategyDao;

import com.ultrader.bot.model.Account;
import com.ultrader.bot.model.Position;
import com.ultrader.bot.service.TradingService;
import com.ultrader.bot.service.alpaca.AlpacaPaperTradingService;
import com.ultrader.bot.service.alpaca.AlpacaTradingService;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingUtil;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.ta4j.core.*;
import org.ta4j.core.num.PrecisionNum;
;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Order strategy monitor
 * @author ytx1991
 */
public class TradingStrategyMonitor extends Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradingStrategyMonitor.class);
    private static TradingStrategyMonitor singleton_instance = null;
    public static Map<String, Strategy> strategies = new HashMap<>();
    public static Object lock = new Object();
    private final SettingDao settingDao;
    private final StrategyDao strategyDao;
    private final RuleDao ruleDao;
    private final TradingService tradingService;
    private final OrderDao orderDao;

    TradingStrategyMonitor(long interval, final TradingService tradingService, final SettingDao settingDao, final StrategyDao strategyDao, final RuleDao ruleDao, final OrderDao orderDao) {
        super(interval);
        Validate.notNull(tradingService, "tradingService is required");
        Validate.notNull(settingDao, "settingDao is required");
        Validate.notNull(strategyDao, "strategyDao is required");
        Validate.notNull(ruleDao, "ruleDao is required");
        Validate.notNull(orderDao, "orderDao is required");

        this.settingDao = settingDao;
        this.strategyDao = strategyDao;
        this.ruleDao = ruleDao;
        this.orderDao = orderDao;
        this.tradingService = tradingService;
    }

    public static void init(long interval, final TradingService tradingService, SettingDao settingDao, StrategyDao strategyDao, RuleDao ruleDao, OrderDao orderDao) {
        singleton_instance = new TradingStrategyMonitor(interval, tradingService, settingDao, strategyDao, ruleDao, orderDao);
    }

    public static TradingStrategyMonitor getInstance() throws IllegalAccessException {
        if(singleton_instance == null) {
            LOGGER.error("Instance is not initialized.");
            throw new IllegalAccessException("Instance is not initialized.");
        }
        return singleton_instance;
    }

    @Override
    void scan() {
        try {

            if(!LicenseMonitor.getInstance().isValidLicense()) {
                LOGGER.error("Invalid license or expired license");
                return;
            }
            if(!Boolean.parseBoolean(RepositoryUtil.getSetting(settingDao, SettingConstant.AUTO_TRADING_ENABLE.getName(), "true"))){
                LOGGER.info("Auto trading disabled");
                return;
            }
            LOGGER.info("Execute trading strategy.");
            //Get current position
            Map<String, Position> positionMap = tradingService.getAllPositions();
            LOGGER.debug(String.format("Found %d positions.", positionMap.size()));
            //Get current portfolio
            Account account = tradingService.getAccountInfo();
            if(account.isTradingBlocked()) {
                LOGGER.error("Your api account is blocked trading, please check the trading platform account.");
                return;
            }
            String buyLimit = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_BUY_MAX_LIMIT.getName(), "1%");

            synchronized (lock) {
                TradingUtil.updateStrategies(strategyDao, ruleDao, settingDao);
                LOGGER.info(String.format("Updated trading strategies for %d stocks", strategies.size()));
                for(Map.Entry<String, Strategy> entry : strategies.entrySet()) {
                    String stock = entry.getKey();
                    if(MarketDataMonitor.timeSeriesMap.containsKey(stock) && MarketDataMonitor.isMarketOpen()) {
                        TradingRecord tradingRecord = new BaseTradingRecord();
                        Double currentPrice = MarketDataMonitor.timeSeriesMap.get(stock).getLastBar().getClosePrice().doubleValue();
                        if(positionMap.containsKey(stock)) {
                            //The buy timing is incorrect, don't use for anything
                            tradingRecord.enter(1, PrecisionNum.valueOf(positionMap.get(stock).getAverageCost()), PrecisionNum.valueOf(positionMap.get(stock).getQuantity()));
                        }


                        if(entry.getValue().shouldEnter(MarketDataMonitor.timeSeriesMap.get(stock).getEndIndex()) && !positionMap.containsKey(stock) && positionMap.size() < 10) {
                            //buy strategy satisfy & no position
                            int buyQuantity = calculateBuyShares(buyLimit, currentPrice, account);
                            if(buyQuantity > 0) {
                                if(tradingService.postOrder(new com.ultrader.bot.model.Order("", stock, "buy", buyQuantity, currentPrice, "")) != null) {
                                    account.setBuyingPower(account.getBuyingPower() - currentPrice * buyQuantity);
                                    LOGGER.info(String.format("Buy %s %d shares at price %f.", stock, buyQuantity, currentPrice));
                                }
                            }
                        } else if (entry.getValue().shouldExit(MarketDataMonitor.timeSeriesMap.get(stock).getEndIndex(), tradingRecord) && positionMap.containsKey(stock)) {
                            //sell strategy satisfy & has position
                            tradingService.postOrder(new com.ultrader.bot.model.Order("", stock, "sell", positionMap.get(stock).getQuantity(), currentPrice, ""));
                            LOGGER.info(String.format("Sell %s %d shares at price %f.", stock, positionMap.get(stock).getQuantity(), currentPrice));
                        }
                    }
                }

            }
        } catch (Exception e) {
            LOGGER.error("Failed to execute trading strategy.", e);
        }
    }

    private static int calculateBuyShares(String limit, double price, Account account) {
        double amount;
        if(limit.indexOf("%") < 0) {
            amount = Double.parseDouble(limit);
        } else {
            amount = account.getPortfolioValue() * Double.parseDouble(limit.substring(0,limit.length()-1)) / 100;
        }
        int quantity = (int)Math.round(Math.floor(amount / price));
        if(account.getBuyingPower() < amount) {
            quantity = (int)Math.round(Math.floor(account.getBuyingPower() / price));
        }
        return quantity;
    }
}
