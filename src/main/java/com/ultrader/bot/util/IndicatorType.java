package com.ultrader.bot.util;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.HashMap;
import java.util.Map;

import static com.ultrader.bot.util.TradingUtil.*;

/**
 * Indicator Types
 *
 * @author ytx1991
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum IndicatorType {

    AccelerationDecelerationIndicator("AccelerationDecelerationIndicator", "Acceleration Deceleration",
            new String[]{
                    TIME_SERIES,
                    TradingUtil.translateToString(TIME_SERIES, INTEGER, INTEGER)},
            new String[]{
                    "Acceleration-deceleration indicator, Google for more details. SMA1 bar count = 5, SMA2 bar count = 34",
                    "Acceleration-deceleration indicator, Google for more details."},
            new String[]{
                    "Current Price",
                    "Current Price|SMA1 bar count (Integer)|SMA2 bar count (Integer)"}),
    AroonDownIndicator("AroonDownIndicator", "Aroon Down",
            new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)},
            new String[]{"Aroon down indicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    AroonUpIndicator("AroonUpIndicator", "Aroon Up",
            new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)},
            new String[]{"Aroon up indicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    AroonOscillatorIndicator("AroonOscillatorIndicator", "Aroon Oscillator",
            new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)},
            new String[]{"Aroon Oscillator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    ATRIndicator("ATRIndicator", "Average True Range",
            new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)},
            new String[]{"Average true range indicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    AwesomeOscillatorIndicator("AwesomeOscillatorIndicator", "Awesome Oscillator",
            new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER, INTEGER)},
            new String[]{"Awesome oscillator, Google for more details. Usually SMA1 = 5, SMA2 = 34."},
            new String[]{"Current Price|SMA1 bar count (Integer)|SMA2 bar count (Integer)"}),
    CCIIndicator("CCIIndicator", "Commodity Channel Index",
            new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)},
            new String[]{"Commodity Channel Index (CCI) indicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    ChandelierExitLongIndicator("ChandelierExitLongIndicator", "Chandelier Exit Long",
            new String[]{
                    TIME_SERIES,
                    TradingUtil.translateToString(TIME_SERIES, INTEGER, DOUBLE)},
            new String[]{
                    "The Chandelier Exit (long) Indicator, Google for more details. Bar count = 22, K = 3.0.",
                    "The Chandelier Exit (long) Indicator, Google for more details. Usually, bar count = 22, K = 3.0."},
            new String[]{
                    "Current Price",
                    "Current Price|Bar count (Integer)|ATR K Multiplier"}),
    ChandelierExitShortIndicator("ChandelierExitShortIndicator", "Chandelier Exit Short",
            new String[]{TIME_SERIES, TradingUtil.translateToString(TIME_SERIES, INTEGER, DOUBLE)},
            new String[]{
                    "The Chandelier Exit (short) Indicator, Google for more details. Bar count = 22, K = 3.0.",
                    "The Chandelier Exit (short) Indicator, Google for more details. Usually, bar count = 22, K = 3.0."},
            new String[]{
                    "Current Price",
                    "Current Price|Bar count (Integer)|ATR K Multiplier"}),
    ChopIndicator("ChopIndicator", "Chop",
            new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER, INTEGER)},
            new String[]{"The \"CHOP\" index is used to indicate side-ways markets, Google for more details. Usually, bar count = 14, normalization = 1 or 100"},
            new String[]{"Current Price|Bar count (Integer)|Normalization (Integer)"}),
    CMOIndicator("CMOIndicator", "Chande Momentum Oscillator",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"Chande Momentum Oscillator indicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    CoppockCurveIndicator("CoppockCurveIndicator", "Coppock Curve",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER, INTEGER, INTEGER)},
            new String[]{"Coppock Curve indicator, Google for more details. Usually long term RoC bar count = 14, short term RoC bar count = 11, WMA bar count = 10"},
            new String[]{"Current Price|Long term RoC bar count|Short term RoC bar count|WMA bar count"}),
    DoubleEMAIndicator("DoubleEMAIndicator", "Double EMA",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"Double exponential moving average indicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    DPOIndicator("DPOIndicator", "Detrended Price Oscillator",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"The Detrended Price Oscillator (DPO) is an indicator designed to remove trend from price and make it easier to identify cycles. DPO does not extend to the last date because it is based on a displaced moving average. However, alignment with the most recent is not an issue because DPO is not a momentum oscillator. Instead, DPO is used to identify cycles highs/lows and estimate cycle length. In short, DPO(20) equals price 11 days ago less the 20-day SMA."},
            new String[]{"Current Price|Bar count (Integer)"}),
    EMAIndicator("EMAIndicator", "Exponential Moving Average",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"Exponential moving average indicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    FisherIndicator("FisherIndicator", "Fisher",
            new String[]{
                    TradingUtil.translateToString(CLOSE_PRICE, INTEGER),
                    TradingUtil.translateToString(CLOSE_PRICE, INTEGER, DOUBLE, DOUBLE, DOUBLE, DOUBLE)},
            new String[]{
                    "The Fisher Indicator, Google for more details. Alpha = 0.33, beta = 0.67, gamma = 0.5, delta = 0.5 .",
                    "The Fisher Indicator, Google for more details. Usually, alpha = 0.33, beta = 0.67, gamma = 0.5, delta = 0.5 ."},
            new String[]{
                    "Current Price|Bar count (Integer)",
                    "Current Price|Bar count (Integer)|Alpha|Beta|Gamma|Delta"}),
    HMAIndicator("HMAIndicator", "Hull Moving Average",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"Hull moving average (HMA) indicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    KAMAIndicator("KAMAIndicator", " Kaufman's Adaptive Moving Average",
            new String[]{
                    CLOSE_PRICE,
                    TradingUtil.translateToString(CLOSE_PRICE, INTEGER, INTEGER, INTEGER)},
            new String[]{
                    "The Kaufman's Adaptive Moving Average (KAMA) Indicator, Google for more details. Effective ratio bar count = 10, Fast bar count = 2, Slow bar count = 30.",
                    "The Kaufman's Adaptive Moving Average (KAMA) Indicator, Google for more details. Usually, Effective ratio bar count = 10, Fast bar count = 2, Slow bar count = 30."},
            new String[]{
                    "Current Price",
                    "Current Price|Effective ratio bar count|Fast bar count|Slow bar count"}),
    MACDIndicator("MACDIndicator", "Moving Average Convergence Divergence",
            new String[]{
                    CLOSE_PRICE,
                    TradingUtil.translateToString(CLOSE_PRICE, INTEGER, INTEGER)},
            new String[]{
                    "Moving average convergence divergence (MACDIndicator) indicator. Aka. MACD Absolute Price Oscillator (APO). Short term bar count = 12, Long term bar count = 26.",
                    "Moving average convergence divergence (MACDIndicator) indicator. Aka. MACD Absolute Price Oscillator (APO). Usually, Short term bar count = 12, Long term bar count = 26."},
            new String[]{
                    "Current Price",
                    "Current Price|Short term bar count (Integer)|Long term bar count (Integer)"}),
    MassIndexIndicator("MassIndexIndicator", "Mass Index",
            new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER, INTEGER)},
            new String[]{"Mass index indicator, Google for more details. Usually, EMA bar count = 9"},
            new String[]{"Current Price|EMA bar count (Integer)|Bar count (Integer)"}),
    MMAIndicator("MMAIndicator", "Modified Moving Average",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"Modified moving average indicator. It is similar to exponential moving average but smooths more slowly. Used in Welles Wilder's indicators like ADX, RSI."},
            new String[]{"Current Price|Bar count (Integer)"}),
    ParabolicSarIndicator("ParabolicSarIndicator", "Parabolic SAR Indicator",
            new String[]{TradingUtil.translateToString(TIME_SERIES, NUMBER, NUMBER, NUMBER)},
            new String[]{"Parabolic SAR indicator, Google for more details. Usually, Acceleration factor = 0.02, Maximum acceleration = 0.2, Increment step = 0.02"},
            new String[]{"Current Price|Acceleration factor|Maximum acceleration|Increment step"}),
    PPOIndicator("PPOIndicator", "Percentage Price Oscillator",
            new String[]{
                    CLOSE_PRICE,
                    TradingUtil.translateToString(CLOSE_PRICE, INTEGER, INTEGER)},
            new String[]{
                    "Percentage price oscillator (PPO) indicator. Aka. MACD Percentage Price Oscillator (MACD-PPO). Short term bar count = 12, Long term bar count = 26.",
                    "Percentage price oscillator (PPO) indicator. Aka. MACD Percentage Price Oscillator (MACD-PPO). Usually, Short term bar count = 12, Long term bar count = 26."},
            new String[]{
                    "Current",
                    "Current Price|Short term bar count (Integer)|Long term bar count (Integer)"}),
    RAVIIndicator("RAVIIndicator", "Chande's Range Action Verification Index",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER, INTEGER)},
            new String[]{"Chande's Range Action Verification Index (RAVI) indicator. To preserve trend direction, default calculation does not use absolute value. Usually, Short SMA bar count = 7, Long SMA bar count = 65."},
            new String[]{"Current Price|Short SMA bar count (Integer)|Long SMA bar count (Integer)"}),
    ROCIndicator("ROCIndicator", "Rate of Change",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"Rate of change (ROCIndicator) indicator. Aka. Momentum. The ROCIndicator calculation compares the current value with the value \"n\" periods ago."},
            new String[]{"Current Price|Bar count (Integer)"}),
    RSIIndicator("RSIIndicator", "Relative Strength Index",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"Relative strength index indicator. Computed using original Welles Wilder formula."},
            new String[]{"Current Price|Bar count (Integer)"}),
    RWIHighIndicator("RWIHighIndicator", "Random Walk Index High",
            new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)},
            new String[]{"The RandomWalkIndexHighIndicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    RWILowIndicator("RWILowIndicator", "Random Walk Index Low",
            new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)},
            new String[]{"The RandomWalkIndexLowIndicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    SMAIndicator("SMAIndicator", "Simple Moving Average",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"Simple moving average (SMA) indicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    StochasticOscillatorDIndicator("StochasticOscillatorDIndicator", "Stochastic Oscillator D",
            new String[]{CLOSE_PRICE},
            new String[]{"Stochastic oscillator D. Receive StochasticOscillatorKIndicator and returns its SMAIndicator(3)."},
            new String[]{"Current Price"}),
    StochasticOscillatorKIndicator("StochasticOscillatorKIndicator", "Stochastic Oscillator K",
            new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)},
            new String[]{"Stochastic oscillator K. Receives timeSeries and barCount and calculates the StochasticOscillatorKIndicator over ClosePriceIndicator, or receives an indicator, MaxPriceIndicator and MinPriceIndicator and returns StochasticOsiclatorK over this indicator."},
            new String[]{"Current Price|Bar count (Integer)"}),
    StochasticRSIIndicator("StochasticRSIIndicator", "Stochastic RSI",
            new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)},
            new String[]{"The Stochastic RSI Indicator. Stoch RSI = (RSI - MinimumRSIn) / (MaximumRSIn - MinimumRSIn)."},
            new String[]{"Current Price|Bar count (Integer)"}),
    TripleEMAIndicator("TripleEMAIndicator", "Triple EMA",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"Triple exponential moving average indicator. A.K.A TRIX. TEMA needs \"3 * period - 2\" of data to start producing values in contrast to the period samples needed by a regular EMA."},
            new String[]{"Current Price|Bar count (Integer)"}),
    UlcerIndexIndicator("UlcerIndexIndicator", "Ulcer Index",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"Ulcer index indicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    WilliamsRIndicator("WilliamsRIndicator", "William's R",
            new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)},
            new String[]{"William's R indicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    WMAIndicator("WMAIndicator", "WMA",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"WMA indicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    ZLEMAIndicator("ZLEMAIndicator", "Zero-lag EMA",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"Zero-lag exponential moving average indicator, Google for more details."},
            new String[]{"Current Price|Bar count (Integer)"}),
    PercentBIndicator("bollinger.PercentBIndicator", "B Percent",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER, DOUBLE)},
            new String[]{"%B indicator. A.K.A. BollingerBands. Usually K = 2.0"},
            new String[]{"Current Price|Bar count (Integer)|K multiplier"});
    private String classz;
    private String name;
    private String[] args;
    private String[] descriptions;
    private String[] argName;

    IndicatorType(String classz, String name, String[] args, String[] descriptions, String[] argName) {
        this.classz = classz;
        this.name = name;
        this.args = args;
        this.argName = argName;
        this.descriptions = descriptions;
    }

    public static final Map<String, String> parentClassMap = new HashMap<>();

    public String getName() {
        return this.name;
    }

    public String[] getArgs() {
        return this.args;
    }

    public String getClassz() {
        return this.classz;
    }

    public String[] getDescriptions() {
        return this.descriptions;
    }

    public String[] getArgName() {
        return this.argName;
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
