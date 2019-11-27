package com.ultrader.bot.rule;

import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.AbstractRule;

public class TrailingStopGainRule extends AbstractRule {
    private ClosePriceIndicator priceIndicator;
    private int barCount;
    private Num gainPercentage;
    /**
     * Constant value for 100
     */
    private final Num HUNDRED;
    /**
     * Constructor.
     *
     * @param closePrice     the close price indicator
     * @param gainPercentage the gain percentage
     * @param barCount       number of bars to look back for the calculation
     */
    public TrailingStopGainRule(ClosePriceIndicator closePrice, Num gainPercentage, int barCount) {
        this.priceIndicator = closePrice;
        this.barCount = barCount;
        this.gainPercentage = gainPercentage;
        HUNDRED = closePrice.numOf(100);
    }

    /**
     * Constructor.
     *
     * @param closePrice     the close price indicator
     * @param gainPercentage the gain percentage
     */
    public TrailingStopGainRule(ClosePriceIndicator closePrice, Num gainPercentage) {
        this(closePrice, gainPercentage, Integer.MAX_VALUE);
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        boolean satisfied = false;
        // No trading history or no trade opened, no loss
        if (tradingRecord != null) {
            Trade currentTrade = tradingRecord.getCurrentTrade();
            if (currentTrade.isOpened()) {

                Num entryPrice = currentTrade.getEntry().getPrice();
                Num currentPrice = priceIndicator.getValue(index);

                if (currentTrade.getEntry().isBuy()) {
                    HighestValueIndicator highest = new HighestValueIndicator(priceIndicator,
                            getValueIndicatorBarCount(index, currentTrade.getEntry().getIndex()));
                    Num highestCloseNum = highest.getValue(index);
                    satisfied = isBuyGainSatisfied(entryPrice, currentPrice, highestCloseNum);
                } else {
                    LowestValueIndicator lowest = new LowestValueIndicator(priceIndicator,
                            getValueIndicatorBarCount(index, currentTrade.getEntry().getIndex()));
                    Num lowestCloseNum = lowest.getValue(index);
                    satisfied = isSellGainSatisfied(entryPrice, currentPrice, lowestCloseNum);
                }
            }
        }
        traceIsSatisfied(index, satisfied);
        return satisfied;
    }

    private boolean isSellGainSatisfied(Num entryPrice, Num currentPrice, Num lowestPrice) {
        Num lossRatioThreshold = HUNDRED.minus(gainPercentage).dividedBy(HUNDRED);
        Num threshold = entryPrice.multipliedBy(lossRatioThreshold);
        return currentPrice.isGreaterThanOrEqual(threshold) && lowestPrice.isLessThanOrEqual(threshold);
    }

    private boolean isBuyGainSatisfied(Num entryPrice, Num currentPrice, Num highestPrice) {
        Num lossRatioThreshold = HUNDRED.plus(gainPercentage).dividedBy(HUNDRED);
        Num threshold = entryPrice.multipliedBy(lossRatioThreshold);
        return currentPrice.isLessThanOrEqual(threshold) && highestPrice.isGreaterThanOrEqual(threshold);
    }

    private int getValueIndicatorBarCount(int index, int tradeIndex) {
        return Math.min(index - tradeIndex + 1, this.barCount);
    }
}
