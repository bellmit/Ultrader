package com.ultrader.bot.controller;

import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.model.Account;
import com.ultrader.bot.model.Order;
import com.ultrader.bot.model.Position;
import com.ultrader.bot.model.Trade;
import com.ultrader.bot.monitor.TradingAccountMonitor;
import com.ultrader.bot.monitor.TradingStrategyMonitor;
import com.ultrader.bot.service.TradingPlatform;
import com.ultrader.bot.util.OrderUtil;
import com.ultrader.bot.util.TradingUtil;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Order Controller
 * @author ytx1991
 */

@RequestMapping("/api/order")
@RestController("OrderController")
public class OrderController {
    private static Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private TradingPlatform tradingPlatform;

    @Autowired
    private OrderDao orderDao;

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
    @RequestMapping(method = RequestMethod.GET, value = "/loadOrders")
    @ResponseBody
    public void loadOrders() {
        try {
            OrderUtil.loadHistoryOrders(orderDao, tradingPlatform.getTradingService());
        } catch (Exception e) {
            LOGGER.error("Load orders failed.", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/liquid")
    @ResponseBody
    public void liquid(@RequestParam String assets) {
        try {
            Account account = TradingAccountMonitor.getAccount();
            for (String asset : assets.split(",")) {
                //sell strategy satisfy & has position
                Position position = TradingAccountMonitor.getPositions().get(asset);
                if (tradingPlatform.getTradingService().postOrder(new com.ultrader.bot.model.Order("", asset, "sell", "market", position.getQuantity(), position.getCurrentPrice(), "", null, "Manual Sell", null)) != null) {
                    account.setBuyingPower(account.getBuyingPower() + position.getQuantity() * position.getQuantity());
                    LOGGER.info(String.format("Sell %s %d shares at price %f.", asset, position.getQuantity(), position.getCurrentPrice()));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Liquid assets failed.", e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getClosedOrders/{days}")
    @ResponseBody
    public Iterable<Trade> getClosedOrders(@PathVariable int days) {
        try {
           List<Order> orders = orderDao.findAllOrdersByDate(LocalDateTime.now().minusDays(days), LocalDateTime.now());
           List<Trade> trades = new ArrayList<>();
           Map<String, Order> stack = new HashMap<>();
           for(Order order : orders) {
               if(order.getSide().equals("sell")) {
                   stack.put(order.getSymbol(), order);
               } else {
                   if(stack.containsKey(order.getSymbol())) {
                       Order sellOrder = stack.get(order.getSymbol());
                       if(sellOrder.getQuantity() != order.getQuantity()) {
                           LOGGER.error("{} Buy & Sell quantity are not equal.", order.getSymbol());
                       }
                       trades.add(new Trade(
                               order.getSymbol(),
                               order.getQuantity(),
                               order.getCloseDate(),
                               sellOrder.getCloseDate(),
                               order.getAveragePrice(),
                               sellOrder.getAveragePrice(),
                               (sellOrder.getAveragePrice() - order.getAveragePrice()) * order.getQuantity(),
                               order.getReason(),
                               sellOrder.getReason()));

                   }
               }
           }
           return trades;
        } catch (Exception e) {
            LOGGER.error("Load orders failed.", e);
            return null;
        }
    }
}
