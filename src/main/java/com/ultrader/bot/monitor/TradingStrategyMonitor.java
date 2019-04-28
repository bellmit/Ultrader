package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.dao.StrategyDao;

import com.ultrader.bot.model.Account;
import com.ultrader.bot.model.Position;
import com.ultrader.bot.service.TradingService;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingUtil;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.*;
import org.ta4j.core.num.PrecisionNum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    TradingStrategyMonitor(long interval, final TradingService tradingService, final SettingDao settingDao, final StrategyDao strategyDao, final RuleDao ruleDao) {
        super(interval);
        Validate.notNull(tradingService, "tradingService is required");
        Validate.notNull(settingDao, "settingDao is required");
        Validate.notNull(strategyDao, "strategyDao is required");
        Validate.notNull(ruleDao, "ruleDao is required");

        this.settingDao = settingDao;
        this.strategyDao = strategyDao;
        this.ruleDao = ruleDao;
        this.tradingService = tradingService;
    }

    public static void init(long interval, final TradingService tradingService, SettingDao settingDao, StrategyDao strategyDao, RuleDao ruleDao) {
        singleton_instance = new TradingStrategyMonitor(interval, tradingService, settingDao, strategyDao, ruleDao);
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
            //Get current position
            Map<String, Position> positionMap = getAllPositions();

            //Get current portfolio
            Account account = getAccount();
            LOGGER.info("Execute trading strategy.");

            //Get open orders
            Map<String, com.ultrader.bot.model.Order> openOrders = tradingService.getOpenOrders();
            String buyLimit = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_BUY_MAX_LIMIT.getName(), "1%");
            int holdLimit = Integer.parseInt(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_BUY_HOLDING_LIMIT.getName(), "0"));
            holdLimit = holdLimit == 0 ? Integer.MAX_VALUE : holdLimit;
            int positionNum = positionMap.size();
            int minLength = Integer.parseInt(RepositoryUtil.getSetting(settingDao, SettingConstant.INDICATOR_MAX_LENGTH.getName(), "50"));
            int buyOpenOrder = (int)openOrders.values().stream().filter(o -> o.getType().equals("buy")).count();
            synchronized (lock) {
                int vailidCount = 0;
                TradingUtil.updateStrategies(strategyDao, ruleDao, settingDao);
                LOGGER.info(String.format("Updated trading strategies for %d stocks", strategies.size()));
                for(Map.Entry<String, Strategy> entry : strategies.entrySet()) {
                    String stock = entry.getKey();
                    //Don't trade stock if it has an open order
                    if(openOrders.containsKey(stock)) {
                        LOGGER.debug("Skip {} trading strategy since there is an open order", stock);
                        continue;
                    }
                    //Don't trade stock if the time series is missing
                    if(!MarketDataMonitor.timeSeriesMap.containsKey(stock)) {
                        LOGGER.debug("Skip {} trading strategy since no time series", stock);
                        continue;
                    }
                    //Don't trade if time series is not long enough
                    if(MarketDataMonitor.timeSeriesMap.get(stock).getBarCount() < minLength) {
                        LOGGER.debug("Skip {} trading strategy since time series is not long enough", stock);
                        continue;
                    }
                    //Don't trade if the last update time is too far
                    if(new Date().getTime() / 1000 - MarketDataMonitor.timeSeriesMap.get(stock).getLastBar().getEndTime().toEpochSecond() > getInterval() * 3) {
                        LOGGER.debug("Skip {} trading strategy since time series is not update to date {} {}", stock, new Date().getTime(),MarketDataMonitor.timeSeriesMap.get(stock).getLastBar().getEndTime().toEpochSecond() );
                        continue;
                    }
                    vailidCount ++;

                    //Check if buy satisfied
                    if(MarketDataMonitor.isMarketOpen()) {
                        TradingRecord tradingRecord = new BaseTradingRecord();
                        Double currentPrice = MarketDataMonitor.timeSeriesMap.get(stock).getLastBar().getClosePrice().doubleValue();
                        if(positionMap.containsKey(stock)) {
                            //The buy timing is incorrect, don't use for anything
                            tradingRecord.enter(1, PrecisionNum.valueOf(positionMap.get(stock).getAverageCost()), PrecisionNum.valueOf(positionMap.get(stock).getQuantity()));
                        }

                        if(entry.getValue().shouldEnter(MarketDataMonitor.timeSeriesMap.get(stock).getEndIndex())
                                && !positionMap.containsKey(stock)
                                && !openOrders.containsKey(stock)
                                && positionNum + buyOpenOrder < holdLimit) {
                            //buy strategy satisfy & no position & hold stock < limit
                            int buyQuantity = calculateBuyShares(buyLimit, currentPrice, account);
                            if(buyQuantity > 0) {
                                if(tradingService.postOrder(new com.ultrader.bot.model.Order("", stock, "buy", buyQuantity, currentPrice, "")) != null) {
                                    account.setBuyingPower(account.getBuyingPower() - currentPrice * buyQuantity);
                                    positionNum++;
                                    LOGGER.info(String.format("Buy %s %d shares at price %f.", stock, buyQuantity, currentPrice));
                                }
                            }
                        } else if (entry.getValue().shouldExit(MarketDataMonitor.timeSeriesMap.get(stock).getEndIndex(), tradingRecord) //Check if sell satisfied
                                && positionMap.containsKey(stock)
                                && positionMap.get(stock).getQuantity() > 0) {
                            //sell strategy satisfy & has position
                            if(tradingService.postOrder(new com.ultrader.bot.model.Order("", stock, "sell", positionMap.get(stock).getQuantity(), currentPrice, "")) != null) {
                                account.setBuyingPower(account.getBuyingPower() + currentPrice * positionMap.get(stock).getQuantity());
                                positionNum--;
                                LOGGER.info(String.format("Sell %s %d shares at price %f.", stock, positionMap.get(stock).getQuantity(), currentPrice));
                            }

                        }
                    }
                }
                LOGGER.info("Checked trading strategies for {} stocks", vailidCount);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to execute trading strategy.", e);
        }
    }

    private Map<String, Position> getAllPositions() throws RuntimeException {
        Map<String, Position> positionMap = tradingService.getAllPositions();
        if(positionMap == null) {
            throw new RuntimeException("Cannot get position info, skip executing trading strategies");
        }
        LOGGER.info(String.format("Found %d positions.", positionMap.size()));
        return positionMap;
    }

    private Account getAccount() throws RuntimeException{
        Account account = tradingService.getAccountInfo();
        if(account == null) {
            throw new RuntimeException("Cannot get account info, skip executing trading strategies");
        }
        if(account.isTradingBlocked()) {
            throw new RuntimeException("Your api account is blocked trading, please check the trading platform account.");
        }
        return account;
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
            quantity = 0;
        }
        return quantity;
    }
}
