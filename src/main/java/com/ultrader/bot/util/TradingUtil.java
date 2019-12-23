package com.ultrader.bot.util;

import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.dao.StrategyDao;
import com.ultrader.bot.model.BackTestingResult;
import com.ultrader.bot.model.StrategyParameter;

import com.ultrader.bot.monitor.TradingAccountMonitor;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.*;
import org.ta4j.core.analysis.CashFlow;
import org.ta4j.core.analysis.criteria.AverageProfitableTradesCriterion;
import org.ta4j.core.analysis.criteria.BuyAndHoldCriterion;
import org.ta4j.core.analysis.criteria.RewardRiskRatioCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;


import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Strategy & Rule Util
 *
 * @author ytx1991
 */
public class TradingUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradingUtil.class);
    public static final String TIME_ZONE = "America/New_York";
    public static final String INDICATOR_DELIMITER = ":";
    public static final String TIME_SERIES = "TimeSeries";
    public static final String CLOSE_PRICE = "ClosePrice";
    public static final String ORDER_TYPE = "OrderType";
    public static final String NUMBER = "Number";
    public static final String BOOLEAN = "Boolean";
    public static final String INTEGER = "Integer";
    public static final String DOUBLE = "Double";
    public static final String NUM_INDICATOR = "NumIndicator";
    public static final String BOOLEAN_INDICATOR = "BooleanIndicator";

    public static org.ta4j.core.Rule generateTradingStrategy(
            StrategyDao strategyDao,
            RuleDao ruleDao,
            long strategyId,
            TimeSeries stock,
            Queue<StrategyParameter> parameters,
            boolean backfill) {
        return generateTradingStrategy(strategyDao, ruleDao, strategyId, stock, parameters, backfill, null, null);
    }

    public static String printSatisfaction(StrategyDao strategyDao,
                                         RuleDao ruleDao,
                                         long strategyId,
                                         TimeSeries stock,
                                         TradingRecord tradingRecord) {
        StringBuilder sb = new StringBuilder();
        generateTradingStrategy(strategyDao, ruleDao, strategyId, stock, null, false, tradingRecord, sb);
        return sb.toString();
    }

    /**
     * Generate trading strategy
     *
     * @param strategyDao
     * @param ruleDao
     * @param strategyId
     * @param stock
     * @param parameters  parameter overrides
     * @param backfill    fill the stored parameters to the "parameters"
     * @return
     */
    public static org.ta4j.core.Rule generateTradingStrategy(
            StrategyDao strategyDao,
            RuleDao ruleDao,
            long strategyId,
            TimeSeries stock,
            Queue<StrategyParameter> parameters,
            boolean backfill,
            TradingRecord tradingRecord,
            StringBuilder printSatisfy) {
        try {
            Validate.notNull(strategyDao, "strategyDao is required");
            Validate.notNull(ruleDao, "ruleDao is required");
            String strategyExp = strategyDao.findById(strategyId).map(s -> s.getFormula()).orElse(null);
            Validate.notEmpty(strategyExp, "Cannot find strategy id: " + strategyId);
            List<String> path = new ArrayList<>();
            path.add(strategyDao.findById(strategyId).get().getName());
            Rule rules = null;
            String logicOperator = "";
            String[] ruleStr = strategyExp.split(SettingConstant.DELIMITER);
            for (String str : ruleStr) {
                Rule newRule = null;
                if (str.indexOf("S") == 0) {
                    //This is a sub strategy, generate it recursively
                    long subStrategyId = Long.parseLong(str.replaceAll("\\D+", ""));
                    newRule = generateTradingStrategy(strategyDao, ruleDao, subStrategyId, stock, parameters, backfill, tradingRecord, printSatisfy);
                } else {
                    long ruleId = Long.parseLong(str.replaceAll("\\D+", ""));
                    com.ultrader.bot.model.Rule rule = ruleDao.findById(ruleId).get();
                    path.add(rule.getName());
                    newRule = generateTradingRule(ruleDao, ruleId, stock, parameters, backfill, path);
                    path.remove(path.size() - 1);
                    if (printSatisfy != null) {
                        Boolean isSatisfied = newRule.isSatisfied(stock.getEndIndex(), tradingRecord);
                        LOGGER.info("Stock:{}, Rid:{},  Name:{}, Satisfied:{}", stock.getName(), ruleId, rule.getName(), isSatisfied);
                        printSatisfy.append(rule.getName() + " : " + isSatisfied + ", ");
                    }
                }


                if (rules == null) {
                    rules = newRule;
                    logicOperator = str.substring(str.length() - 1);
                } else {
                    if (!logicOperator.equals("&") && !logicOperator.equals("|") && !logicOperator.equals("^")) {
                        LOGGER.error("Invalid strategy expression!");
                        return null;
                    } else {
                        switch (logicOperator) {
                            case "&":
                                rules = rules.and(newRule);
                                break;
                            case "|":
                                rules = rules.or(newRule);
                                break;
                            case "^":
                                rules = rules.xor(newRule);
                                break;
                        }
                        logicOperator = str.substring(str.length() - 1);
                    }
                }
            }
            return rules;
        } catch (Exception e) {
            LOGGER.error(String.format("Generate trading strategy %d error!", strategyId), e);
            return null;
        }
    }

    /**
     * Get trading rule class
     *
     * @param type
     * @return
     */
    public static Class<?> getRuleClass(String type) throws ClassNotFoundException {
        try {
            Class<?> rule = Class.forName("org.ta4j.core.trading.rules." + type);
            return rule;
        } catch (ClassNotFoundException e) {
            //Not original rule, check if it's Ultrader rule
            try {
                Class<?> rule = Class.forName("com.ultrader.bot.rule." + type);
                return rule;
            } catch (ClassNotFoundException e1) {
                LOGGER.error("Cannot recognize rule type {}", type);
                throw e1;
            }
        }
    }

    /**
     * Generate trading rule
     *
     * @param ruleDao
     * @param rid
     * @param stock
     * @return
     * @throws Exception
     */
    public static Rule generateTradingRule(RuleDao ruleDao, Long rid, TimeSeries stock, Queue<StrategyParameter> parameters, boolean backfill, List<String> path) throws Exception {
        String ruleExp = ruleDao.findById(rid).map(r -> r.getFormula()).orElse(null);
        String ruleType = ruleDao.findById(rid).map(r -> r.getType()).orElse(null);
        Validate.notEmpty(ruleExp, "Cannot find ruleExp for rule id " + rid);
        String[] args = ruleExp.split(",");
        //Generate rule
        try {
            Class<?> rule = getRuleClass(ruleType);
            Constructor<?> constructor = rule.getConstructor(getArgClasses(args));
            return (Rule) constructor.newInstance(getArgs(args, stock, parameters, backfill, path));
        } catch (Exception e) {
            LOGGER.error(String.format("Generate rule %s, type %s failed", ruleExp, ruleType), e);
            throw e;
        }
    }

    /**
     * Create rule based on inputs
     *
     * @param args
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?>[] getArgClasses(String[] args) throws ClassNotFoundException {
        Class<?>[] classes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            try {
                String argType = args[i].split(INDICATOR_DELIMITER)[0];
                if (IndicatorType.parentClassMap.containsKey(argType)) {
                    //Arg is an indicator
                    if (IndicatorType.parentClassMap.get(argType).equals(NUM_INDICATOR)) {
                        classes[i] = Class.forName("org.ta4j.core.Indicator");
                    } else if (IndicatorType.parentClassMap.get(argType).equals(BOOLEAN_INDICATOR)) {
                        classes[i] = Class.forName("org.ta4j.core.Indicator");
                    } else {
                        LOGGER.error("Invalid indicator type: " + argType);
                        throw new IllegalArgumentException("Invalid indicator type: " + argType);
                    }
                } else if (argType.equals(NUMBER)) {
                    //Arg is a number
                    classes[i] = Num.class;
                } else if (argType.equals(INTEGER)) {
                    classes[i] = int.class;
                } else if (argType.equals(BOOLEAN)) {
                    classes[i] = boolean.class;
                } else if (argType.equals(DOUBLE)) {
                    classes[i] = double.class;
                } else if (argType.equals(CLOSE_PRICE)) {
                    classes[i] = ClosePriceIndicator.class;
                } else if (argType.equals(ORDER_TYPE)) {
                    classes[i] = Order.OrderType.class;
                } else {
                    //Unknown arg type
                    LOGGER.error("Invalid arg type: " + argType);
                    throw new IllegalArgumentException("Invalid arg type: " + argType);
                }

            } catch (Exception e) {
                LOGGER.error(String.format("Get arg %s class failed.", args[i]), e);
                throw e;
            }
        }
        return classes;
    }

    /**
     * Create indicators based on inputs
     *
     * @param args
     * @param stock
     * @return
     * @throws Exception
     */
    public static Object[] getArgs(String[] args, TimeSeries stock, Queue<StrategyParameter> parameters, boolean backfill, List<String> path) throws Exception {
        Object[] values = new Object[args.length];
        for (int i = 0; i < values.length; i++) {
            try {
                String[] argStr = args[i].split(INDICATOR_DELIMITER);
                String argType = argStr[0];
                if (IndicatorType.parentClassMap.containsKey(argType)) {
                    //Arg is an indicator
                    Class<?> argument = Class.forName("org.ta4j.core.indicators." + argType);
                    Class<?>[] valueClasses = new Class<?>[argStr.length - 1];
                    for (int j = 1; j < argStr.length; j++) {
                        if (argStr[j].equals(TIME_SERIES)) {
                            //Indicator value is time series
                            valueClasses[j - 1] = TimeSeries.class;
                        } else if (argStr[j].equals(CLOSE_PRICE)) {
                            //Indicator value is close price indicator
                            valueClasses[j - 1] = Indicator.class;
                        } else if (argStr[j].indexOf(".") >= 0) {
                            valueClasses[j - 1] = double.class;

                        } else {
                            //Indicator value is an integer
                            valueClasses[j - 1] = int.class;
                        }
                    }
                    Constructor<?> constructor = argument.getConstructor(valueClasses);
                    Object[] argValues = new Object[argStr.length - 1];
                    for (int j = 1; j < argStr.length; j++) {
                        if (argStr[j].equals(TIME_SERIES)) {
                            //Indicator value is time series
                            if (stock == null) {
                                LOGGER.error(String.format("Missing stock time series when generating the rule"));
                            }
                            argValues[j - 1] = stock;
                        } else if (argStr[j].equals(CLOSE_PRICE)) {
                            //Indicator value is close price indicator
                            if (stock == null) {
                                LOGGER.error(String.format("Missing stock time series when generating the rule"));
                            }
                            argValues[j - 1] = new ClosePriceIndicator(stock);
                        } else if (argStr[j].indexOf(".") >= 0) {
                            if (parameters != null) {
                                path.add(argStr[0]);
                                if (backfill) {
                                    argValues[j - 1] = Double.parseDouble(argStr[j]);
                                    parameters.offer(new StrategyParameter(path.toString(), Double.parseDouble(argStr[j])));
                                    LOGGER.debug("Push {}: {}", path.toString(), Double.parseDouble(argStr[j]));
                                } else {
                                    //Use input parameters
                                    LOGGER.debug("Pop {}: {}", path.toString(), parameters.peek());
                                    argValues[j - 1] = parameters.poll().getValue();
                                }
                                path.remove(path.size() - 1);
                            } else {
                                //Use saved parameters
                                argValues[j - 1] = Double.parseDouble(argStr[j]);
                            }
                        } else {
                            //Indicator value is an integer
                            argValues[j - 1] = Integer.parseInt(argStr[j]);
                        }
                    }
                    values[i] = constructor.newInstance(argValues);
                } else if (argType.equals(NUMBER)) {
                    //Arg is a number
                    if (parameters != null) {
                        path.add(argStr[0]);
                        if (backfill) {
                            //Use saved parameters
                            values[i] = PrecisionNum.valueOf(argStr[1]);
                            parameters.offer(new StrategyParameter(path.toString(), Double.valueOf(argStr[1])));
                            LOGGER.debug("Push {}: {}", path.toString(), Double.parseDouble(argStr[1]));
                        } else {
                            //Use input parameters
                            LOGGER.debug("Pop {}: {}", path, parameters.peek());
                            values[i] = PrecisionNum.valueOf(parameters.poll().getValue());
                        }
                        path.remove(path.size() - 1);
                    } else {
                        //Use saved parameters
                        values[i] = PrecisionNum.valueOf(argStr[1]);
                    }
                } else if (argType.equals(INTEGER)) {
                    values[i] = Integer.parseInt(argStr[1]);
                } else if (argType.equals(DOUBLE)) {
                    values[i] = Double.parseDouble(argStr[1]);
                } else if (argType.equals(BOOLEAN)) {
                    values[i] = Boolean.parseBoolean(argStr[1]);
                } else if (argType.equals(CLOSE_PRICE)) {
                    values[i] = new ClosePriceIndicator(stock);
                } else if (argType.equals(ORDER_TYPE)) {
                    values[i] = Order.OrderType.valueOf(argStr[1]);
                } else {
                    //Unknown arg type
                    LOGGER.error("Invalid arg type: " + argType);
                }
            } catch (Exception e) {
                LOGGER.error(String.format("Generate arg %s failed.", args[i]), e);
                throw e;
            }
        }
        return values;
    }


    /**
     * Translate args to string
     *
     * @param args
     * @return
     */
    public static String translateToString(String... args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i == 0) {
                sb.append(args[i]);
            } else {
                sb.append("|" + args[i]);
            }
        }
        return sb.toString();
    }

    /**
     * Back test an asset
     *
     * @param series
     * @param buyStrategyId
     * @param sellStrategyId
     * @param strategyDao
     * @param ruleDao
     * @return
     */
    public static BackTestingResult backTest(TimeSeries series, int buyStrategyId, int sellStrategyId, StrategyDao strategyDao, RuleDao ruleDao, List<Double> parameters) {
        try {
            Queue<StrategyParameter> parameterQueue = null;
            if (parameters != null) {
                Queue<StrategyParameter> queue = new LinkedList<StrategyParameter>();
                parameters.stream().forEach(p -> queue.offer(new StrategyParameter("", p)));
                parameterQueue = queue;
            }

            TimeSeriesManager manager = new TimeSeriesManager(series);
            //Get strategy
            org.ta4j.core.Strategy strategy = new BaseStrategy(series.getName(),
                    TradingUtil.generateTradingStrategy(strategyDao, ruleDao, buyStrategyId, series, parameterQueue, false),
                    TradingUtil.generateTradingStrategy(strategyDao, ruleDao, sellStrategyId, series, parameterQueue, false));

            strategy.setUnstablePeriod(20);
            TradingRecord tradingRecord = manager.run(strategy);
            // Getting the cash flow of the resulting trades
            CashFlow cashFlow = new CashFlow(series, tradingRecord);
            // Getting the profitable trades ratio
            AnalysisCriterion profitTradesRatioCriterion = new AverageProfitableTradesCriterion();
            double profitTradesRatio = profitTradesRatioCriterion.calculate(series, tradingRecord).doubleValue();

            // Getting the reward-risk ratio
            AnalysisCriterion rewardRiskRatioCriterion = new RewardRiskRatioCriterion();
            double rewardRiskRatio = rewardRiskRatioCriterion.calculate(series, tradingRecord).doubleValue();
            // Total profit of our strategy
            TotalProfitCriterion totalProfitCriterion = new TotalProfitCriterion();
            double totalProfit = totalProfitCriterion.calculate(series, tradingRecord).doubleValue();
            // buy and hold profit
            AnalysisCriterion vsBuyAndHoldCriterion = new BuyAndHoldCriterion();
            double buyAndHold = vsBuyAndHoldCriterion.calculate(series, tradingRecord).doubleValue();
            double holdingDays = 0.0;
            for (Trade trade : tradingRecord.getTrades()) {
                holdingDays += (series.getBar(trade.getExit().getIndex()).getEndTime().toEpochSecond()
                        - series.getBar(trade.getEntry().getIndex()).getEndTime().toEpochSecond()) / 24 / 3600;
            }
            return new BackTestingResult(
                    series.getName(),
                    tradingRecord.getTradeCount(),
                    profitTradesRatio,
                    rewardRiskRatio,
                    buyAndHold - 1,
                    totalProfit - 1,
                    holdingDays / tradingRecord.getTrades().size(),
                    series.getBar(0).getBeginTime().toLocalDateTime(),
                    series.getLastBar().getEndTime().toLocalDateTime());
        } catch (Exception e) {
            LOGGER.error("Back test {} failed.",
                    series.getName(),
                    e);
            return null;
        }
    }

    /**
     * Extract float parameters from trading strategy
     *
     * @param strategyDao
     * @param ruleDao
     * @param buyStrategyId
     * @param sellStrategyId
     * @return
     */
    public static List<StrategyParameter> extractParameters(StrategyDao strategyDao, RuleDao ruleDao, long buyStrategyId, long sellStrategyId) {
        Queue<StrategyParameter> parameterQueue = new LinkedList<>();
        //Extract parameters from buy strategy first, the order does matter.
        generateTradingStrategy(strategyDao, ruleDao, buyStrategyId, new BaseTimeSeries(), parameterQueue, true);
        generateTradingStrategy(strategyDao, ruleDao, sellStrategyId, new BaseTimeSeries(), parameterQueue, true);
        List<StrategyParameter> parameters = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder(String.format("Find parameters in strategy %d, %d:", buyStrategyId, sellStrategyId));
        while (!parameterQueue.isEmpty()) {
            parameters.add(parameterQueue.poll());
            stringBuilder.append(" " + parameters.get(parameters.size() - 1));
        }
        LOGGER.info(stringBuilder.toString());
        return parameters;
    }
}
