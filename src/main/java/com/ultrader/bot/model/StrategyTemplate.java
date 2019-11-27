package com.ultrader.bot.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StrategyTemplate {
    SWING_TRADING("SwingTrading", "A swing trading strategy based on multiple indicators, changing with the market trend", "<p>Buy Strategy: Buying when there may be a bounce. </p><p>Sell Strategy: Selling when the bounce stopped.</p>",
            "{ \n" +
                    "   \"strategies\":[ \n" +
                    "      { \n" +
                    "         \"id\":20,\n" +
                    "         \"name\":\"Normal Buy Strategy\",\n" +
                    "         \"description\":\"Buy strategy for normal market\",\n" +
                    "         \"type\":\"Buy\",\n" +
                    "         \"formula\":\"18&,16&,15&,12&,9\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":21,\n" +
                    "         \"name\":\"Bullish Buy Strategy\",\n" +
                    "         \"description\":\"Buy strategy for bullish market\",\n" +
                    "         \"type\":\"Buy\",\n" +
                    "         \"formula\":\"18&,16&,15&,12&,8\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":22,\n" +
                    "         \"name\":\"Bearish Buy Strategy\",\n" +
                    "         \"description\":\"Buy strategy for bearish market\",\n" +
                    "         \"type\":\"Buy\",\n" +
                    "         \"formula\":\"18&,16&,15&,12&,10\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":23,\n" +
                    "         \"name\":\"Bullish Stop Loss Strategy\",\n" +
                    "         \"description\":\"Stop loss strategy for bullish market\",\n" +
                    "         \"type\":\"Sell\",\n" +
                    "         \"formula\":\"18&,2\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":24,\n" +
                    "         \"name\":\"Normal Stop Loss Strategy\",\n" +
                    "         \"description\":\"Stop loss strategy for normal market\",\n" +
                    "         \"type\":\"Sell\",\n" +
                    "         \"formula\":\"18&,3\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":25,\n" +
                    "         \"name\":\"Bearish Stop Loss Strategy\",\n" +
                    "         \"description\":\"Stop loss strategy for bearish market\",\n" +
                    "         \"type\":\"Sell\",\n" +
                    "         \"formula\":\"18&,4\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":26,\n" +
                    "         \"name\":\"Bullish Stop Gain Strategy\",\n" +
                    "         \"description\":\"Stop gain strategy for bullish market\",\n" +
                    "         \"type\":\"Sell\",\n" +
                    "         \"formula\":\"13|,14|,17&,5\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":27,\n" +
                    "         \"name\":\"Normal Stop Gain Strategy\",\n" +
                    "         \"description\":\"Stop gain strategy for normal market\",\n" +
                    "         \"type\":\"Sell\",\n" +
                    "         \"formula\":\"13|,14|,17&,6\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":28,\n" +
                    "         \"name\":\"Bearish Stop Gain Strategy\",\n" +
                    "         \"description\":\"Stop gain strategy for bearish market\",\n" +
                    "         \"type\":\"Sell\",\n" +
                    "         \"formula\":\"13|,14|,17&,7\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":29,\n" +
                    "         \"name\":\"Bullish Sell Strategy\",\n" +
                    "         \"description\":\"Sell strategy for bullish market\",\n" +
                    "         \"type\":\"Sell\",\n" +
                    "         \"formula\":\"S23|,S26|,11\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":30,\n" +
                    "         \"name\":\"Normal Sell Strategy\",\n" +
                    "         \"description\":\"Sell strategy for normal market\",\n" +
                    "         \"type\":\"Sell\",\n" +
                    "         \"formula\":\"S24|,S27|,11\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":31,\n" +
                    "         \"name\":\"Bearish Sell Strategy\",\n" +
                    "         \"description\":\"Sell strategy for bearish market\",\n" +
                    "         \"type\":\"Sell\",\n" +
                    "         \"formula\":\"S25|,S28|,11\"\n" +
                    "      }\n" +
                    "   ],\n" +
                    "   \"rules\":[ \n" +
                    "      { \n" +
                    "         \"id\":2,\n" +
                    "         \"name\":\"Bullish Stop Loss\",\n" +
                    "         \"description\":\"Stop loss when market is bullish\",\n" +
                    "         \"type\":\"StopLossRule\",\n" +
                    "         \"formula\":\"ClosePrice,Number:1.5\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":3,\n" +
                    "         \"name\":\"Normal Stop Loss\",\n" +
                    "         \"description\":\"Stop loss when market is normal\",\n" +
                    "         \"type\":\"StopLossRule\",\n" +
                    "         \"formula\":\"ClosePrice,Number:2.0\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":4,\n" +
                    "         \"name\":\"Bearish Stop Loss\",\n" +
                    "         \"description\":\"Stop loss when market is bearish\",\n" +
                    "         \"type\":\"StopLossRule\",\n" +
                    "         \"formula\":\"ClosePrice,Number:2.5\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":5,\n" +
                    "         \"name\":\"Bullish Stop Gain\",\n" +
                    "         \"description\":\"Stop gain when market is bullish\",\n" +
                    "         \"type\":\"StopGainRule\",\n" +
                    "         \"formula\":\"ClosePrice,Number:1\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":6,\n" +
                    "         \"name\":\"Normal Stop Gain\",\n" +
                    "         \"description\":\"Stop gain when market is normal\",\n" +
                    "         \"type\":\"StopGainRule\",\n" +
                    "         \"formula\":\"ClosePrice,Number:0.75\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":7,\n" +
                    "         \"name\":\"Bearish Stop Gain\",\n" +
                    "         \"description\":\"Stop gain when market is bearish\",\n" +
                    "         \"type\":\"StopGainRule\",\n" +
                    "         \"formula\":\"ClosePrice,Number:0.5\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":8,\n" +
                    "         \"name\":\"MACD Bullish Buy Signal\",\n" +
                    "         \"description\":\"MACD buy signal for bullish market\",\n" +
                    "         \"type\":\"UnderIndicatorRule\",\n" +
                    "         \"formula\":\"MACDIndicator:ClosePrice,Number:0.06\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":9,\n" +
                    "         \"name\":\"MACD Normal Buy Signal\",\n" +
                    "         \"description\":\"MACD buy signal for normal market\",\n" +
                    "         \"type\":\"UnderIndicatorRule\",\n" +
                    "         \"formula\":\"MACDIndicator:ClosePrice,Number:-0.065\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":10,\n" +
                    "         \"name\":\"MACD Bearish Buy Signal\",\n" +
                    "         \"description\":\"MACD buy signal for beraish market\",\n" +
                    "         \"type\":\"UnderIndicatorRule\",\n" +
                    "         \"formula\":\"MACDIndicator:ClosePrice,Number:-0.324\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":11,\n" +
                    "         \"name\":\"Hold 3 Days\",\n" +
                    "         \"description\":\"Hold no longer than 4 days (345600 seconds)\",\n" +
                    "         \"type\":\"StopHoldRule\",\n" +
                    "         \"formula\":\"ClosePrice,Integer:345600\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":12,\n" +
                    "         \"name\":\"RSI Buy Signal\",\n" +
                    "         \"description\":\"RSI crossed up 30\",\n" +
                    "         \"type\":\"CrossedUpIndicatorRule\",\n" +
                    "         \"formula\":\"RSIIndicator:ClosePrice:14,Number:30\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":13,\n" +
                    "         \"name\":\"RSI Sell Signal I\",\n" +
                    "         \"description\":\"RSI crossed down 70\",\n" +
                    "         \"type\":\"CrossedDownIndicatorRule\",\n" +
                    "         \"formula\":\"RSIIndicator:ClosePrice:14,Number:70\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":14,\n" +
                    "         \"name\":\"RSI Sell Signal II\",\n" +
                    "         \"description\":\"RSI crossed down 80\",\n" +
                    "         \"type\":\"CrossedDownIndicatorRule\",\n" +
                    "         \"formula\":\"RSIIndicator:ClosePrice:14,Number:80\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":15,\n" +
                    "         \"name\":\"MACD Raising\",\n" +
                    "         \"description\":\"MACD is raising\",\n" +
                    "         \"type\":\"IsHighestRule\",\n" +
                    "         \"formula\":\"MACDIndicator:ClosePrice,Integer:5\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":16,\n" +
                    "         \"name\":\"EMA Buy Signal\",\n" +
                    "         \"description\":\"EMA buy signal\",\n" +
                    "         \"type\":\"OverIndicatorRule\",\n" +
                    "         \"formula\":\"EMAIndicator:ClosePrice:26,EMAIndicator:ClosePrice:12\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":17,\n" +
                    "         \"name\":\"B% Sell Signal\",\n" +
                    "         \"description\":\"B% crossed down 1.1\",\n" +
                    "         \"type\":\"CrossedDownIndicatorRule\",\n" +
                    "         \"formula\":\"bollinger.PercentBIndicator:ClosePrice:20:2.0,Number:1.1\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":18,\n" +
                    "         \"name\":\"RSI > 30\",\n" +
                    "         \"description\":\"RSI > 30\",\n" +
                    "         \"type\":\"OverIndicatorRule\",\n" +
                    "         \"formula\":\"RSIIndicator:ClosePrice:14,Number:30\"\n" +
                    "      },\n" +
                    "      { \n" +
                    "         \"id\":19,\n" +
                    "         \"name\":\"Consolidation \",\n" +
                    "         \"description\":\"DEMA(50) consolidation validation\",\n" +
                    "         \"type\":\"InSlopeRule\",\n" +
                    "         \"formula\":\"DoubleEMAIndicator:ClosePrice:50,Integer:36,Number:-1,Number:0.3\"\n" +
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
