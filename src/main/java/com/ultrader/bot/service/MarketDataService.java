package com.ultrader.bot.service;

import org.ta4j.core.TimeSeries;

import java.util.List;
import java.util.Set;

/**
 * Market data service interface
 * @author ytx1991
 */
public interface MarketDataService {
    List<TimeSeries> getStockBars(Set<String> stocks, Long interval, int ticks);
}
