package com.ultrader.bot.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.HashMap;
import java.util.Map;

import static com.ultrader.bot.util.TradingUtil.*;

/**
 * Indicator Types
 * @author ytx1991
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum IndicatorType {

    AccelerationDecelerationIndicator("AccelerationDecelerationIndicator", "Acceleration Deceleration", new String[]{
            TIME_SERIES,
            TradingUtil.translateToString(TIME_SERIES, INTEGER, INTEGER)}),
    AroonDownIndicator("AroonDownIndicator", "Aroon Down", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)}),
    AroonUpIndicator("AroonUpIndicator", "Aroon Up", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)}),
    AroonOscillatorIndicator("AroonOscillatorIndicator", "Aroon Oscillator", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)}),
    ATRIndicator("ATRIndicator", "Average True Range", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)}),
    AwesomeOscillatorIndicator("AwesomeOscillatorIndicator", "Awesome Oscillator", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)}),
    CCIIndicator("CCIIndicator", "Commodity Channel Index", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)}),
    ChandelierExitLongIndicator("ChandelierExitLongIndicator", "Chandelier Exit Long", new String[]{TIME_SERIES, TradingUtil.translateToString(TIME_SERIES, INTEGER, DOUBLE)}),
    ChandelierExitShortIndicator("ChandelierExitShortIndicator", "Chandelier Exit Short", new String[]{TIME_SERIES, TradingUtil.translateToString(TIME_SERIES, INTEGER, DOUBLE)}),
    ChopIndicator("ChopIndicator", "Chop", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER, INTEGER)}),
    CMOIndicator("CMOIndicator", "Chande Momentum Oscillator", new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    CoppockCurveIndicator("CoppockCurveIndicator", "Coppock Curve", new String[]{CLOSE_PRICE, TradingUtil.translateToString(CLOSE_PRICE, INTEGER, INTEGER)}),
    DoubleEMAIndicator("DoubleEMAIndicator", "Double EMA", new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    DPOIndicator("DPOIndicator", "Detrended Price Oscillator", new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    EMAIndicator("EMAIndicator", "Exponential Moving Average", new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    FisherIndicator("FisherIndicator", "Fisher",  new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER), TradingUtil.translateToString(CLOSE_PRICE, INTEGER, DOUBLE, DOUBLE, DOUBLE, DOUBLE)}),
    HMAIndicator("HMAIndicator", "Hull Moving Average",  new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    KAMAIndicator("KAMAIndicator", " Kaufman's Adaptive Moving Average",  new String[]{CLOSE_PRICE, TradingUtil.translateToString(CLOSE_PRICE, INTEGER, INTEGER, INTEGER)}),
    MACDIndicator("MACDIndicator", "Moving Average Convergence Divergence", new String[]{CLOSE_PRICE, TradingUtil.translateToString(CLOSE_PRICE, INTEGER, INTEGER)}),
    MassIndexIndicator("MassIndexIndicator", "Mass Index", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER, INTEGER)}),
    MMAIndicator("MMAIndicator", "Modified Moving Average",  new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    PPOIndicator("PPOIndicator", "Percentage Price Oscillator", new String[]{CLOSE_PRICE, TradingUtil.translateToString(CLOSE_PRICE, INTEGER, INTEGER)}),
    RAVIIndicator("RAVIIndicator", "Chande's Range Action Verification Index", new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER, INTEGER)}),
    ROCIndicator("ROCIndicator", "Rate of Change",  new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    RSIIndicator("RSIIndicator", "Relative Strength Index",  new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    RWIHighIndicator("RWIHighIndicator", "Random Walk Index High", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)}),
    RWILowIndicator("RWILowIndicator", "Random Walk Index Low", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)}),
    SMAIndicator("SMAIndicator", "Simple Moving Average",  new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    StochasticOscillatorDIndicator("StochasticOscillatorDIndicator", "Stochastic Oscillator D", new String[]{CLOSE_PRICE}),
    StochasticOscillatorKIndicator("StochasticOscillatorKIndicator", "Stochastic Oscillator K", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)}),
    StochasticRSIIndicator("StochasticRSIIndicator", "Stochastic RSI", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)}),
    TripleEMAIndicator("TripleEMAIndicator", "Triple EMA",  new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    UlcerIndexIndicator("UlcerIndexIndicator", "Ulcer Index",  new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    WilliamsRIndicator("WilliamsRIndicator", "William's R", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)}),
    WMAIndicator("WMAIndicator", "WMA",  new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    ZLEMAIndicator("ZLEMAIndicator", "Zero-lag EMA",  new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    PercentBIndicator("bollinger.PercentBIndicator", "B Percent", new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER, DOUBLE)});
    private String classz;
    private String name;
    private String[] args;
    IndicatorType(String classz, String name, String[] args){
        this.classz = classz;
        this.name = name;
        this.args = args;
    }
    public static final Map<String, String> parentClassMap = new HashMap<>();

    public String getName() {
        return this.classz;
    }

    public String[] getArgs() {
        return this.args;
    }

    public String getClassz() {
        return this.classz;
    }
    static {
        parentClassMap.put("AccelerationDecelerationIndicator", NUM_INDICATOR);
        parentClassMap.put("AroonDownIndicator", NUM_INDICATOR);
        parentClassMap.put("AroonUpIndicator", NUM_INDICATOR);
        parentClassMap.put("AroonOscillatorIndicator", NUM_INDICATOR);
        parentClassMap.put("ATRIndicator", NUM_INDICATOR);
        parentClassMap.put("AwesomeOscillatorIndicator", NUM_INDICATOR);
        parentClassMap.put("CCIIndicator", NUM_INDICATOR);
        parentClassMap.put("ChandelierExitLongIndicator", NUM_INDICATOR);
        parentClassMap.put("ChandelierExitShortIndicator", NUM_INDICATOR);
        parentClassMap.put("ChopIndicator", NUM_INDICATOR);
        parentClassMap.put("CMOIndicator", NUM_INDICATOR);
        parentClassMap.put("CoppockCurveIndicator", NUM_INDICATOR);
        parentClassMap.put("DoubleEMAIndicator", NUM_INDICATOR);
        parentClassMap.put("DPOIndicator", NUM_INDICATOR);
        parentClassMap.put("EMAIndicator", NUM_INDICATOR);
        parentClassMap.put("FisherIndicator", NUM_INDICATOR);
        parentClassMap.put("HMAIndicator", NUM_INDICATOR);
        parentClassMap.put("KAMAIndicator", NUM_INDICATOR);
        parentClassMap.put("MACDIndicator", NUM_INDICATOR);
        parentClassMap.put("MassIndexIndicator", NUM_INDICATOR);
        parentClassMap.put("MMAIndicator", NUM_INDICATOR);
        parentClassMap.put("PPOIndicator", NUM_INDICATOR);
        parentClassMap.put("RAVIIndicator", NUM_INDICATOR);
        parentClassMap.put("ROCIndicator", NUM_INDICATOR);
        parentClassMap.put("RSIIndicator", NUM_INDICATOR);
        parentClassMap.put("RWIHighIndicator", NUM_INDICATOR);
        parentClassMap.put("RWILowIndicator", NUM_INDICATOR);
        parentClassMap.put("SMAIndicator", NUM_INDICATOR);
        parentClassMap.put("StochasticOscillatorDIndicator", NUM_INDICATOR);
        parentClassMap.put("StochasticOscillatorKIndicator", NUM_INDICATOR);
        parentClassMap.put("StochasticRSIIndicator", NUM_INDICATOR);
        parentClassMap.put("TripleEMAIndicator", NUM_INDICATOR);
        parentClassMap.put("UlcerIndexIndicator", NUM_INDICATOR);
        parentClassMap.put("WilliamsRIndicator", NUM_INDICATOR);
        parentClassMap.put("WMAIndicator", NUM_INDICATOR);
        parentClassMap.put("ZLEMAIndicator", NUM_INDICATOR);
        parentClassMap.put("bollinger.PercentBIndicator", NUM_INDICATOR);
    }
}
