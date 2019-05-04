package com.ultrader.bot.controller;

import com.ultrader.bot.model.Order;
import com.ultrader.bot.monitor.TradingStrategyMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Order Controller
 * @author ytx1991
 */

@RequestMapping("/api/order")
@RestController
public class OrderController {
    private static Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/getOpenOrders")
    @ResponseBody
    public Iterable<Order> getOpenOrders() {
        try {
            Iterable<Order> orders = TradingStrategyMonitor.getOpenOrders().values();
            return orders;
        } catch (Exception e) {
            LOGGER.error("Get open orders failed.", e);
            return null;
        }
    }

}
