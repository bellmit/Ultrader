package com.ultrader.bot.service;

import com.ultrader.bot.dao.ChartDao;
import com.ultrader.bot.dao.NotificationDao;
import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.model.*;
import com.ultrader.bot.model.websocket.DashboardDataMessage;
import com.ultrader.bot.model.websocket.StatusMessage;
import com.ultrader.bot.monitor.LicenseMonitor;
import com.ultrader.bot.monitor.MarketDataMonitor;
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
import java.time.format.DateTimeFormatter;
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

    private LocalDateTime getEastCoastMidnight(long days) {
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate startDate = LocalDate.now(ZoneId.of(TradingUtil.TIME_ZONE)).minusDays(days - 1);
        LocalDateTime eastCoastMidnight = LocalDateTime.of(startDate, midnight);
        //Convert US east coast midnight to local time
        return eastCoastMidnight.atZone(ZoneId.of(TradingUtil.TIME_ZONE)).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    private List<Trade> getTradesByDate(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderDao.findAllOrdersByDate(startDate, endDate);
        List<Trade> trades = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            Order exitOrder = orders.get(i);
            if (exitOrder.getSide().equals("sell")) {
                Order entryOrder = null;
                for (int j = i + 1; j < orders.size(); j++) {
                    if (orders.get(j).getSide().equals("buy") && orders.get(j).getSymbol().equals(exitOrder.getSymbol()) && orders.get(j).getQuantity() == exitOrder.getQuantity()) {
                        entryOrder = orders.get(j);
                        break;
                    }
                }
                if (entryOrder == null) {
                    Instant instant = Instant.ofEpochSecond(exitOrder.getCloseDate().getTime() / 1000 + 1);
                    LocalDateTime sellDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    List<Order> trade = orderDao.findLastTradeBySymbol(exitOrder.getSymbol(), sellDate);
                    if (trade.size() == 2 && trade.get(1).getSide().equals("buy") && exitOrder.getQuantity() == trade.get(1).getQuantity()) {
                        entryOrder = trade.get(1);
                    } else {
                        LOGGER.error("Cannot find buy order for {}, sell date {}", exitOrder, sellDate);
                    }
                }

                if (entryOrder != null) {
                    //There is a buy/sell pair and has same quantity
                    LOGGER.debug("Stock {}, buy price {}, sell price{}, profit {}", exitOrder.getSymbol(), entryOrder.getAveragePrice(), exitOrder.getAveragePrice(), exitOrder.getQuantity());
                    trades.add(Trade.builder()
                            .symbol(exitOrder.getSymbol())
                            .buyDate(entryOrder.getCloseDate())
                            .buyPrice(entryOrder.getAveragePrice())
                            .sellDate(exitOrder.getCloseDate())
                            .sellPrice(exitOrder.getAveragePrice())
                            .qty(exitOrder.getQuantity())
                            .profit((exitOrder.getAveragePrice() - entryOrder.getAveragePrice()) * exitOrder.getQuantity())
                            .build());
                }

            }
        }
        Collections.reverse(trades);
        return trades;
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
     * Generate Dashboard profit notification
     *
     * @return
     */
    public DashboardDataMessage sendProfitNotification(int period, boolean refresh) {
        if (refresh || !profitMessageCache.containsKey(period)) {
            DecimalFormat df = new DecimalFormat("#.####");
            Map<String, String> map = new HashMap<>();
            double totalProfit = 0, totalRatio = 0;
            int sellCount = 0;
            List<Trade> trades = getTradesByDate(getEastCoastMidnight(period), LocalDateTime.now());
            for (Trade trade : trades) {
                totalProfit += trade.getProfit();
                sellCount++;
                totalRatio += trade.getSellPrice() / trade.getBuyPrice() - 1;
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
            String marketTrend = " Market Trend: " + MarketDataMonitor.getMarketTrend().name();
            if (isOpen) {
                notifier.convertAndSend("/topic/status/market", new StatusMessage("opened", "Market is open." + marketTrend));
            } else {
                notifier.convertAndSend("/topic/status/market", new StatusMessage("closed", "Market is closed." + marketTrend));
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

    public ChartResponse aggregatePortfolioChart(long length, long period, int step) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusSeconds(length * period);
        long offset = now.toEpochSecond(ZoneOffset.UTC) - new Date().getTime() / 1000;
        List<Chart> charts = chartDao.getDataByName(startDate, now, ChartType.Portfolio.name());
        ChartResponse response = new ChartResponse();
        response.setName("Portfolio");
        List<String> labels = new ArrayList<>();
        response.setLabels(labels);
        List<Double> values = new ArrayList<>();
        response.setSeries(Collections.singletonList(values));
        if (charts.size() == 0) {
            LOGGER.error("Cannot find any portfolio data.");
            return response;
        }
        Double lastValue = 0.0;
        DateTimeFormatter formatter;
        if (period > 24 * 3600) {
            formatter = DateTimeFormatter.ofPattern("MM/dd");
        } else if (period == 24 * 3600) {
            formatter = DateTimeFormatter.ofPattern("dd");
        } else if (period > 3600) {
            formatter = DateTimeFormatter.ofPattern("dd");
        } else {
            formatter = DateTimeFormatter.ofPattern("HH:mm");
        }

        startDate = startDate.plusSeconds(period);
        while (startDate.toEpochSecond(ZoneOffset.UTC) - offset < charts.get(0).getDate().getTime() / 1000) {
            values.add(0.0);
            labels.add(labels.size() % step == 0 ? startDate.format(formatter) : "");
            startDate = startDate.plusSeconds(period);
        }
        for (Chart chart : charts) {
            LOGGER.debug("start {} chart {}", startDate.toString(), chart.getDate().toString());
            if (chart.getDate().getTime() / 1000 < startDate.toEpochSecond(ZoneOffset.UTC) - offset) {
                lastValue = chart.getValue();
            } else {
                if (lastValue > 0) {
                    values.add(lastValue);
                    labels.add(labels.size() % step == 0 ? startDate.format(formatter) : "");
                }

                startDate = startDate.plusSeconds(period);
                lastValue = chart.getValue();
            }
        }
        return response;
    }

    public ChartResponse aggregateProfitChart(long length, long period, int step) {
        LocalDateTime startDate = getEastCoastMidnight(period * length / 86400);
        LocalDateTime endDate =LocalDateTime.now();
        long offset = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - new Date().getTime() / 1000;
        List<Trade> trades = getTradesByDate(startDate, endDate);
        ChartResponse response = new ChartResponse();
        response.setName("Profit");
        List<String> labels = new ArrayList<>();
        response.setLabels(labels);
        List<Double> values = new ArrayList<>();
        response.setSeries(Collections.singletonList(values));
        if (trades.size() == 0) {
            LOGGER.error("Cannot find any profit data.");
            return response;
        }
        DateTimeFormatter formatter;
        if (period > 24 * 3600) {
            formatter = DateTimeFormatter.ofPattern("MM/dd");
        } else if (period == 24 * 3600) {
            formatter = DateTimeFormatter.ofPattern("dd");
        } else if (period > 3600) {
            formatter = DateTimeFormatter.ofPattern("dd");
        } else {
            formatter = DateTimeFormatter.ofPattern("HH:mm");
        }
        labels.add(labels.size() % step == 0 ? startDate.format(formatter) : "");
        values.add(0.0);
        for (Trade trade : trades) {
            while (trade.getSellDate().getTime() / 1000 > startDate.toEpochSecond(ZoneOffset.UTC) - offset) {
                startDate = startDate.plusSeconds(period);
                labels.add(labels.size() % step == 0 ? startDate.format(formatter) : "");
                values.add(0.0);
            }
            values.set(values.size() - 1, values.get(values.size()-1) + trade.getProfit());
        }
        while (endDate.isAfter(startDate)) {
            startDate = startDate.plusSeconds(period);
            labels.add(labels.size() % step == 0 ? startDate.format(formatter) : "");
            values.add(0.0);
        }
        return response;
    }

    public ChartResponse aggregateTradeChart(long length, long period, int step) {
        LocalDateTime startDate = getEastCoastMidnight(period * length / 86400);
        LocalDateTime endDate =LocalDateTime.now();
        long offset = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - new Date().getTime() / 1000;
        List<Trade> trades = getTradesByDate(startDate, endDate);
        ChartResponse response = new ChartResponse();
        response.setName("Trade");
        List<String> labels = new ArrayList<>();
        response.setLabels(labels);
        List<Double> values = new ArrayList<>();
        response.setSeries(Collections.singletonList(values));
        if (trades.size() == 0) {
            LOGGER.error("Cannot find any trade data.");
            return response;
        }
        DateTimeFormatter formatter;
        if (period > 24 * 3600) {
            formatter = DateTimeFormatter.ofPattern("MM/dd");
        } else if (period == 24 * 3600) {
            formatter = DateTimeFormatter.ofPattern("dd");
        } else if (period > 3600) {
            formatter = DateTimeFormatter.ofPattern("dd");
        } else {
            formatter = DateTimeFormatter.ofPattern("HH:mm");
        }
        labels.add(labels.size() % step == 0 ? startDate.format(formatter) : "");
        values.add(0.0);
        for (Trade trade : trades) {

            while (trade.getSellDate().getTime() / 1000 > startDate.toEpochSecond(ZoneOffset.UTC) - offset) {
                startDate = startDate.plusSeconds(period);
                labels.add(labels.size() % step == 0 ? startDate.format(formatter) : "");
                values.add(0.0);
            }
            values.set(values.size() - 1, values.get(values.size()-1) + 1);
        }
        while (endDate.isAfter(startDate)) {
            startDate = startDate.plusSeconds(period);
            labels.add(labels.size() % step == 0 ? startDate.format(formatter) : "");
            values.add(0.0);
        }
        return response;
    }

    public SimpMessagingTemplate getNotifier() {
       return this.notifier;
    }
}
