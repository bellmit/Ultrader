package com.ultrader.bot.util;

import com.ultrader.bot.monitor.MarketDataMonitor;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.PrecisionNum;

import java.util.Map;
import java.util.Set;

public class MarketDataUtil {
    public static Bar fillBarGap(Bar previousBar, long offsetSeconds) {
        Bar bar = new BaseBar(
                previousBar.getTimePeriod(),
                previousBar.getEndTime().plusSeconds(offsetSeconds),
                previousBar.getClosePrice(),
                previousBar.getClosePrice(),
                previousBar.getClosePrice(),
                previousBar.getClosePrice(),
                PrecisionNum.valueOf(0),
                PrecisionNum.valueOf(0));
        return bar;
    }

    public static String getExchangeBySymbol(String symbol) {
        try {
            for(Map.Entry<String, Set<String>> entry : MarketDataMonitor.getInstance().getAvailableStock().entrySet()) {
                if (entry.getValue().contains(symbol)) {
                    return entry.getKey();
                }
            }
        } catch (IllegalAccessException e) {
            return "";
        }
        return "";
    }
}
