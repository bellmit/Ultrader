package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.dao.StrategyDao;

import com.ultrader.bot.model.Account;
import com.ultrader.bot.model.Position;
import com.ultrader.bot.model.websocket.StatusMessage;
import com.ultrader.bot.service.TradingService;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingUtil;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.ta4j.core.*;
import org.ta4j.core.num.PrecisionNum;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Order strategy monitor
 *
 * @author ytx1991
 */
public class TradingStrategyMonitor extends Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradingStrategyMonitor.class);
    private static TradingStrategyMonitor singleton_instance = null;
    private static Map<String, com.ultrader.bot.model.Order> openOrders = new HashMap<>();
    private static Object lock = new Object();
    private final SettingDao settingDao;
    private final StrategyDao strategyDao;
    private final RuleDao ruleDao;
    private final TradingService tradingService;
    private final SimpMessagingTemplate notifier;

    private TradingStrategyMonitor(long interval, final TradingService tradingService, final SettingDao settingDao, final StrategyDao strategyDao, final RuleDao ruleDao, final SimpMessagingTemplate notifier) {
        super(interval);
        Validate.notNull(tradingService, "tradingService is required");
        Validate.notNull(settingDao, "settingDao is required");
        Validate.notNull(strategyDao, "strategyDao is required");
        Validate.notNull(ruleDao, "ruleDao is required");
        Validate.notNull(notifier, "notifier is required");

        this.settingDao = settingDao;
        this.strategyDao = strategyDao;
        this.ruleDao = ruleDao;
        this.tradingService = tradingService;
        this.notifier = notifier;
    }

    public static void init(long interval, final TradingService tradingService, SettingDao settingDao, StrategyDao strategyDao, RuleDao ruleDao, SimpMessagingTemplate notifier) {
        singleton_instance = new TradingStrategyMonitor(interval, tradingService, settingDao, strategyDao, ruleDao, notifier);
    }

    public static TradingStrategyMonitor getInstance() throws IllegalAccessException {
        if (singleton_instance == null) {
            LOGGER.error("Instance is not initialized.");
            throw new IllegalAccessException("Instance is not initialized.");
        }
        return singleton_instance;
    }

    @Override
    void scan() {
        try {

            if (!LicenseMonitor.getInstance().isValidLicense()) {
                LOGGER.error("Invalid license or expired license");
                return;
            }
            if (!Boolean.parseBoolean(RepositoryUtil.getSetting(settingDao, SettingConstant.AUTO_TRADING_ENABLE.getName(), "true"))) {
                LOGGER.info("Auto trading disabled");
                return;
            }

            Account account = tradingService.getAccountInfo();
            Map<String, Position> positions = TradingAccountMonitor.getPositions();
            LOGGER.info("Execute trading strategy.");

            //Get open orders
            openOrders = tradingService.getOpenOrders();
            String buyLimit = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_BUY_MAX_LIMIT.getName(), "1%");
            int holdLimit = Integer.parseInt(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_BUY_HOLDING_LIMIT.getName(), "0"));
            holdLimit = holdLimit == 0 ? Integer.MAX_VALUE : holdLimit;
            int positionNum = positions.size();
            int minLength = Integer.parseInt(RepositoryUtil.getSetting(settingDao, SettingConstant.INDICATOR_MAX_LENGTH.getName(), "50"));
            int buyOpenOrder = (int) openOrders.values().stream().filter(o -> o.getSide().equals("buy")).count();
            String buyOrderType = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_BUY_ORDER_TYPE.getName(), "market");
            String sellOrderType = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_SELL_ORDER_TYPE.getName(), "market");
            int tradeInterval = Integer.parseInt(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_PERIOD_SECOND.getName(), "60"));
            LOGGER.info("Buy order type {}, Sell order type {}", buyOrderType, sellOrderType);

            int vailidCount = 0;
            int noTimeSeries = 0;
            int notLongEnough = 0;
            int notNewEnough = 0;
            //update trading strategy
            Long buyStrategyId = Long.parseLong(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_BUY_STRATEGY.getName(), "-1"));
            Long sellStrategyId = Long.parseLong(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_SELL_STRATEGY.getName(), "-1"));
            for (Map.Entry<String, TimeSeries> entry : MarketDataMonitor.timeSeriesMap.entrySet()) {
                String stock = entry.getKey();
                TimeSeries timeSeries = entry.getValue();
                //Don't trade stock if it has an open order
                if (openOrders.containsKey(stock)) {
                    LOGGER.debug("Skip {} trading strategy since there is an open order", stock);
                    continue;
                }

                //Don't trade if time series is not long enough
                if (timeSeries.getBarCount() < minLength) {
                    LOGGER.debug("Skip {} trading strategy since time series is not long enough", stock);
                    notLongEnough++;
                    continue;
                }
                //Don't trade if the last update time is too far
                if (ZonedDateTime.now(ZoneId.of(TradingUtil.TIME_ZONE)).toEpochSecond() - timeSeries.getLastBar().getEndTime().toEpochSecond() > tradeInterval * 3) {
                    LOGGER.debug("Skip {} trading strategy since time series is not update to date {} {}", stock, new Date().getTime(), timeSeries.getLastBar().getEndTime().toEpochSecond());
                    notNewEnough++;
                    continue;
                }
                vailidCount++;
                //Check if buy satisfied
                if (MarketDataMonitor.isMarketOpen()) {
                    //Generate strategy
                    Strategy strategy = new BaseStrategy(stock,
                            TradingUtil.generateTradingStrategy(strategyDao, ruleDao, buyStrategyId, timeSeries, null, false),
                            TradingUtil.generateTradingStrategy(strategyDao, ruleDao, sellStrategyId, timeSeries, null, false));
                    TradingRecord tradingRecord = new BaseTradingRecord();
                    Double currentPrice = timeSeries.getLastBar().getClosePrice().doubleValue();
                    if (positions.containsKey(stock)) {
                        //The buy timing is incorrect, don't use for anything
                        tradingRecord.enter(1, PrecisionNum.valueOf(positions.get(stock).getAverageCost()), PrecisionNum.valueOf(positions.get(stock).getQuantity()));
                    }

                    try {
                        if (strategy.shouldEnter(timeSeries.getEndIndex())
                                && !positions.containsKey(stock)
                                && !openOrders.containsKey(stock)
                                && positionNum + buyOpenOrder < holdLimit) {
                            //buy strategy satisfy & no position & hold stock < limit
                            int buyQuantity = calculateBuyShares(buyLimit, currentPrice, account);
                            if (buyQuantity > 0) {
                                if (tradingService.postOrder(new com.ultrader.bot.model.Order("", stock, "buy", buyOrderType, buyQuantity, currentPrice, "", null)) != null) {
                                    account.setBuyingPower(account.getBuyingPower() - currentPrice * buyQuantity);
                                    positionNum++;
                                    LOGGER.info(String.format("Buy %s %d shares at price %f.", stock, buyQuantity, currentPrice));
                                }
                            }
                        } else if (strategy.shouldExit(timeSeries.getEndIndex(), tradingRecord) //Check if sell satisfied
                                && positions.containsKey(stock)

                                && positions.get(stock).getQuantity() > 0) {
                            //sell strategy satisfy & has position
                            if (tradingService.postOrder(new com.ultrader.bot.model.Order("", stock, "sell", sellOrderType, positions.get(stock).getQuantity(), currentPrice, "", null)) != null) {
                                account.setBuyingPower(account.getBuyingPower() + currentPrice * positions.get(stock).getQuantity());
                                positionNum--;
                                LOGGER.info(String.format("Sell %s %d shares at price %f.", stock, positions.get(stock).getQuantity(), currentPrice));
                            }

                        }
                    } catch (Exception e) {
                        //Delete time series when it failed
                        LOGGER.error("Check {} failed, remove it to reload, {}", stock, e);
                        MarketDataMonitor.timeSeriesMap.remove(stock);
                    }

                }
            }
            LOGGER.info("Checked trading strategies for {} stocks, {} stocks no time series, {} stocks time series too short , {} stocks time series too old ",
                    vailidCount, noTimeSeries, notLongEnough, notNewEnough);
            if (MarketDataMonitor.timeSeriesMap.size() * 0.1 > vailidCount) {
                notifier.convertAndSend("/topic/status/data", new StatusMessage("error", "Most stocks don't have latest data"));
            } else if (MarketDataMonitor.timeSeriesMap.size() * 0.5 > vailidCount) {
                notifier.convertAndSend("/topic/status/data", new StatusMessage("warning", "Some stocks don't have latest data"));
            } else {
                notifier.convertAndSend("/topic/status/data", new StatusMessage("normal", "Most stocks have latest data"));
            }


        } catch (Exception e) {
            LOGGER.error("Failed to execute trading strategy.", e);
        }
    }

    public static Map<String, com.ultrader.bot.model.Order> getOpenOrders() {
        return openOrders;
    }

    public static Object getLock() {
        return lock;
    }


    private static int calculateBuyShares(String limit, double price, Account account) {
        double amount;
        if (limit.indexOf("%") < 0) {
            amount = Double.parseDouble(limit);
        } else {
            amount = account.getPortfolioValue() * Double.parseDouble(limit.substring(0, limit.length() - 1)) / 100;
        }
        int quantity = (int) (Math.floor(amount / price));
        if (account.getBuyingPower() < amount) {
            quantity = 0;
        }
        return quantity;
    }
}
