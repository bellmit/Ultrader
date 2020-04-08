package com.ultrader.bot.util;

import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.model.Order;
import com.ultrader.bot.service.TradingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class OrderUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderUtil.class);
    public static void loadHistoryOrders(OrderDao orderDao, TradingService tradingService) {
        List<Order> orders = tradingService.getHistoryOrders(null, null);
        for (Order order : orders) {
            Optional<Order> o = orderDao.findById(order.getId());
            if (!o.isPresent()) {
                orderDao.save(order);
                LOGGER.info("Loaded order {}", order);
            } else {
                //Update
                o.get().setAveragePrice(order.getAveragePrice());
                o.get().setCloseDate(order.getCloseDate());
                o.get().setQuantity(order.getQuantity());
                orderDao.save(o.get());
            }
        }
    }
}
