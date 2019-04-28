package com.ultrader.bot.util;

import com.fasterxml.jackson.annotation.JsonFormat;

import static com.ultrader.bot.util.TradingUtil.*;

/**
 * Rule Types
 * @author ytx1991
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RuleType {
    BooleanIndicatorRule("BooleanIndicatorRule", "Boolean Indicator Rule", new String[]{BOOLEAN_INDICATOR}),

    CrossedDownIndicatorRule("CrossedDownIndicatorRule", "Crossed Down Rule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER)}),

    CrossedUpIndicatorRule("CrossedUpIndicatorRule", "Crossed Up Rule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER)}),

    InPipeRule("InPipeRule", "In Pipe Rule",  new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR, NUM_INDICATOR),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER, NUMBER)}),

    InSlopeRule("InSlopeRule", "In Slope Rule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER, NUMBER),
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER, NUMBER, NUMBER),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER, NUMBER)}),

    IsEqualRule("IsEqualRule", "Is Equal Rule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER)}),

    IsFallingRule("IsFallingRule", "Is Falling Rule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER),
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER, NUMBER)}),

    IsRisingRule("IsRisingRule", "Is Rising Rule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER),
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER, NUMBER)}),

    IsHighestRule("IsHighestRule", "Is Highest Rule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER)}),

    IsLowestRule("IsLowestRule", "Is Lowest Rule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER)}),

    OverIndicatorRule("OverIndicatorRule", "Over Rule",  new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER)}),

    UnderIndicatorRule("UnderIndicatorRule", "Under Rule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER)}),

    StopGainRule("StopGainRule", "Stop Gain Rule", new String[]{
            TradingUtil.translateToString(CLOSE_PRICE, NUMBER)}),

    StopLossRule("StopLossRule", "Stop Loss Rule", new String[]{
            TradingUtil.translateToString(CLOSE_PRICE, NUMBER)});

    private final String name;
    private String classz;
    private String[] args;
    RuleType(String classz, String name, String[] args) {
        this.classz = classz;
        this.name = name;
        this.args = args;
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
}
