package com.ultrader.bot.service;

import com.ultrader.bot.dao.ChartDao;
import com.ultrader.bot.dao.NotificationDao;
import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.model.*;
import com.ultrader.bot.model.websocket.DashboardDataMessage;
import com.ultrader.bot.model.websocket.StatusMessage;
import com.ultrader.bot.monitor.LicenseMonitor;
import com.ultrader.bot.monitor.TradingAccountMonitor;
import com.ultrader.bot.util.ChartType;
import com.ultrader.bot.util.NotificationType;
import com.ultrader.bot.util.TradingUtil;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.*;
import java.util.*;

@Service("NotificationService")
public class NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private final ChartDao chartDao;
    private final OrderDao orderDao;
    private final SimpMessagingTemplate notifier;
    private final NotificationDao notificationDao;
    private Map<Integer, DashboardDataMessage> profitMessageCache = new HashMap<>();

    @Autowired
    public NotificationService(OrderDao orderDao, ChartDao chartDao, NotificationDao notificationDao, SimpMessagingTemplate notifier) {
        Validate.notNull(orderDao, "OrderDao is required");
        Validate.notNull(chartDao, "chartDao is required");
        Validate.notNull(notificationDao, "notificationDao is required");
        Validate.notNull(notifier, "notifier is required");
        this.orderDao = orderDao;
        this.chartDao = chartDao;
        this.notificationDao = notificationDao;
        this.notifier = notifier;
    }

    /**
     * Generate Dashboard account notification
     *
     * @param account
     * @return
     */
    public DashboardDataMessage sendAccountNotification(Account account) throws IllegalAccessException {
        DecimalFormat df = new DecimalFormat("#.##");
        Map<String, String> map = new HashMap<>();
        List<Chart> now = chartDao.getDataByDate(LocalDateTime.now(), ChartType.Portfolio.name());
        List<Chart> before = chartDao.getDataByDate(LocalDateTime.now().minusDays(1), ChartType.Portfolio.name());
        Double change = 0.0;
        if (now.size() > 0 && before.size() > 0 && account.getPortfolioValue() > 0) {
            change = now.get(0).getValue() - before.get(0).getValue();
        }
        map.put("Portfolio", df.format(account.getPortfolioValue()));
        map.put("BuyingPower", df.format(account.getBuyingPower()));
        map.put("Change", df.format(change));
        map.put("status", LicenseMonitor.getInstance().isValidLicense() ? account.getStatus() : "Invalid License");
        map.put("IsTradingBlock", String.valueOf(account.isTradingBlocked()));
        LOGGER.debug("Notify account update {}", map);
        DashboardDataMessage message = new DashboardDataMessage(map);
        notifier.convertAndSend("/topic/dashboard/account", message);
        return message;
    }

    /**
     * Generate Dashboard account notification
     *
     * @return
     */
    public DashboardDataMessage sendTradesNotification() {
        DecimalFormat df = new DecimalFormat("#.##");
        Map<String, String> map = new HashMap<>();
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now(ZoneId.of(TradingUtil.TIME_ZONE));
        LocalDateTime eastCoastMidnight = LocalDateTime.of(today, midnight);
        //Convert US east coast midnight to local time
        LocalDateTime localMidnight = eastCoastMidnight.atZone(ZoneId.of(TradingUtil.TIME_ZONE)).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        LOGGER.debug("Aggregate trades from {}", localMidnight);
        List<Order> orders = orderDao.findAllOrdersByDate(localMidnight, LocalDateTime.now());
        double sell = 0, buy = 0;
        int sellCount = 0, buyCount = 0;
        for (Order order : orders) {
            if (order.getSide().equals("sell")) {
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
        LOGGER.debug("Notify trades update {}", map);
        DashboardDataMessage message = new DashboardDataMessage(map);
        notifier.convertAndSend("/topic/dashboard/trades", message);
        return message;
    }


    /**
     * Generate Dashboard profit notification
     *
     * @return
     */
    public DashboardDataMessage sendProfitNotification(int period, boolean refresh) {
        if (refresh || !profitMessageCache.containsKey(period)) {
            DecimalFormat df = new DecimalFormat("#.####");
            Map<String, String> map = new HashMap<>();
            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalDate startDate = LocalDate.now(ZoneId.of(TradingUtil.TIME_ZONE)).minusDays(period-1);
            LocalDateTime eastCoastMidnight = LocalDateTime.of(startDate, midnight);
            //Convert US east coast midnight to local time
            LocalDateTime localMidnight = eastCoastMidnight.atZone(ZoneId.of(TradingUtil.TIME_ZONE)).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            LOGGER.debug("Aggregate trades from {}", localMidnight);
            List<Order> orders = orderDao.findAllOrdersByDate(localMidnight, LocalDateTime.now());
            double totalProfit = 0, totalRatio = 0;
            int sellCount = 0;
            for (int i = 0; i < orders.size(); i++) {
                Order sellOrder = orders.get(i);
                if (sellOrder.getSide().equals("sell")) {
                    Order buyOrder = null;
                    for (int j = i + 1; j < orders.size(); j++) {
                        if (orders.get(j).getSide().equals("buy") && orders.get(j).getSymbol().equals(sellOrder.getSymbol()) && orders.get(j).getQuantity() == sellOrder.getQuantity()) {
                            buyOrder = orders.get(j);
                            break;
                        }
                    }
                    if (buyOrder == null) {
                        Instant instant = Instant.ofEpochSecond(sellOrder.getCloseDate().getTime() / 1000 + 1);
                        LocalDateTime sellDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                        List<Order> trade = orderDao.findLastTradeBySymbol(sellOrder.getSymbol(), sellDate);
                        if (trade.size() == 2 && trade.get(1).getSide().equals("buy") && sellOrder.getQuantity() == trade.get(1).getQuantity()) {
                            buyOrder = trade.get(1);
                        } else {
                            LOGGER.error("Cannot find buy order for {}, sell date {}", sellOrder, sellDate);
                        }
                    }

                    if (buyOrder != null) {
                        //There is a buy/sell pair and has same quantity
                        LOGGER.debug("Stock {}, buy price {}, sell price{}, profit {}, total {}", sellOrder.getSymbol(), buyOrder.getAveragePrice(), sellOrder.getAveragePrice(), sellOrder.getQuantity(), totalProfit);
                        totalProfit += (sellOrder.getAveragePrice() - buyOrder.getAveragePrice()) * sellOrder.getQuantity();
                        sellCount++;
                        totalRatio += sellOrder.getAveragePrice() / buyOrder.getAveragePrice() - 1;
                    }

                }
            }
            map.put("PeriodDays", String.valueOf(period));
            map.put("TotalTrades", String.valueOf(sellCount));
            map.put("TotalProfit", df.format(totalProfit));
            map.put("AverageProfit", df.format(sellCount == 0 ? 0 : (totalProfit / sellCount)));
            map.put("AverageProfitRatio", df.format(sellCount == 0 ? 0 : (totalRatio / sellCount)));
            LOGGER.debug("Notify profit update {}", map);
            DashboardDataMessage message = new DashboardDataMessage(map);
            profitMessageCache.put(period, message);
            notifier.convertAndSend("/topic/dashboard/profit", message);
            return message;
        } else {
            notifier.convertAndSend("/topic/dashboard/profit", profitMessageCache.get(period));
            return profitMessageCache.get(period);
        }

    }

    public DashboardDataMessage sendPositionNotification() {
        Map<String, String> map = new HashMap<>();
        map.put("Holds", String.valueOf(TradingAccountMonitor.getPositions().size()));
        int profitableStock = 0;
        double profit = 0;
        for (Position position : TradingAccountMonitor.getPositions().values()) {
            if (position.getAverageCost() <= position.getCurrentPrice()) {
                profitableStock++;
            }
            profit += (position.getCurrentPrice() - position.getAverageCost()) * position.getQuantity();
        }
        map.put("ProfitableStock", String.valueOf(profitableStock));
        map.put("Profit", String.valueOf(profit));
        DashboardDataMessage message = new DashboardDataMessage(map);
        notifier.convertAndSend("/topic/dashboard/position", message);
        return message;
    }

    public void sendNotification(String title, String content, NotificationType type) {
        try {
            Notification notification = new Notification(
                    UUID.randomUUID().toString(),
                    type.name(),
                    content,
                    title,
                    new Date());
            notificationDao.save(notification);
            notifier.convertAndSend("/topic/notification", notification);
        } catch (Exception e) {
            LOGGER.error("Send notification title {}, content {}, type {} failed.", title, content, type);
        }
    }

    public void sendMarketStatus(boolean isOpen) {
        try {
            if (isOpen) {
                notifier.convertAndSend("/topic/status/market", new StatusMessage("opened", "Market is open"));
            } else {
                notifier.convertAndSend("/topic/status/market", new StatusMessage("closed", "Market is closed"));
            }
        } catch (Exception e) {
            LOGGER.error("Send market status isOpen {} failed.", isOpen);
        }
    }

    public void sendMarketDataStatus(String status, int validAsset, int totalAsset) {
        try {
            switch (status) {
                case "normal":
                    notifier.convertAndSend("/topic/status/data", new StatusMessage("normal", String.format("Most assets have latest data. Found %d assets, %d valid.", totalAsset, validAsset)));
                    break;
                case "warning":
                    notifier.convertAndSend("/topic/status/data", new StatusMessage("warning", String.format("Some assets don't have latest data. Found %d assets, %d valid.", totalAsset, validAsset)));
                    break;
                case "error":
                    notifier.convertAndSend("/topic/status/data", new StatusMessage("error", String.format("Most assets don't have latest data. Found %d assets, %d valid.", totalAsset, validAsset)));
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Send market data status {} failed.", status);
        }
    }
}
