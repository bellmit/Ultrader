package com.ultrader.bot.service;

import com.ultrader.bot.model.Account;
import com.ultrader.bot.model.Order;
import com.ultrader.bot.model.Position;
import com.ultrader.bot.model.alpaca.Asset;

import java.util.Map;
import java.util.Set;

/**
 * Order Service Interface
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

    /**
     * Get all current position
     * @return
     */
    Map<String, Position> getAllPositions();

    Account getAccountInfo();
    /**
     * Post an Order
     * @param order
     * @return
     */
    Order postOrder(Order order);
}
