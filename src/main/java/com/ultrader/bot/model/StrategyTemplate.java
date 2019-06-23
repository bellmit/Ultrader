package com.ultrader.bot.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StrategyTemplate {
    MACD("MACD", "MACD Basic Strategy", "<p>Buy Strategy: MACD bigger than 0.5</p><p>Sell Strategy: Current profit bigger than 2%</p>",
            "{\n" +
            "\t\"strategies\": [{\n" +
            "\t\t\"id\": 1,\n" +
            "\t\t\"name\": \"Simple sell strategy\",\n" +
            "\t\t\"description\": \"2% sell\",\n" +
            "\t\t\"type\": \"Sell\",\n" +
            "\t\t\"formula\": \"1\"\n" +
            "\t}, {\n" +
            "\t\t\"id\": 2,\n" +
            "\t\t\"name\": \"MACD simple buy strategy\",\n" +
            "\t\t\"description\": \"MACD buy\",\n" +
            "\t\t\"type\": \"Buy\",\n" +
            "\t\t\"formula\": \"2\"\n" +
            "\t}],\n" +
            "\t\"rules\": [{\n" +
            "\t\t\"id\": 1,\n" +
            "\t\t\"name\": \"Stop Gain\",\n" +
            "\t\t\"description\": \"Stop Gain\",\n" +
            "\t\t\"type\": \"StopGainRule\",\n" +
            "\t\t\"formula\": \"ClosePrice,Number:2\"\n" +
            "\t}, {\n" +
            "\t\t\"id\": 2,\n" +
            "\t\t\"name\": \"MACD Buy\",\n" +
            "\t\t\"description\": \"When MACD > 0.5\",\n" +
            "\t\t\"type\": \"CrossedUpIndicatorRule\",\n" +
            "\t\t\"formula\": \"MACDIndicator:ClosePrice:12:26,Number:0.5\"\n" +
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
