package com.ultrader.bot.util;

import static com.ultrader.bot.util.TradingUtil.*;

/**
 * Rule Types
 * @author ytx1991
 */
public enum RuleType {
    BooleanIndicatorRule("BooleanIndicatorRule", new String[]{BOOLEAN_INDICATOR}),

    BooleanRule("BooleanRule", new String[]{BOOLEAN}),

    CrossedDownIndicatorRule("CrossedDownIndicatorRule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER)}),

    CrossedUpIndicatorRule("CrossedUpIndicatorRule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER)}),

    InPipeRule("InPipeRule",  new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR, NUM_INDICATOR),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER, NUMBER)}),

    InSlopeRule("InSlopeRule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER, NUMBER),
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER, NUMBER, NUMBER),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER, NUMBER)}),

    IsEqualRule("IsEqualRule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER)}),

    IsFallingRule("IsFallingRule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER),
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER, NUMBER)}),

    IsRisingRule("IsRisingRule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER),
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER, NUMBER)}),

    IsHighestRule("IsHighestRule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER)}),

    IsLowestRule("IsLowestRule", new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, INTEGER)}),

    OverIndicatorRule("OverIndicatorRule",  new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER)}),

    UnderIndicatorRule("OverIndicatorRule",  new String[]{
            TradingUtil.translateToString(NUM_INDICATOR, NUM_INDICATOR),
            TradingUtil.translateToString(NUM_INDICATOR, NUMBER)}),

    StopGainRule("StopGainRule", new String[]{
            TradingUtil.translateToString(CLOSE_PRICE, NUMBER)}),

    StopLossRule("StopLossRule", new String[]{
            TradingUtil.translateToString(CLOSE_PRICE, NUMBER)}),

    WaitForRule("WaitForRule", new String[]{
            TradingUtil.translateToString(ORDER_TYPE, INTEGER)});
    private final String name;
    private String[] args;
    RuleType(String name, String[] args) {
        this.name = name;
        this.args = args;
    }


}
