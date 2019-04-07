package com.ultrader.bot.service;

import com.ultrader.bot.model.alpaca.Asset;

import java.util.Map;
import java.util.Set;

/**
 * Trading Service Interface
 * @author ytx1991
 */
public interface TradingService {
    /**
     * Check if the market is open
     * @return
     */
    boolean isMarketOpen();

    /**
     * Get all tradable stocks
     * @return
     */
    Map<String, Set<String>> getAvailableStocks();
}
