package com.ultrader.bot.service.alpaca;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.model.Order;
import com.ultrader.bot.model.alpaca.websocket.TradeUpdateResponse;
import com.ultrader.bot.model.alpaca.websocket.TradeUpdate;
import com.ultrader.bot.util.TradingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class AlpacaWebSocketHandler extends BinaryWebSocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AlpacaWebSocketHandler.class);
    private final String key;
    private final String secret;
    private final OrderDao orderDao;
    private ObjectMapper objectMapper;

    public AlpacaWebSocketHandler(final String key, final String secret, final OrderDao orderDao) {
        this.key = key;
        this.secret = secret;
        this.objectMapper = new ObjectMapper();
        this.orderDao = orderDao;
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        try {
            String str = StandardCharsets.UTF_8.decode(message.getPayload()).toString();
            LOG.debug("Message Received [" + str + "]");

            if(str.indexOf("trade_updates") > 0 && str.indexOf("listening") < 0 ) {
                TradeUpdateResponse response = objectMapper.readValue(str, TradeUpdateResponse.class);
                handleTradeUpdate(response.getData());
            }
        } catch (Exception e) {
            LOG.error("Handle Alpaca web socket message failed.", e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOG.info("Alpaca websocket Connected");
       String payload = String.format("{\"action\": \"authenticate\",\"data\": {\"key_id\":\"%s\" ,\"secret_key\":\"%s\"}}", key, secret);
        LOG.info("Sending [" + payload + "]");
        session.sendMessage(new TextMessage(payload));
        payload = "{\n" +
                "    \"action\": \"listen\",\n" +
                "    \"data\": {\n" +
                "        \"streams\": [\"account_updates\", \"trade_updates\"]\n" +
                "    }\n" +
                "}";
        session.sendMessage(new TextMessage(payload));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        LOG.error("Transport Error", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        LOG.info("Connection Closed [" + status.getReason() + "]");
    }

    private void handleTradeUpdate(TradeUpdate tradeUpdate) {
        if(tradeUpdate.getEvent().equals("fill")) {
            LOG.info("{} {} at {}, qyt:{}",
                    tradeUpdate.getOrder().getSide(),
                    tradeUpdate.getOrder().getSymbol(),
                    tradeUpdate.getOrder().getFilled_avg_price(),
                    tradeUpdate.getOrder().getQty());
            Order order = new Order(
                    tradeUpdate.getOrder().getId(),
                    tradeUpdate.getOrder().getSymbol(),
                    tradeUpdate.getOrder().getSide(),
                    tradeUpdate.getOrder().getOrder_type(),
                    tradeUpdate.getOrder().getQty(),
                    tradeUpdate.getOrder().getFilled_avg_price(),
                    tradeUpdate.getOrder().getStatus(),
                    tradeUpdate.getOrder().getFilled_at());
            orderDao.save(order);
            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalDate today = LocalDate.now(ZoneId.of(TradingUtil.TIME_ZONE));
            LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
            orderDao.findAllOrdersByDate(todayMidnight, LocalDateTime.now(ZoneId.of(TradingUtil.TIME_ZONE)));
        }
    }
}
