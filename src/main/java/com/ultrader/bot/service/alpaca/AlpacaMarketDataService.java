package com.ultrader.bot.service.alpaca;

import com.ultrader.bot.service.MarketDataService;
import org.ta4j.core.TimeSeries;

import java.util.List;
import java.util.Set;

/**
 * Alpaca Market data API
 * @author ytx1991
 */
public class AlpacaMarketDataService implements MarketDataService {
    @Override
    public List<TimeSeries> getStockBars(Set<String> stocks, Long interval, int ticks) {
        return null;
    }
}
