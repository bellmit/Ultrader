package com.ultrader.bot.util;

import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.model.Account;
import com.ultrader.bot.model.Order;
import com.ultrader.bot.model.websocket.DashboardDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Notification Util
 * @author ytx1991
 */
public class NotificationUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationUtil.class);
    /**
     * Generate Dashboard account notification
     * @param account
     * @return
     */
    public static DashboardDataMessage generateAccountNotification(Account account) {
        DecimalFormat df = new DecimalFormat("#.##");
        Map<String, String> map = new HashMap<>();
        map.put("Portfolio", df.format(account.getPortfolioValue()));
        map.put("BuyingPower", df.format(account.getBuyingPower()));
        map.put("Cash", df.format(account.getCashWithdrawable()));
        LOGGER.info("Notify account update {}", map);
        return new DashboardDataMessage(map);
    }

    /**
     * Generate Dashboard account notification
     * @return
     */
    public static DashboardDataMessage generateTradesNotification(OrderDao orderDao) {
        DecimalFormat df = new DecimalFormat("#.##");
        Map<String, String> map = new HashMap<>();
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now(ZoneId.of(TradingUtil.TIME_ZONE));
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        LOGGER.info("Aggregate trades from {}", todayMidnight);
        List<Order> orders = orderDao.findAllOrdersByDate(todayMidnight, LocalDateTime.now(ZoneId.of(TradingUtil.TIME_ZONE)));
        double sell = 0, buy = 0;
        int sellCount = 0, buyCount = 0;
        for (Order order : orders) {
            if(order.getSide().equals("sell")) {
                sell += order.getAveragePrice() * order.getQuantity();
                sellCount++;
            } else {
                buy += order.getAveragePrice() * order.getQuantity();
                buyCount++;
            }
        }
        map.put("SellAmount", df.format(sell));
        map.put("BuyAmount", df.format(buy));
        map.put("SellCount", df.format(sellCount));
        map.put("BuyCount", df.format(buyCount));
        LOGGER.info("Notify trades update {}", map);
        return new DashboardDataMessage(map);
    }


    /**
     * Generate Dashboard profit notification
     * @return
     */
    public static DashboardDataMessage generateProfitNotification(OrderDao orderDao) {
        DecimalFormat df = new DecimalFormat("#.##");
        Map<String, String> map = new HashMap<>();
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now(ZoneId.of(TradingUtil.TIME_ZONE));
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        LOGGER.debug("Aggregate trades from {}", todayMidnight);
        List<Order> orders = orderDao.findAllOrdersByDate(todayMidnight, LocalDateTime.now(ZoneId.of(TradingUtil.TIME_ZONE)));
        double totalProfit = 0, totalRatio = 0;
        int sellCount = 0;
        for (Order order : orders) {
            if(order.getSide().equals("sell")) {
                List<Order> trades = orderDao.findLastTradeBySymbol(order.getSymbol());
                if(trades.size() == 2 && trades.get(0).getQuantity() == trades.get(1).getQuantity()) {
                    //There is a buy/sell pair and has same quantity
                    totalProfit += (trades.get(0).getAveragePrice() - trades.get(1).getAveragePrice()) * trades.get(0).getQuantity();
                    sellCount++;
                    totalRatio += trades.get(0).getAveragePrice() / trades.get(1).getAveragePrice() - 1;
                }

            }
        }
        map.put("TotalProfit", df.format(totalProfit));
        map.put("AverageProfit", df.format( sellCount == 0 ? 0 : totalProfit / sellCount));
        map.put("AverageProfitRatio", df.format(sellCount == 0 ? 0 : totalRatio / sellCount * 100));
        LOGGER.info("Notify profit update {}", map);
        return new DashboardDataMessage(map);
    }
}
