package com.ultrader.bot.service;

import org.ta4j.core.Bar;
import org.ta4j.core.TimeSeries;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Market data service interface
 * @author ytx1991
 */
public interface MarketDataService {
    List<TimeSeries> updateTimeSeries(List<TimeSeries> stocks, Long interval);
}
