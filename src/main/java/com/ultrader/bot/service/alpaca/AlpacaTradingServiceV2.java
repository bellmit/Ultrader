package com.ultrader.bot.service.alpaca;

import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.Account;
import com.ultrader.bot.model.Order;
import com.ultrader.bot.model.Position;
import com.ultrader.bot.model.Setting;
import com.ultrader.bot.model.alpaca.AccountConfiguration;
import com.ultrader.bot.model.alpaca.Asset;
import com.ultrader.bot.model.alpaca.Clock;
import com.ultrader.bot.service.NotificationService;
import com.ultrader.bot.service.TradingService;
import com.ultrader.bot.util.NotificationType;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingPlatformConstant;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
public class AlpacaTradingServiceV2 implements TradingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaTradingServiceV2.class);
    private String alpacaKey;
    private String alpacaSecret;
    private RestTemplate client;
    private final RestTemplateBuilder restTemplateBuilder;
    private WebSocketConnectionManager connectionManager;
    private final OrderDao orderDao;
    private final NotificationService notifier;
    private final SettingDao settingDao;
    private final String plaformName;

    public AlpacaTradingServiceV2(final SettingDao settingDao,
                                  final RestTemplateBuilder restTemplateBuilder,
                                  final OrderDao orderDao,
                                  final NotificationService notifier,
                                  final String platformName) {
        Validate.notNull(restTemplateBuilder, "restTemplateBuilder is required");
        Validate.notNull(settingDao, "settingDao is required");
        Validate.notNull(orderDao, "orderDao is required");
        Validate.notNull(notifier, "notifier is required");
        Validate.notEmpty(platformName, "platformName is required");
        this.settingDao = settingDao;
        this.orderDao = orderDao;
        this.plaformName = platformName;
        this.notifier = notifier;
        this.restTemplateBuilder = restTemplateBuilder;
        initService();
    }

    private void initService() {
        destroy();
        if (plaformName.equals(TradingPlatformConstant.ALPACA)) {
            this.alpacaKey = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_KEY.getName(), "");
            this.alpacaSecret = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_SECRET.getName(), "");
            if(alpacaKey.equals("") || alpacaSecret.equals("")) {
                //It can be the first time setup
                LOGGER.warn("Cannot find Alpaca API key, please check your config");
                notifier.sendNotification("Setting Missing", "Cannot find Alpaca Live trading API key/secret, please check your setting", NotificationType.ERROR);
            } else {
                client = restTemplateBuilder.rootUri("https://api.alpaca.markets/v2").build();
                client.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
                //Init Websocket
                if (!alpacaKey.isEmpty()) {
                    connectionManager = new WebSocketConnectionManager(
                            new StandardWebSocketClient(),
                            new AlpacaWebSocketHandler(alpacaKey, alpacaSecret, orderDao, notifier), "wss://api.alpaca.markets/stream");
                    connectionManager.start();
                }
            }
        } else if (plaformName.equals(TradingPlatformConstant.ALPACA_PAPER)) {
            this.alpacaKey = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_PAPER_KEY.getName(), "");
            this.alpacaSecret = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_PAPER_SECRET.getName(), "");
            if(alpacaKey.equals("") || alpacaSecret.equals("")) {
                //It can be the first time setup
                LOGGER.warn("Cannot find Alpaca Paper API key, please check your config");
                notifier.sendNotification("Setting Missing", "Cannot find Alpaca Paper trading API key/secret, please check your setting", NotificationType.ERROR);
            } else {
                client = restTemplateBuilder.rootUri("https://paper-api.alpaca.markets/v2").build();
                client.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
                //Init Websocket
                if (!alpacaKey.isEmpty()) {
                    connectionManager = new WebSocketConnectionManager(
                            new StandardWebSocketClient(),
                            new AlpacaWebSocketHandler(alpacaKey, alpacaSecret, orderDao, notifier), "wss://paper-api.alpaca.markets/stream");
                    connectionManager.start();
                }
            }
        } else {
            LOGGER.error("Illegal trading platform {}", plaformName);
            notifier.sendNotification("Trading Platform Error", "Illegal trading plaform " + plaformName, NotificationType.ERROR);
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
        initService();
    }

    @Override
    public void destroy() {
        //If already init, clean up
        if (connectionManager != null) {
            try {
                connectionManager.stopInternal();
            } catch (Exception e) {
                LOGGER.info("Restart Alpaca Websocket.");
            }
        }
    }


    public boolean isMarketOpen() {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(generateHeader());
            ResponseEntity<Clock> clock = client.exchange("/clock", HttpMethod.GET, entity, Clock.class);
            if (clock.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca clock API, Error " + clock.getStatusCode().toString(), NotificationType.ERROR);
                return false;
            }
            LOGGER.debug(clock.getBody().toString());
            return clock.getBody().getIs_open();
        } catch (Exception e) {
            LOGGER.error("Failed to call /clock api.", e);
            notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca clock API, Error " + e.getMessage(), NotificationType.ERROR);
            return false;
        }
    }

    public Map<String, Set<String>> getAvailableStocks() {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(generateHeader());
            ResponseEntity<Asset[]> stocks = client.exchange("/assets?status=active", HttpMethod.GET, entity, Asset[].class);
            if (stocks.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca clock API, Error " + stocks.getStatusCode().toString(), NotificationType.ERROR);
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
            notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca asset API, Error " + e.getMessage(), NotificationType.ERROR);
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
                notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca positions API, Error " + positions.getStatusCode().toString(), NotificationType.ERROR);
                return null;
            }
            Map<String, Position> positionMap = new HashMap<>();
            for (com.ultrader.bot.model.alpaca.Position position : positions.getBody()) {
                positionMap.put(position.getSymbol(), new Position(position.getSymbol(), position.getQty(), position.getAvg_entry_price(), new Date(), position.getCurrent_price(), position.getExchange(), position.getMarket_value(), position.getChange_today()));
            }
            return positionMap;
        } catch (Exception e) {
            LOGGER.error("Failed to call /positions api.", e);
            notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca positions API, Error " + e.getMessage(), NotificationType.ERROR);
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
                notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca account API, Error " + account.getStatusCode().toString(), NotificationType.ERROR);
                return null;
            }
            return new Account(account.getBody().getId(),
                    account.getBody().getStatus(),
                    account.getBody().getCurrency(),
                    account.getBody().getBuying_power(),
                    account.getBody().getCash(),
                    account.getBody().getCash(),
                    account.getBody().getEquity(),
                    account.getBody().getPattern_day_trader(),
                    account.getBody().getTrading_blocked(),
                    account.getBody().getTransfers_blocked(),
                    account.getBody().getAccount_blocked());
        } catch (Exception e) {
            LOGGER.error("Failed to call /account api.", e);
            notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca account API, Error " + e.getMessage(), NotificationType.ERROR);
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
                notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca order API, Error " + orderResponseEntity.getStatusCode().toString(), NotificationType.ERROR);
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
                notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca order API, Error " + orders.getStatusCode().toString(), NotificationType.ERROR);
                return null;
            }
            Map<String, Order> orderMap = new HashMap<>();
            for (com.ultrader.bot.model.alpaca.Order order : orders.getBody()) {
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
            notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca order API, Error " + e.getMessage(), NotificationType.ERROR);
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
                notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca order API, Error " + orders.getStatusCode().toString(), NotificationType.ERROR);
                return null;
            }
            List<Order> orderList = new ArrayList<>();
            for (com.ultrader.bot.model.alpaca.Order order : orders.getBody()) {
                LOGGER.debug("Found open order {}", order.toString());
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
            return orderList;
        } catch (Exception e) {
            LOGGER.error("Failed to get open orders.", e);
            notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca order API, Error " + e.getMessage(), NotificationType.ERROR);
            return null;
        }
    }

    @Override
    public List<Setting> getAccountConfiguration() {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(generateHeader());
            ResponseEntity<AccountConfiguration> configurationResponseEntity = client.exchange("/account/configurations", HttpMethod.GET, entity, AccountConfiguration.class);
            if (configurationResponseEntity.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca account/configurations API, Error " + configurationResponseEntity.getStatusCode().toString(), NotificationType.ERROR);
                return null;
            }
            List<Setting> settings = new ArrayList<>();
            settings.add(new Setting(SettingConstant.ALPACA_DTMC.getName(), configurationResponseEntity.getBody().getDtbp_check()));
            settings.add(new Setting(SettingConstant.ALPACA_TRADE_CONFIRM_EMAIL.getName(), configurationResponseEntity.getBody().getTrade_confirm_email()));
            settings.add(new Setting(SettingConstant.ALPACA_NO_SHORTING.getName(), configurationResponseEntity.getBody().getNo_shorting().toString()));
            settings.add(new Setting(SettingConstant.ALPACA_SUSPEND_TRADE.getName(), configurationResponseEntity.getBody().getSuspend_trade().toString()));
            return settings;
        } catch (Exception e) {
            LOGGER.error("Failed to get account configuration.", e);
            notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca account/configurations API, Error " + e.getMessage(), NotificationType.ERROR);
            return null;
        }
    }

    @Override
    public void setAccountConfiguration(List<Setting> accountConfiguration) {
        try {
            AccountConfiguration configuration = new AccountConfiguration();
            for (Setting setting : accountConfiguration) {
                if (setting.getName().equals(SettingConstant.ALPACA_DTMC.getName())) {
                    configuration.setDtbp_check(setting.getValue());
                }
                if (setting.getName().equals(SettingConstant.ALPACA_SUSPEND_TRADE.getName())) {
                    configuration.setSuspend_trade(setting.getValue().equalsIgnoreCase("false") ? false : true);
                }
                if (setting.getName().equals(SettingConstant.ALPACA_NO_SHORTING.getName())) {
                    configuration.setNo_shorting(setting.getValue().equalsIgnoreCase("false") ? false : true);
                }
                if (setting.getName().equals(SettingConstant.ALPACA_TRADE_CONFIRM_EMAIL.getName())) {
                    configuration.setTrade_confirm_email(setting.getValue());
                }
            }
            HttpEntity<AccountConfiguration> entity = new HttpEntity<>(configuration, generateHeader());
            ResponseEntity<AccountConfiguration> configurationResponseEntity = client.exchange("/account/configurations", HttpMethod.PATCH, entity, AccountConfiguration.class);
            if (configurationResponseEntity.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca account/configurations API, Error " + configurationResponseEntity.getStatusCode().toString(), NotificationType.ERROR);
            }
            return;
        } catch (Exception e) {
            LOGGER.error("Failed to get account configuration.", e);
            notifier.sendNotification("Alpaca API Failure", "Cannot call Alpaca account/configurations API, Error " + e.getMessage(), NotificationType.ERROR);
            return;
        }
    }
}
