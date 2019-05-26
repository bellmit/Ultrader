package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.Account;
import com.ultrader.bot.model.Order;
import com.ultrader.bot.model.Position;
import com.ultrader.bot.model.websocket.DashboardDataMessage;
import com.ultrader.bot.service.TradingService;
import com.ultrader.bot.service.alpaca.AlpacaWebSocketHandler;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingUtil;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.ta4j.core.BaseBar;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.num.PrecisionNum;

import java.text.DecimalFormat;
import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sync Account information
 *
 * @author ytx1991
 */
public class TradingAccountMonitor extends Monitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradingAccountMonitor.class);
    private static TradingAccountMonitor singleton_instance = null;
    private static Map<String, Position> positions = new HashMap<>();
    private static Account account = new Account();
    private final TradingService tradingService;
    private final SettingDao settingDao;
    private final OrderDao orderDao;
    private final SimpMessagingTemplate notifier;


    private TradingAccountMonitor(long interval,
                                  final TradingService tradingService,
                                  final SettingDao settingDao,
                                  final SimpMessagingTemplate notifier,
                                  final OrderDao orderDao) {
        super(interval);
        Validate.notNull(tradingService, "tradingService is required");
        Validate.notNull(settingDao, "settingDao is required");
        Validate.notNull(notifier, "notifier is required");
        Validate.notNull(orderDao, "orderDao is required");
        this.tradingService = tradingService;
        this.settingDao = settingDao;
        this.notifier = notifier;
        this.orderDao = orderDao;
    }

    public static void init(long interval,
                            final TradingService tradingService,
                            final SettingDao settingDao,
                            final SimpMessagingTemplate notifier,
                            final OrderDao orderDao) {
        singleton_instance = new TradingAccountMonitor(interval, tradingService, settingDao, notifier, orderDao);
    }

    public static TradingAccountMonitor getInstance() throws IllegalAccessException {
        if (singleton_instance == null) {
            LOGGER.error("Instance is not initialized.");
            throw new IllegalAccessException("Instance is not initialized.");
        }
        return singleton_instance;
    }

    private Map<String, Position> getAllPositions() throws RuntimeException {
        Map<String, Position> positionMap = tradingService.getAllPositions();
        if (positionMap == null) {
            throw new RuntimeException("Cannot get position info, skip executing trading strategies");
        }
        LOGGER.info(String.format("Found %d positions.", positionMap.size()));
        return positionMap;
    }

    public static Map<String, Position> getPositions() {
        return positions;
    }

    public static Account getAccount() {
        return account;
    }

    @Override
    void scan() {
        try {
            if (!LicenseMonitor.getInstance().isValidLicense()) {
                LOGGER.error("Invalid license or expired license");
                return;
            }
            //Get current position
            positions = getAllPositions();
            if (RepositoryUtil.getSetting(settingDao, SettingConstant.MARKET_DATA_PLATFORM.getName(), "IEX").equals("IEX")) {
                //Add inter-bar price to position stocks
                synchronized (MarketDataMonitor.lock) {
                    for (String stock : positions.keySet()) {
                        addRealtimePrice(stock);
                    }
                }
            }

            //Get current portfolio
            account = syncAccount();
            //Populate Dashboard Message
            notifier.convertAndSend("/topic/dashboard/account", generateAccountNotification(account));
            notifier.convertAndSend("/topic/dashboard/trades", generateTradesNotification());
            notifier.convertAndSend("/topic/dashboard/profit", generateProfitNotification());


        } catch (Exception e) {
            LOGGER.error("Update trading account failed.", e);
        }
    }

    /**
     * Add a realtime bar in the time series, this should be called in a synchronized block
     *
     * @param stock
     */
    private void addRealtimePrice(String stock) {
        TimeSeries timeSeries = MarketDataMonitor.timeSeriesMap.get(stock);
        if (timeSeries == null || timeSeries.getBarCount() == 0) {
            return;
        }
        //Update the realtime bar
        timeSeries.getLastBar().addPrice(PrecisionNum.valueOf(positions.get(stock).getCurrentPrice()));
        LOGGER.debug("Updated real time price. {}", timeSeries.getLastBar());
    }

    /**
     * Sync user trading account with platform
     *
     * @return
     * @throws RuntimeException
     */
    private Account syncAccount() throws RuntimeException {
        Account account = tradingService.getAccountInfo();
        if (account == null) {
            throw new RuntimeException("Cannot get account info, skip executing trading strategies");
        }
        if (account.isTradingBlocked()) {
            throw new RuntimeException("Your api account is blocked trading, please check the trading platform account.");
        }
        return account;
    }

    /**
     * Generate Dashboard account notification
     * @param account
     * @return
     */
    private DashboardDataMessage generateAccountNotification(Account account) {
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
    private DashboardDataMessage generateTradesNotification() {
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
    private DashboardDataMessage generateProfitNotification() {
        DecimalFormat df = new DecimalFormat("#.##");
        Map<String, String> map = new HashMap<>();
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now(ZoneId.of(TradingUtil.TIME_ZONE));
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        LOGGER.info("Aggregate trades from {}", todayMidnight);
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
