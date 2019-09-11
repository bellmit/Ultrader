package com.ultrader.bot.rule;

import com.ultrader.bot.monitor.TradingAccountMonitor;
import org.ta4j.core.TimeSeries;
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
    private TimeSeries timeSeries;
    public StopHoldRule(ClosePriceIndicator closePrice, int seconds) {
        limitInSeconds = seconds;
        symbol = closePrice.getTimeSeries().getName();
        timeSeries = closePrice.getTimeSeries();
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        if(!TradingAccountMonitor.getPositions().containsKey(symbol)) {
            //Could be back testing
            if (tradingRecord.getLastEntry() == null) {
                return false;
            }
            return tradingRecord.getLastEntry().isBuy() &&
                    (timeSeries.getBar(index).getEndTime().toEpochSecond() -
                            timeSeries.getBar(tradingRecord.getLastEntry().getIndex()).getEndTime().toEpochSecond()) > limitInSeconds;
        } else {
            //Could be back testing
            if (tradingRecord.getLastEntry() == null) {
                return false;
            }
            if (tradingRecord.getLastEntry().isBuy() &&
                    (timeSeries.getBar(index).getEndTime().toEpochSecond() -
                            timeSeries.getBar(tradingRecord.getLastEntry().getIndex()).getEndTime().toEpochSecond()) > limitInSeconds) {
                return true;
            }
            Date buyDate =TradingAccountMonitor.getPositions().get(symbol).getBuyDate();
            return (new Date().getTime() - buyDate.getTime()) / 1000 >= limitInSeconds;
        }
    }
}
