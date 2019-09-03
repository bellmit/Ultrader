package com.ultrader.bot.rule;

import com.ultrader.bot.monitor.TradingAccountMonitor;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.AbstractRule;

import java.util.Date;

/**
 * Stop Hold Rule
 * Satisfy when holding asset for a long time
 * @author ytx1991
 */
public class StopHoldRule extends AbstractRule {

    private String symbol;
    private long limitInSeconds;
    public StopHoldRule(ClosePriceIndicator closePrice, int seconds) {
        limitInSeconds = seconds;
        symbol = closePrice.getTimeSeries().getName();
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        if(!TradingAccountMonitor.getPositions().containsKey(symbol)) {
            return false;
        }
        Date buyDate =TradingAccountMonitor.getPositions().get(symbol).getBuyDate();
        return (new Date().getTime() - buyDate.getTime()) / 1000 >= limitInSeconds;
    }
}
