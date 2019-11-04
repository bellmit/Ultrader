package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.*;
import com.ultrader.bot.model.*;
import com.ultrader.bot.model.websocket.StatusMessage;
import com.ultrader.bot.service.NotificationService;
import com.ultrader.bot.service.TradingService;
import com.ultrader.bot.util.NotificationType;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.num.PrecisionNum;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Sync Account information
 *
 * @author ytx1991
 */
public class TradingAccountMonitor extends Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradingAccountMonitor.class);
    private static final int CHART_UPDATE_PERIOD_SECONDS = 60;
    private static int chartUpdateTicker = 0;
    private static TradingAccountMonitor singleton_instance = null;
    private static Map<String, Position> positions = new HashMap<>();
    private static Account account = new Account();
    private final TradingService tradingService;
    private final SettingDao settingDao;
    private final OrderDao orderDao;
    private final NotificationService notifier;
    private final ChartDao chartDao;
    private final PositionDao positionDao;

    private TradingAccountMonitor(long interval,
                                  final TradingService tradingService,
                                  final SettingDao settingDao,
                                  final NotificationService notifier,
                                  final OrderDao orderDao,
                                  final ChartDao chartDao,
                                  final PositionDao positionDao) {
        super(interval);
        Validate.notNull(tradingService, "tradingService is required");
        Validate.notNull(settingDao, "settingDao is required");
        Validate.notNull(notifier, "notifier is required");
        Validate.notNull(orderDao, "orderDao is required");
        Validate.notNull(chartDao, "charDao is required");
        Validate.notNull(positionDao, "positionDao is required");
        this.tradingService = tradingService;
        this.settingDao = settingDao;
        this.notifier = notifier;
        this.orderDao = orderDao;
        this.chartDao = chartDao;
        this.positionDao = positionDao;
        //Load missing orders
        try {
            orderDao.saveAll(tradingService.getHistoryOrders(null, null));
        } catch (Exception e) {
            LOGGER.error("Load history orders failed.");
        }
    }

    public static void init(long interval,
                            final TradingService tradingService,
                            final SettingDao settingDao,
                            final NotificationService notifier,
                            final OrderDao orderDao,
                            final ChartDao chartDao,
                            final PositionDao positionDao) {
        singleton_instance = new TradingAccountMonitor(interval,
                tradingService,
                settingDao,
                notifier,
                orderDao,
                chartDao,
                positionDao);
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
        int updateCount = 0, deleteCount = 0, addCount = 0;
        //Update database
        Set<String> existedStock = new HashSet<>();
        Iterable<Position> positions = positionDao.findAll();
        for (Position position : positions) {
            if(positionMap.containsKey(position.getSymbol())) {
                //Update existed stocks
                Position newPosition = positionMap.get(position.getSymbol());
                newPosition.setBuyDate(position.getBuyDate());
                positionDao.save(newPosition);
                existedStock.add(newPosition.getSymbol());
                updateCount++;
            } else {
                //Delete sold stocks
                positionDao.delete(position);
                deleteCount++;
            }
        }

        for (Position position : positionMap.values()) {
            if(!existedStock.contains(position.getSymbol())) {
                //Add New stocks
                List<Order> orders = orderDao.findLastTradeBySymbol(position.getSymbol(), LocalDateTime.now());
                if (orders.size() > 0 && orders.get(0).getSide().equals(com.ultrader.bot.model.alpaca.Order.BUY)) {
                    position.setBuyDate(orders.get(0).getCloseDate());
                } else {
                    position.setBuyDate(new Date());
                }

                positionDao.save(position);
                addCount++;
            }
        }
        LOGGER.info(String.format("Positions: Add %d, Delete %d, Update %d.", addCount, deleteCount, updateCount));
        return positionMap;
    }

    public static Map<String, Position> getPositions() {
        return positions;
    }

    public static Account getAccount() {
        return account;
    }

    @Override
    synchronized void scan() {
        try {
            if (!LicenseMonitor.getInstance().isValidLicense()) {
                LOGGER.error("Invalid license or expired license");
                return;
            }
            //Get current portfolio
            syncAccount();

            if (RepositoryUtil.getSetting(settingDao, SettingConstant.MARKET_DATA_PLATFORM.getName(), "IEX").equals("IEX")) {
                //Add inter-bar price to position stocks
                synchronized (MarketDataMonitor.lock) {
                    for (String stock : positions.keySet()) {
                        addRealtimePrice(stock);
                    }
                }
            }
            if(!tradingService.checkWebSocket()) {
                tradingService.restart();
            }
            if (chartUpdateTicker % CHART_UPDATE_PERIOD_SECONDS == 0) {
                List<Chart> lastOne = chartDao.getLastId();
                long id = lastOne.size() > 0 ? (lastOne.get(0).getId()) + 1 : 1000;
                Chart chart = new Chart();
                chart.setDate(new Date());
                chart.setSerialName("Portfolio");
                chart.setValue(account.getPortfolioValue());
                chart.setId(id);
                chartDao.save(chart);
                chartUpdateTicker = 0;
            }
            chartUpdateTicker += getInterval() / 1000;

            //Populate Dashboard Message
            notifier.sendAccountNotification(account);
            notifier.sendProfitNotification(1, false);
            notifier.sendProfitNotification(7, false);
            notifier.sendProfitNotification(30, false);
            notifier.sendProfitNotification(365, false);
            notifier.sendPositionNotification();
            //Publish status
            notifier.sendMarketStatus(MarketDataMonitor.isMarketOpen());
        } catch (Exception e) {
            LOGGER.error("Update trading account failed.", e);
            notifier.sendNotification(
                    "Account Update Failure",
                    "Cannot update your trading account, please check your account and reboot the bot.",
                    NotificationType.ERROR);
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
    public void syncAccount() throws RuntimeException {
        account = tradingService.getAccountInfo();
        //Get current position
        positions = getAllPositions();
        LOGGER.info("Account {}", account);
        if (account == null) {
            throw new RuntimeException("Cannot get account info, skip executing trading strategies");
        }
        if (account.isTradingBlocked()) {
            throw new RuntimeException("Your api account is blocked trading, please check the trading platform account.");
        }
    }
}
