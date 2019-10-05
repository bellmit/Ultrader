package com.ultrader.bot.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StrategyTemplate {
    SWING_TRADING("SwingTrading", "基于MACD，RSI，DEMA短线交易策略", "<p>买入策略: 基于MACD，RSI和DEMA的短线买入策略 </p><p>卖出策略: 在停止上涨或达到止损、持有时间过长后卖出的策略</p>",
            "{ \n" +
                    "   \"strategies\":[ \n" +
                    "      { \n" +
                    "         \"id\":9,\n" +
                    "         \"name\":\"基于MACD,RSI，DEMA的买入策略\",\n" +
                    "         \"description\":\"针对ULTRADER 600优化的短线买入策略\",\n" +
                    "         \"type\":\"Buy\",\n" +
                    "         \"formula\":\"4&,6&,8\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":10,\n" +
                    "         \"name\":\"基于RSI卖出策略\",\n" +
                    "         \"description\":\"适用于短线的卖出策略\",\n" +
                    "         \"type\":\"Sell\",\n" +
                    "         \"formula\":\"3&,7|,2|,5\"\n" +
                    "      }\n" +
                    "   ],\n" +
                    "   \"rules\":[ \n" +
                    "      { \n" +
                    "         \"id\":2,\n" +
                    "         \"name\":\"亏损3%\",\n" +
                    "         \"description\":\"下跌3%时止损\",\n" +
                    "         \"type\":\"StopLossRule\",\n" +
                    "         \"formula\":\"ClosePrice,Number:3\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":3,\n" +
                    "         \"name\":\"盈利1%\",\n" +
                    "         \"description\":\"上涨1%时止盈\",\n" +
                    "         \"type\":\"StopGainRule\",\n" +
                    "         \"formula\":\"ClosePrice,Number:1\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":4,\n" +
                    "         \"name\":\"MACD小于-0.06\",\n" +
                    "         \"description\":\"MACD从小于-0.06\",\n" +
                    "         \"type\":\"UnderIndicatorRule\",\n" +
                    "         \"formula\":\"MACDIndicator:ClosePrice,Number:-0.06\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":5,\n" +
                    "         \"name\":\"Hold 7天\",\n" +
                    "         \"description\":\"hold不超过7天 （604800秒）\",\n" +
                    "         \"type\":\"StopHoldRule\",\n" +
                    "         \"formula\":\"ClosePrice,Integer:604800\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":6,\n" +
                    "         \"name\":\"RSI 超卖\",\n" +
                    "         \"description\":\"RSI 从下穿越30\",\n" +
                    "         \"type\":\"CrossedUpIndicatorRule\",\n" +
                    "         \"formula\":\"RSIIndicator:ClosePrice:14,Number:30\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":7,\n" +
                    "         \"name\":\"RSI 超买\",\n" +
                    "         \"description\":\"RSI 从上穿越70\",\n" +
                    "         \"type\":\"CrossedDownIndicatorRule\",\n" +
                    "         \"formula\":\"RSIIndicator:ClosePrice:14,Number:70\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":8,\n" +
                    "         \"name\":\"整理阶段\",\n" +
                    "         \"description\":\"Double EMA在过去36个Bar内斜率稳定\",\n" +
                    "         \"type\":\"InSlopeRule\",\n" +
                    "         \"formula\":\"DoubleEMAIndicator:ClosePrice:50,Integer:36,Number:-0.6,Number:0.6\"\n" +
                    "      }\n" +
                    "   ]\n" +
                    "}");

    private String id;
    private String name;
    private String description;
    private String strategy;
    StrategyTemplate(String id, String name, String description, String strategy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.strategy = strategy;
    }
    public String getId() {
        return id;
    }

    public String getStrategy() {
        return strategy;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
