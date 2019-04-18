package com.ultrader.bot.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

import static com.ultrader.bot.util.TradingUtil.TIME_SERIES;
import static com.ultrader.bot.util.TradingUtil.INTEGER;
import static com.ultrader.bot.util.TradingUtil.CLOSE_PRICE;
import static com.ultrader.bot.util.TradingUtil.NUM_INDICATOR;
/**
 * Indicator Types
 * @author ytx1991
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum IndicatorType {

    AccelerationDecelerationIndicator("AccelerationDecelerationIndicator", new String[]{
            TIME_SERIES,
            TradingUtil.translateToString(TIME_SERIES, INTEGER, INTEGER)}),
    AroonDownIndicator("AroonDownIndicator", new String[]{TradingUtil.translateToString(TIME_SERIES, INTEGER)}),
    EMAIndicator("EMAIndicator", new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    SMAIndicator("SMAIndicator", new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER)}),
    MACDIndicator("MACDIndicator", new String[]{TradingUtil.translateToString(CLOSE_PRICE, INTEGER, INTEGER)});
    private String name;

    private String[] args;
    IndicatorType(String name, String[] args){
        this.name = name;
        this.args = args;
    }
    public static final Map<String, String> parentClassMap = new HashMap<>();

    public String getName() {
        return this.name;
    }

    public String[] getArgs() {
        return this.args;
    }
    static {
        parentClassMap.put("AccelerationDecelerationIndicator", NUM_INDICATOR);
        parentClassMap.put("AroonDownIndicator", NUM_INDICATOR);
        parentClassMap.put("EMAIndicator", NUM_INDICATOR);
        parentClassMap.put("SMAIndicator", NUM_INDICATOR);
        parentClassMap.put("MACDIndicator", NUM_INDICATOR);
    }
}
