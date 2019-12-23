package com.ultrader.bot.util;

import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.model.Order;
import com.ultrader.bot.service.TradingService;

import java.util.List;

public class OrderUtil {
    public static void loadHistoryOrders(OrderDao orderDao, TradingService tradingService) {
        List<Order> orders = tradingService.getHistoryOrders(null, null);
        for (Order order : orders) {
            if (!orderDao.findById(order.getId()).isPresent()) {
                orderDao.save(order);
            }
        }
    }
}
