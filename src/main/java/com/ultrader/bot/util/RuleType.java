package com.ultrader.bot.util;

import com.fasterxml.jackson.annotation.JsonFormat;

import static com.ultrader.bot.util.TradingUtil.*;

/**
 * Rule Types
 *
 * @author ytx1991
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RuleType {
    CrossedDownIndicatorRule("CrossedDownIndicatorRule", "Crossed Down Rule",
            new String[]{
                    TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
                    TradingUtil.translateToString(NUM_INDICATOR, NUMBER)},
            new String[]{
                    "Satisfied when the value of Indicator1 crosses-down the value Indicator2.",
                    "Satisfied when the value of Indicator1 crosses-down the value of Threshold."},
            new String[]{
                    "Indicator1|Indicator2",
                    "Indicator1|Threshold"}),

    CrossedUpIndicatorRule("CrossedUpIndicatorRule", "Crossed Up Rule",
            new String[]{
                    TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
                    TradingUtil.translateToString(NUM_INDICATOR, NUMBER)},
            new String[]{
                    "Satisfied when the value of Indicator1 crosses-up the value Indicator2.",
                    "Satisfied when the value of Indicator1 crosses-up the value of Threshold."},
            new String[]{
                    "Indicator1|Indicator2",
                    "Indicator1|Threshold"}),

    InPipeRule("InPipeRule", "In Pipe Rule",
            new String[]{
                    TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR, NUM_INDICATOR),
                    TradingUtil.translateToString(NUM_INDICATOR, NUMBER, NUMBER)},
            new String[]{
                    "Satisfied when the value of the Indicator is between the Upper Indicator & Lower Indicator",
                    "Satisfied when the value of the Indicator is between the Upper Threshold & Lower Threshold"},
            new String[]{
                    "Indicator|Upper Indicator|Lower Indicator",
                    "Indicator|Upper Threshold|Lower Threshold"}),

    InSlopeRule("InSlopeRule", "In Slope Rule",
            new String[]{
                    TradingUtil.translateToString(NUM_INDICATOR, INTEGER, NUMBER),
                    TradingUtil.translateToString(NUM_INDICATOR, INTEGER, NUMBER, NUMBER),
                    TradingUtil.translateToString(NUM_INDICATOR, NUMBER),
                    TradingUtil.translateToString(NUM_INDICATOR, NUMBER, NUMBER)},
            new String[]{
                    "Satisfied when the difference of the indicator current value and its previous n (Integer N) value is below the Upper Threshold. It can test both, positive and negative slope.",
                    "Satisfied when the difference of the indicator current value and its previous n (Integer N) value is below the Upper Threshold and above the Lower Threshold. It can test both, positive and negative slope.",
                    "Satisfied when the difference of the indicator current value and its previous value is above the Lower Threshold. It can test both, positive and negative slope.",
                    "Satisfied when the difference of the indicator current value and its previous value is below the Upper Threshold and above the Lower Threshold. It can test both, positive and negative slope."},
            new String[]{
                    "Indicator|Integer N|Upper Threshold",
                    "Indicator|Integer N|Lower Threshold|Upper Threshold",
                    "Indicator|Lower Threshold",
                    "Indicator|Lower Threshold|Upper Threshold"}),

    IsEqualRule("IsEqualRule", "Is Equal Rule",
            new String[]{
                    TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
                    TradingUtil.translateToString(NUM_INDICATOR, NUMBER)},
            new String[]{
                    "Satisfied when the value of the Indicator1 is equal to the value of the Indicator2.",
                    "Satisfied when the value of the Indicator is equal to the Number."},
            new String[]{
                    "Indicator1|Indicator2",
                    "Indicator|Number"}),

    IsFallingRule("IsFallingRule", "Is Falling Rule",
            new String[]{
                    TradingUtil.translateToString(NUM_INDICATOR, INTEGER),
                    TradingUtil.translateToString(NUM_INDICATOR, INTEGER, DOUBLE)},
            new String[]{
                    "Satisfied when ALL the values of the indicator are decreasing within a number of data points.",
                    "Satisfied when there are a Percentage of values of the indicator are decreasing within a number of data points."},
            new String[]{
                    "Indicator|Number of data points (Integer)",
                    "Indicator|Number of data points (Integer)|Percentage (0 to 1)"}),

    IsRisingRule("IsRisingRule", "Is Rising Rule",
            new String[]{
                    TradingUtil.translateToString(NUM_INDICATOR, INTEGER),
                    TradingUtil.translateToString(NUM_INDICATOR, INTEGER, DOUBLE)},
            new String[]{
                    "Satisfied when ALL the values of the indicator are increasing within a number of data points.)",
                    "Satisfied when there are a Percentage (0 to 1) of values of the indicator are increasing within a number of data points."},
            new String[]{
                    "Indicator|Number of data points (Integer)",
                    "Indicator|Number of data points (Integer)|Percentage (0 to 1)"}),

    IsHighestRule("IsHighestRule", "Is Highest Rule",
            new String[]{
                    TradingUtil.translateToString(NUM_INDICATOR, INTEGER)},
            new String[]{"Satisfied when the value of the indicator is the highest within a number of data points."},
            new String[]{"Indicator|Number of data points (Integer)"}),

    IsLowestRule("IsLowestRule", "Is Lowest Rule",
            new String[]{
                    TradingUtil.translateToString(NUM_INDICATOR, INTEGER)},
            new String[]{"Satisfied when the value of the indicator is the lowest within a number of data points."},
            new String[]{"Indicator|Number of data points (Integer)"}),

    OverIndicatorRule("OverIndicatorRule", "Over Rule",
            new String[]{
                    TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
                    TradingUtil.translateToString(NUM_INDICATOR, NUMBER)},
            new String[]{
                    "Satisfied when the current value of the Indicator1 is strictly greater than the value of the Indicator2.",
                    "Satisfied when the current value of the Indicator is strictly greater than the value of the Threshold."},
            new String[]{
                    "Indicator1|Indicator2",
                    "Indicator|Threshold"}),

    UnderIndicatorRule("UnderIndicatorRule", "Under Rule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER)},
            new String[]{
                    "Satisfied when the current value of the Indicator1 is strictly lesser than the value of the Indicator2.",
                    "Satisfied when the current value of the Indicator is strictly lesser than the value of the Threshold."},
            new String[]{
                    "Indicator1|Indicator2",
                    "Indicator|Threshold"}),

    StopGainRule("StopGainRule", "Stop Gain Rule",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, NUMBER)},
            new String[]{"Satisfied when the current price reaches the gain percentage (e.g. 5% = 5)."},
            new String[]{"Current Price|Percentage (Positive)"}),

    StopHoldRule("StopHoldRule", "Stop Hold Rule",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)},
            new String[]{"Satisfied when holding an asset more than n seconds."},
            new String[]{"Current Price|Seconds (Integer)"}),

    StopLossRule("StopLossRule", "Stop Loss Rule",
            new String[]{TradingUtil.translateToString(CLOSE_PRICE, NUMBER)},
            new String[]{"Satisfied when the current price reaches the loss percentage (e.g. 5% = 5)."},
            new String[]{"Current Price|Percentage (Positive)"});

    private final String name;
    private String classz;
    private String[] args;
    private String[] argName;
    private String[] descriptions;

    RuleType(String classz, String name, String[] args, String[] descriptions, String[] argName) {
        this.classz = classz;
        this.name = name;
        this.args = args;
        this.descriptions = descriptions;
        this.argName = argName;
    }

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
}
