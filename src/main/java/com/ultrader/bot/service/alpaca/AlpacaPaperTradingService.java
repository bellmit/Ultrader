package com.ultrader.bot.service.alpaca;

import com.ultrader.bot.dao.ChartDao;
import com.ultrader.bot.dao.NotificationDao;
import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.Account;
import com.ultrader.bot.model.Order;
import com.ultrader.bot.model.Position;
import com.ultrader.bot.model.alpaca.Asset;
import com.ultrader.bot.model.alpaca.Clock;
import com.ultrader.bot.service.TradingService;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Alpaca Order API
 * @author ytx1991
 */
@Service("AlpacaPaperTradingService")
public class AlpacaPaperTradingService implements TradingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaPaperTradingService.class);
    private String alpacaKey;
    private String alpacaSecret;
    private final RestTemplate client;
    private WebSocketConnectionManager connectionManager;
    private final OrderDao orderDao;
    private final ChartDao chartDao;
    private final SimpMessagingTemplate notifier;
    private final SettingDao settingDao;
    private final NotificationDao notificationDao;

    @Autowired
    public AlpacaPaperTradingService(final SettingDao settingDao,
                                     final RestTemplateBuilder restTemplateBuilder,
                                     final OrderDao orderDao,
                                     final ChartDao chartDao,
                                     final SimpMessagingTemplate notifier,
                                     final NotificationDao notificationDao) {
        Validate.notNull(restTemplateBuilder, "restTemplateBuilder is required");
        Validate.notNull(settingDao, "settingDao is required");
        Validate.notNull(orderDao, "orderDao is required");
        Validate.notNull(chartDao, "chartDao is required");
        Validate.notNull(notificationDao, "notificationDao is required");
        this.settingDao = settingDao;
        this.alpacaKey = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_PAPER_KEY.getName(), "");
        this.alpacaSecret = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_PAPER_SECRET.getName(), "");
        this.orderDao = orderDao;
        this.chartDao = chartDao;
        this.notificationDao = notificationDao;
        if(alpacaKey.equals("") || alpacaSecret.equals("")) {
            //It can be the first time setup
            LOGGER.warn("Cannot find Alpaca API key, please check our config");
        }
        this.notifier = notifier;
        client = restTemplateBuilder.rootUri("https://paper-api.alpaca.markets/v1/").build();
        if (!alpacaKey.isEmpty() && !alpacaSecret.isEmpty()) {
            //Init Websocket
            connectionManager = new WebSocketConnectionManager(new StandardWebSocketClient(), new AlpacaWebSocketHandler(alpacaKey, alpacaSecret, orderDao, notificationDao, chartDao, notifier), "wss://paper-api.alpaca.markets/stream");
            connectionManager.start();
        }

    }

    private HttpHeaders generateHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("APCA-API-KEY-ID", alpacaKey);
        httpHeaders.set("APCA-API-SECRET-KEY", alpacaSecret);
        return httpHeaders;
    }
    @Override
    public boolean checkWebSocket() {
        if(connectionManager != null && !connectionManager.isRunning()) {
            LOGGER.error("Alpaca websocket listener is not working.");
            return false;
        }
        return true;
    }

    @Override
    public void restart() {
        this.alpacaKey = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_PAPER_KEY.getName(), "");
        this.alpacaSecret = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_PAPER_SECRET.getName(), "");
        if(alpacaKey.equals("") || alpacaSecret.equals("")) {
            //It can be the first time setup
            LOGGER.warn("Cannot find Alpaca API key, please check our config");
        }
        try {
            connectionManager.stopInternal();
        } catch (Exception e) {
            LOGGER.error("Terminate Alpaca web socket", e);
        }
        if (!alpacaKey.isEmpty() && !alpacaSecret.isEmpty()) {
            //Init Websocket
            connectionManager = new WebSocketConnectionManager(new StandardWebSocketClient(), new AlpacaWebSocketHandler(alpacaKey, alpacaSecret, orderDao, notificationDao, chartDao, notifier), "wss://paper-api.alpaca.markets/stream");
            connectionManager.start();
        }

    }

    public boolean isMarketOpen() {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(generateHeader());
            ResponseEntity<Clock> clock = client.exchange("/clock", HttpMethod.GET, entity, Clock.class);
            if (clock.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                return false;
            }
            LOGGER.debug(clock.getBody().toString());
            return clock.getBody().getIs_open();
        } catch (Exception e) {
            LOGGER.error("Failed to call /clock api.", e);
            return false;
        }
    }

    public Map<String, Set<String>> getAvailableStocks() {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(generateHeader());
            ResponseEntity<Asset[]> stocks = client.exchange("/assets?status=active", HttpMethod.GET, entity, Asset[].class);
            if (stocks.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                return new HashMap<>();
            }
            Map<String, Set<String>> exchangeStockMap = new HashMap<>();
            for(Asset asset : stocks.getBody()) {
                if(!exchangeStockMap.containsKey(asset.getExchange())) {
                    exchangeStockMap.put(asset.getExchange(), new HashSet<String>());
                }
                if(asset.getTradable()) {
                    exchangeStockMap.get(asset.getExchange()).add(asset.getSymbol());
                }
            }
            return exchangeStockMap;
        } catch (Exception e) {
            LOGGER.error("Failed to call /assets api.", e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Position> getAllPositions() {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(generateHeader());
            ResponseEntity<com.ultrader.bot.model.alpaca.Position[]> positions = client.exchange("/positions", HttpMethod.GET, entity, com.ultrader.bot.model.alpaca.Position[].class);
            if (positions.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                return null;
            }
            Map<String, Position> positionMap = new HashMap<>();
            for (com.ultrader.bot.model.alpaca.Position position : positions.getBody()) {
                positionMap.put(position.getSymbol(), new Position(position.getSymbol(), position.getQty(), position.getAvg_entry_price(), null, position.getCurrent_price(), position.getExchange(), position.getMarket_value(), position.getChange_today()));
            }
            return positionMap;
        } catch (Exception e) {
            LOGGER.error("Failed to call /positions api.", e);
            return null;
        }
    }

    @Override
    public Account getAccountInfo() {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(generateHeader());
            ResponseEntity<com.ultrader.bot.model.alpaca.Account> account = client.exchange("/account", HttpMethod.GET, entity, com.ultrader.bot.model.alpaca.Account.class);
            if (account.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                return null;
            }
            return new Account(account.getBody().getId(),
                    account.getBody().getStatus(),
                    account.getBody().getCurrency(),
                    account.getBody().getBuying_power(),
                    account.getBody().getCash(),
                    account.getBody().getCash_withdrawable(),
                    account.getBody().getPortfolio_value(),
                    account.getBody().isPattern_day_trader(),
                    account.getBody().isTrading_blocked(),
                    account.getBody().isTransfers_blocked(),
                    account.getBody().isAccount_blocked());
        } catch (Exception e) {
            LOGGER.error("Failed to call /account api.", e);
            return null;
        }
    }

    @Override
    public Order postOrder(Order order) {
        try {
            Map<String, String> request = new HashMap<>();
            DecimalFormat df = new DecimalFormat("#.00");
            request.put("symbol", order.getSymbol());
            request.put("qty", String.valueOf(order.getQuantity()));
            request.put("type", order.getType());
            request.put("side", order.getSide());
            request.put("time_in_force", "gtc");
            if(order.getType().equals("limit")) {
                request.put("limit_price", df.format(order.getAveragePrice()));
            }
            HttpEntity<Map<String,String>> entity = new HttpEntity<>(request, generateHeader());
            ResponseEntity<com.ultrader.bot.model.alpaca.Order> orderResponseEntity = client.exchange("/orders", HttpMethod.POST, entity, com.ultrader.bot.model.alpaca.Order.class);
            if (orderResponseEntity.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                return null;
            }
            Order responseOrder = new Order(
                    orderResponseEntity.getBody().getId(),
                    orderResponseEntity.getBody().getSymbol(),
                    orderResponseEntity.getBody().getSide(),
                    orderResponseEntity.getBody().getType(),
                    orderResponseEntity.getBody().getQty(),
                    order.getAveragePrice(),
                    orderResponseEntity.getBody().getStatus(),
                    null);
            return responseOrder;
        } catch (Exception e) {
            LOGGER.error("Failed to call /orders api.", e);
            return null;
        }
    }

    @Override
    public Map<String, Order> getOpenOrders() {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(generateHeader());
            ResponseEntity<com.ultrader.bot.model.alpaca.Order[]> orders = client.exchange("/orders?limit=500", HttpMethod.GET, entity, com.ultrader.bot.model.alpaca.Order[].class);
            if (orders.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                return null;
            }
            Map<String, Order> orderMap = new HashMap<>();
            for (com.ultrader.bot.model.alpaca.Order order : orders.getBody()) {
                LOGGER.debug("Found open order {}", order.toString());
                orderMap.put(order.getSymbol(),new Order(
                        order.getId(),
                        order.getSymbol(),
                        order.getSide(),
                        order.getType(),
                        order.getQty(),
                        order.getLimit_price(),
                        order.getStatus(),
                        null));
            }
            return orderMap;
        } catch (Exception e) {
            LOGGER.error("Failed to get open orders.", e);
            return null;
        }
    }

    @Override
    public List<Order> getHistoryOrders(Date startDate, Date endDate) {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(generateHeader());
            ResponseEntity<com.ultrader.bot.model.alpaca.Order[]> orders = client.exchange("/orders?limit=500&status=closed", HttpMethod.GET, entity, com.ultrader.bot.model.alpaca.Order[].class);
            if (orders.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                return null;
            }
            List<Order> orderList = new ArrayList<>();
            for (com.ultrader.bot.model.alpaca.Order order : orders.getBody()) {
                LOGGER.debug("Found open order {}", order.toString());
                if(order.getStatus().equals("filled")) {
                    orderList.add(new Order(
                            order.getId(),
                            order.getSymbol(),
                            order.getSide(),
                            order.getType(),
                            order.getQty(),
                            order.getFilled_avg_price(),
                            order.getStatus(),
                            order.getFilled_at()));
                }
            }
            return orderList;
        } catch (Exception e) {
            LOGGER.error("Failed to get open orders.", e);
            return null;
        }
    }

}
