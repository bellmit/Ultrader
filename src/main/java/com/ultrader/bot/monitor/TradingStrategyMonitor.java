package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.ConditionalSettingDao;
import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.dao.StrategyDao;

import com.ultrader.bot.model.Account;
import com.ultrader.bot.model.ConditionalSetting;
import com.ultrader.bot.model.MarketInfo;
import com.ultrader.bot.model.Position;
import com.ultrader.bot.model.Setting;
import com.ultrader.bot.service.NotificationService;
import com.ultrader.bot.service.TradingService;
import com.ultrader.bot.util.*;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.*;
import org.ta4j.core.num.PrecisionNum;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Order strategy monitor
 *
 * @author ytx1991
 */
public class TradingStrategyMonitor extends Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradingStrategyMonitor.class);
    private static TradingStrategyMonitor singleton_instance = null;
    private static Map<String, com.ultrader.bot.model.Order> openOrders = new HashMap<>();
    private static final Map<String, Integer> dayTradeCount = new HashMap<>();
    private static Object lock = new Object();
    private final SettingDao settingDao;
    private final StrategyDao strategyDao;
    private final RuleDao ruleDao;
    private final ConditionalSettingDao conditionalSettingDao;
    private final TradingService tradingService;
    private final NotificationService notifier;

    private TradingStrategyMonitor(long interval,
                                   final TradingService tradingService,
                                   final SettingDao settingDao,
                                   final ConditionalSettingDao conditionalSettingDao,
                                   final StrategyDao strategyDao,
                                   final RuleDao ruleDao,
                                   final NotificationService notifier) {
        super(interval);
        Validate.notNull(tradingService, "tradingService is required");
        Validate.notNull(settingDao, "settingDao is required");
        Validate.notNull(conditionalSettingDao, "conditionalSettingDao is required");
        Validate.notNull(strategyDao, "strategyDao is required");
        Validate.notNull(ruleDao, "ruleDao is required");
        Validate.notNull(notifier, "notifier is required");

        this.settingDao = settingDao;
        this.conditionalSettingDao = conditionalSettingDao;
        this.strategyDao = strategyDao;
        this.ruleDao = ruleDao;
        this.tradingService = tradingService;
        this.notifier = notifier;
    }

    public static void init(long interval, final TradingService tradingService, final SettingDao settingDao, final ConditionalSettingDao conditionalSettingDao, final StrategyDao strategyDao, final RuleDao ruleDao, final NotificationService notifier) {
        singleton_instance = new TradingStrategyMonitor(interval, tradingService, settingDao, conditionalSettingDao, strategyDao, ruleDao, notifier);
    }

    public static TradingStrategyMonitor getInstance() throws IllegalAccessException {
        if (singleton_instance == null) {
            LOGGER.error("Instance is not initialized.");
            throw new IllegalAccessException("Instance is not initialized.");
        }
        return singleton_instance;
    }

    @Override
    synchronized void scan() {
        try {

            if (!LicenseMonitor.getInstance().isValidLicense()) {
                LOGGER.error("Invalid license or expired license");
                return;
            }
            if (!Boolean.parseBoolean(RepositoryUtil.getSetting(settingDao, SettingConstant.AUTO_TRADING_ENABLE.getName(), "true"))) {
                LOGGER.info("Auto trading disabled");
                return;
            }
            MarketInfo marketInfo = tradingService.getMarketInfo();
            Account account = tradingService.getAccountInfo();
            Map<String, Position> positions = TradingAccountMonitor.getPositions();
            LOGGER.info("Execute trading strategy.");

            //Update trading settings based on market trend
            marketAdaptation();
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

            int validCount = 0;
            int noTimeSeries = 0;
            int notLongEnough = 0;
            int notNewEnough = 0;
            //update trading strategy
            Long buyStrategyId = Long.parseLong(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_BUY_STRATEGY.getName(), "-1"));
            Long sellStrategyId = Long.parseLong(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_SELL_STRATEGY.getName(), "-1"));
            int sellCount = 0;
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
                    if (marketInfo.getIsOpen()) {
                        //If it is trading time, reset the times series
                        TimeSeries newTimeSeries = new BaseTimeSeries(timeSeries.getName());
                        newTimeSeries.setMaximumBarCount(timeSeries.getMaximumBarCount());
                        timeSeries = newTimeSeries;
                    }
                    continue;
                }
                validCount++;

                //Check if buy satisfied
                if (MarketDataMonitor.isMarketOpen() && marketInfo.getNextCloseDate().getTime() - marketInfo.getTimestamp().getTime() > tradeInterval * 1000) {
                    //Generate strategy
                    Strategy strategy = new BaseStrategy(stock,
                            TradingUtil.generateTradingStrategy(strategyDao, ruleDao, buyStrategyId, timeSeries, null, false),
                            TradingUtil.generateTradingStrategy(strategyDao, ruleDao, sellStrategyId, timeSeries, null, false));
                    Double currentPrice = timeSeries.getLastBar().getClosePrice().doubleValue();
                    TradingRecord tradingRecord = new BaseTradingRecord();
                    if (positions.containsKey(stock)) {
                        //If the actual buy date is during the time series, set as the right index
                        //Otherwise set the first index
                        ZonedDateTime buyDate = ZonedDateTime.ofInstant(positions.get(stock).getBuyDate().toInstant(), ZoneId.of(TradingUtil.TIME_ZONE));
                        for (int i = timeSeries.getBeginIndex(); i <= timeSeries.getEndIndex(); i++) {
                            if (timeSeries.getBar(i).getEndTime().isAfter(buyDate)) {
                                tradingRecord.enter(i, PrecisionNum.valueOf(positions.get(stock).getAverageCost()), PrecisionNum.valueOf(positions.get(stock).getQuantity()));
                                LOGGER.debug("Stock {}, Buy Date {}, set index {}, {}", stock, buyDate, i, timeSeries.getBar(i).getEndTime());
                                break;
                            }
                        }
                        if (timeSeries.getLastBar().getEndTime().isBefore(buyDate)) {
                            tradingRecord.enter(timeSeries.getEndIndex(), PrecisionNum.valueOf(positions.get(stock).getAverageCost()), PrecisionNum.valueOf(positions.get(stock).getQuantity()));
                        }
                        //TradingUtil.printSatisfaction(strategyDao, ruleDao, sellStrategyId, timeSeries, tradingRecord);
                    }
                    try {
                        if (!dayTradeCount.containsKey(stock)
                                && !positions.containsKey(stock)
                                && !openOrders.containsKey(stock)
                                && positionNum + buyOpenOrder < holdLimit
                                && account.getPortfolioValue() > account.getInitialMargin()
                                && strategy.shouldEnter(timeSeries.getEndIndex())) {
                            LOGGER.info(String.format("%s buy strategy satisfied. ", stock));
                            String reason = TradingUtil.printSatisfaction(strategyDao, ruleDao, buyStrategyId, timeSeries, null);
                            //buy strategy satisfy & no position & hold stock < limit
                            int buyQuantity = calculateBuyShares(
                                    buyLimit,
                                    currentPrice,
                                    account,
                                    Boolean.parseBoolean(RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_USE_MARGIN.getName(), "false")),
                                    Boolean.parseBoolean(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_INTRADAY_TRADING.getName(), "false")));
                            if (buyQuantity > 0) {
                                LOGGER.info(String.format("Buy %s %d shares at price %f.", stock, buyQuantity, currentPrice));
                                if (tradingService.postOrder(new com.ultrader.bot.model.Order("", stock, "buy", buyOrderType, buyQuantity, currentPrice, "", null, reason, MarketDataUtil.getExchangeBySymbol(stock))) != null) {
                                    account.setBuyingPower(account.getBuyingPower() - currentPrice * buyQuantity);
                                    positionNum++;
                                }
                            }
                        } else if (positions.containsKey(stock)
                                && positions.get(stock).getQuantity() > 0
                                && strategy.shouldExit(timeSeries.getEndIndex(), tradingRecord)) {
                            LOGGER.info(String.format("%s sell strategy satisfied.", stock));
                            String reason = TradingUtil.printSatisfaction(strategyDao, ruleDao, sellStrategyId, timeSeries, tradingRecord);
                            //sell strategy satisfy & has position
                            LOGGER.info(String.format("Sell %s %d shares at price %f.", stock, positions.get(stock).getQuantity(), currentPrice));
                            if (tradingService.postOrder(new com.ultrader.bot.model.Order("", stock, "sell", sellOrderType, positions.get(stock).getQuantity(), currentPrice, "", null, reason, MarketDataUtil.getExchangeBySymbol(stock))) != null) {
                                account.setBuyingPower(account.getBuyingPower() + currentPrice * positions.get(stock).getQuantity());
                                positionNum--;
                                sellCount++;
                                dayTradeCount.put(stock, 1);

                            }

                        }
                    } catch (Exception e) {
                        //Delete time series when it failed
                        LOGGER.error("Check {} failed, remove it to reload, {}", stock, e);
                        MarketDataMonitor.timeSeriesMap.remove(stock);
                    }

                }
            }
            //Check if it's time to cover margin.
            if ( RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_AUTO_COVER.getName(), "true").equals("true")
                    && marketInfo.getIsOpen()
                    && marketInfo.getNextCloseDate().getTime() - marketInfo.getTimestamp().getTime() < tradeInterval * 1000) {
                tradingService.sellForCoverMargin(account, positions.values());
            }
            LOGGER.info("Checked trading strategies for {} stocks, {} stocks no time series, {} stocks time series too short , {} stocks time series too old ",
                    validCount, noTimeSeries, notLongEnough, notNewEnough);
            checkMarketData(validCount, notNewEnough);
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

    public static Map<String, Integer> getDayTradeCount() {
        return dayTradeCount;
    }


    private void checkMarketData(int validCount, int notNewEnough) {
        if (MarketDataMonitor.timeSeriesMap.size() * 0.1 > validCount) {
            notifier.sendMarketDataStatus("error", validCount, validCount + notNewEnough);
        } else if (MarketDataMonitor.timeSeriesMap.size() * 0.5 > validCount) {
            notifier.sendMarketDataStatus("warning", validCount, validCount + notNewEnough);
        } else if (validCount == 0) {
            notifier.sendMarketDataStatus("error", validCount, validCount + notNewEnough);
        } else {
            notifier.sendMarketDataStatus("normal", validCount, validCount + notNewEnough);
        }

    }
    private void marketAdaptation() {
        String currentTrend = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_MARKET_TREND.getName(), "NORMAL");
        if (!currentTrend.equals(MarketDataMonitor.getMarketTrend().name())) {
            notifier.sendNotification("Market Trend Changed", String.format("Market trend changed from %s to %s.", currentTrend, MarketDataMonitor.getMarketTrend().name()), NotificationType.INFO);
            //Change trading setting
            List<ConditionalSetting> conditionalSettings = conditionalSettingDao.findByMarketTrend(MarketDataMonitor.getMarketTrend().name());
            List<ConditionalSetting> defaultSettings = conditionalSettingDao.findByMarketTrend(MarketTrend.NORMAL.name());
            //If default setting is empty, copy current settings as normal
            if (defaultSettings.size() == 0) {
                for (String settingName : ConditionalSetting.getSupportSettings()) {
                    Optional<Setting> setting = settingDao.findById(settingName);
                    if (setting.isPresent()) {
                        defaultSettings.add(new ConditionalSetting(null, MarketTrend.NORMAL.name(), settingName, setting.get().getValue()));
                    }
                }
                conditionalSettingDao.saveAll(defaultSettings);
            }
            //Merge settings
            List<Setting> changes = new ArrayList<>();
            for (ConditionalSetting defaultSetting : defaultSettings) {
                if (!ConditionalSetting.getSupportSettings().contains(defaultSetting.getSettingName())) {
                    //Unsupport setting
                    continue;
                }
                //Check if there is override
                Optional<ConditionalSetting> override = conditionalSettings.stream().filter(c -> c.getSettingName().equals(defaultSetting.getSettingName())).findFirst();
                if (override.isPresent()) {
                    changes.add(new Setting(override.get().getSettingName(), override.get().getSettingValue()));
                } else {
                    changes.add(new Setting(defaultSetting.getSettingName(), defaultSetting.getSettingValue()));
                }
            }
            changes.add(new Setting(SettingConstant.TRADE_MARKET_TREND.getName(), MarketDataMonitor.getMarketTrend().name()));
            settingDao.saveAll(changes);
        }
    }

    private static int calculateBuyShares(String limit, double price, Account account, Boolean useMargin, Boolean intradayTrade) {
        double amount;
        if (limit.indexOf("%") < 0) {
            amount = Double.parseDouble(limit);
        } else {
            if (useMargin && intradayTrade && account.isDayTrader()) {
                amount = account.getPortfolioValue() * 4 * Double.parseDouble(limit.substring(0, limit.length() - 1)) / 100;
            } else if (useMargin) {
                amount = account.getPortfolioValue() * 2 * Double.parseDouble(limit.substring(0, limit.length() - 1)) / 100;
            } else {
                amount = account.getPortfolioValue() * Double.parseDouble(limit.substring(0, limit.length() - 1)) / 100;
            }
        }
        int quantity = (int) (Math.floor(amount / price));
        if (account.getDayTradeBuyingPower() < amount || account.getBuyingPower() < amount) {
            quantity = 0;
        }
        return quantity;
    }
}
