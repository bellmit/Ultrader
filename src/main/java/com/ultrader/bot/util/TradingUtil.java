package com.ultrader.bot.util;

import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.dao.StrategyDao;
import com.ultrader.bot.model.Setting;
import com.ultrader.bot.monitor.MarketDataMonitor;
import com.ultrader.bot.monitor.TradingStrategyMonitor;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.*;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.IsHighestRule;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Strategy & Rule Util
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
    public static org.ta4j.core.Rule generateTradingStrategy(StrategyDao strategyDao, RuleDao ruleDao, long strategyId, String stock) {
        try{
            Validate.notNull(strategyDao, "strategyDao is required");
            Validate.notNull(ruleDao, "ruleDao is required");
            String strategyExp = strategyDao.findById(strategyId).map(s -> s.getFormula()).orElse(null);
            Validate.notEmpty(strategyExp,"Cannot find strategy id: " + strategyId);

            Rule rules = null;
            String logicOperator = "";
            String[] ruleStr = strategyExp.split(SettingConstant.DELIMITER);
            for(String str : ruleStr) {
                long ruleId =Long.parseLong(str.replaceAll("\\D+",""));
                if(rules == null) {
                    rules = generateTradingRule(ruleDao, ruleId, stock);
                    logicOperator = str.substring(str.length()-1);
                } else {
                    if(!logicOperator.equals("&") && !logicOperator.equals("|") && !logicOperator.equals("^")) {
                        LOGGER.error("Invalid strategy expression!");
                        return null;
                    } else {
                        switch (logicOperator) {
                            case "&":
                                rules = rules.and(generateTradingRule(ruleDao, ruleId, stock));
                                break;
                            case "|":
                                rules = rules.or(generateTradingRule(ruleDao, ruleId, stock));
                                break;
                            case "^":
                                rules = rules.xor(generateTradingRule(ruleDao,  ruleId, stock));
                                break;
                        }
                        logicOperator = str.substring(str.length()-1);
                    }
                }
            }
            return rules;
        } catch (Exception e) {
            LOGGER.error(String.format("Generate trading strategy %d error!", strategyId), e);
            return null;
        }
    }

    public static Rule generateTradingRule(RuleDao ruleDao , Long rid, String stock) throws Exception {
        String ruleExp = ruleDao.findById(rid).map(r -> r.getFormula()).orElse(null);
        String ruleType = ruleDao.findById(rid).map(r -> r.getType()).orElse(null);
        Validate.notEmpty(ruleExp, "Cannot find ruleExp for rule id " + rid);
        String[] args = ruleExp.split(",");
        //Generate rule
        try {
            Class<?> rule = Class.forName("org.ta4j.core.trading.rules." + ruleType);
            Constructor<?> constructor = rule.getConstructor(getArgClasses(args));
            return (Rule) constructor.newInstance(getArgs(args, stock));
        } catch (Exception e) {
            LOGGER.error(String.format("Generate rule %s, type %s failed", ruleExp, ruleType), e);
            throw e;
        }
    }

    public static Class<?>[] getArgClasses(String[] args) throws ClassNotFoundException {
        Class<?>[] classes = new Class<?>[args.length];
        for (int i=0; i < args.length; i++) {
            try {
                String argType = args[i].split(INDICATOR_DELIMITER)[0];
                if (IndicatorType.parentClassMap.containsKey(argType)) {
                    //Arg is an indicator
                    if(IndicatorType.parentClassMap.get(argType).equals(NUM_INDICATOR)) {
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

    public static Object[] getArgs(String[] args, String stock) throws Exception {
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
                        if(argStr[j].equals(TIME_SERIES)) {
                            //Indicator value is time series
                            valueClasses[j-1] = TimeSeries.class;
                        } else if (argStr[j].equals(CLOSE_PRICE)) {
                            //Indicator value is close price indicator
                            valueClasses[j-1] = Indicator.class;
                        } else if (argStr[j].indexOf(".") >= 0) {
                            valueClasses[j-1] = double.class;
                        } else {
                            //Indicator value is an integer
                            valueClasses[j-1] = int.class;
                        }
                    }
                    Constructor<?> constructor = argument.getConstructor(valueClasses);
                    Object[] argValues = new Object[argStr.length - 1];
                    for (int j = 1; j < argStr.length; j++) {
                        if(argStr[j].equals(TIME_SERIES)) {
                            //Indicator value is time series
                            if(!MarketDataMonitor.timeSeriesMap.containsKey(stock)) {
                                LOGGER.error(String.format("Missing stock time series for %s when generating the rule", stock));
                            }
                            argValues[j-1] = MarketDataMonitor.timeSeriesMap.get(stock);
                        } else if (argStr[j].equals(CLOSE_PRICE)) {
                            //Indicator value is close price indicator
                            if(!MarketDataMonitor.timeSeriesMap.containsKey(stock)) {
                                LOGGER.error(String.format("Missing stock time series for %s when generating the rule", stock));
                            }
                            argValues[j-1] = new  ClosePriceIndicator(MarketDataMonitor.timeSeriesMap.get(stock));
                        } else if (argStr[j].indexOf(".") >= 0) {
                            argValues[j-1] = Double.parseDouble(argStr[j]);
                        } else {
                            //Indicator value is an integer
                            argValues[j-1] = Integer.parseInt(argStr[j]);
                        }
                    }
                    values[i] = constructor.newInstance(argValues);
                } else if (argType.equals(NUMBER)) {
                    //Arg is a number
                    values[i] = PrecisionNum.valueOf(argStr[1]);
                } else if (argType.equals(INTEGER)) {
                    values[i] = Integer.parseInt(argStr[1]);
                } else if (argType.equals(BOOLEAN)) {
                    values[i] = Boolean.parseBoolean(argStr[1]);
                } else if (argType.equals(CLOSE_PRICE)) {
                    values[i] = new ClosePriceIndicator(MarketDataMonitor.timeSeriesMap.get(stock));
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
     * Update trading strategies for each stock
     * This method must be used in synchronous block (TradingStrategyMonitor.lock)
     * @param strategyDao
     * @param ruleDao
     * @param settingDao
     * @return
     */
    public static void updateStrategies(StrategyDao strategyDao, RuleDao ruleDao, SettingDao settingDao) {
        Validate.notNull(strategyDao, "strategyDao is required");
        Validate.notNull(ruleDao, "ruleDao is required");
        Validate.notNull(settingDao, "settingDao is  required");

        try {
            //update trading strategy
            Long buyStrategyId = Long.parseLong(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_BUY_STRATEGY.getName(), "-1"));
            Long sellStrategyId = Long.parseLong(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_SELL_STRATEGY.getName(), "-1"));
            Map<String, Strategy> strategyMap = new HashMap<>();
            for(String stock : MarketDataMonitor.timeSeriesMap.keySet()) {
                if(!TradingStrategyMonitor.getStrategies().containsKey(stock)) {
                    //Add strategy for new stock
                    Rule buyRules = TradingUtil.generateTradingStrategy(strategyDao, ruleDao, buyStrategyId, stock);
                    Rule sellRules = TradingUtil.generateTradingStrategy(strategyDao, ruleDao, sellStrategyId, stock);
                    if(buyRules != null && sellRules != null) {
                        strategyMap.put(stock, new BaseStrategy(stock, buyRules, sellRules));
                    } else {
                        LOGGER.error("Missing trading rules.");
                    }
                } else {
                    //Use the exist strategy
                    strategyMap.put(stock, TradingStrategyMonitor.getStrategies().get(stock));
                }
            }
            TradingStrategyMonitor.setStrategies(strategyMap);
        } catch (Exception e) {
            LOGGER.error("Update trading strategies failed.", e);
        }

    }

    /**
     * Translate args to string
     * @param args
     * @return
     */
    public static String translateToString(String ... args) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            if(i == 0) {
                sb.append(args[i]);
            } else {
                sb.append("|" + args[i]);
            }
        }
        return sb.toString();
    }

}
