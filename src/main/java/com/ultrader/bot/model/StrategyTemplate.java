package com.ultrader.bot.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StrategyTemplate {
    MACD("MACD", "基于MACD的高级策略", "<p>买入策略: 当前期小范围震荡且MACD出现V字型且MACD大于0.5 </p><p>卖出策略: （当MACD出现A字型且盈利3%）或者亏损5%</p>","{\n" +
            "\t\"strategies\": [{\n" +
            "\t\t\"id\": 7,\n" +
            "\t\t\"name\": \"MACD买入策略\",\n" +
            "\t\t\"description\": \"基于MACD的高级买入策略\",\n" +
            "\t\t\"type\": \"Buy\",\n" +
            "\t\t\"formula\": \"5&,4\"\n" +
            "\t}, {\n" +
            "\t\t\"id\": 8,\n" +
            "\t\t\"name\": \"MACD卖出策略\",\n" +
            "\t\t\"description\": \"基于MACD的高级卖出策略\",\n" +
            "\t\t\"type\": \"Sell\",\n" +
            "\t\t\"formula\": \"6&,3|,2\"\n" +
            "\t}],\n" +
            "\t\"rules\": [{\n" +
            "\t\t\"id\": 2,\n" +
            "\t\t\"name\": \"亏了5%\",\n" +
            "\t\t\"description\": \"跌5%时止损\",\n" +
            "\t\t\"type\": \"StopLossRule\",\n" +
            "\t\t\"formula\": \"ClosePrice,Number:5\"\n" +
            "\t}, {\n" +
            "\t\t\"id\": 3,\n" +
            "\t\t\"name\": \"盈利3%\",\n" +
            "\t\t\"description\": \"3% 止盈\",\n" +
            "\t\t\"type\": \"StopGainRule\",\n" +
            "\t\t\"formula\": \"ClosePrice,Number:3\"\n" +
            "\t}, {\n" +
            "\t\t\"id\": 4,\n" +
            "\t\t\"name\": \"MACD大于0.5\",\n" +
            "\t\t\"description\": \"MACD从小于0.5变成大于0.5\",\n" +
            "\t\t\"type\": \"CrossedUpIndicatorRule\",\n" +
            "\t\t\"formula\": \"MACDIndicator:ClosePrice:12:26,Number:0.5\"\n" +
            "\t}, {\n" +
            "\t\t\"id\": 5,\n" +
            "\t\t\"name\": \"MACD V型\",\n" +
            "\t\t\"description\": \"MACD 出现V型 在过去36个数据点中\",\n" +
            "\t\t\"type\": \"IsHighestRule\",\n" +
            "\t\t\"formula\": \"MACDIndicator:ClosePrice,Integer:36\"\n" +
            "\t}, {\n" +
            "\t\t\"id\": 6,\n" +
            "\t\t\"name\": \"MACD A型\",\n" +
            "\t\t\"description\": \"MACD 出现A型 在过去24个数据点中\",\n" +
            "\t\t\"type\": \"IsLowestRule\",\n" +
            "\t\t\"formula\": \"MACDIndicator:ClosePrice,Integer:24\"\n" +
            "\t}]\n" +
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
