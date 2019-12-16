package com.ultrader.bot.util;

import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.PrecisionNum;

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
}
