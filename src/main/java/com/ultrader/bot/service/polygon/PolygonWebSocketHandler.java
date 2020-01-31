package com.ultrader.bot.service.polygon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultrader.bot.model.polygon.Aggregation;
import com.ultrader.bot.monitor.MarketDataMonitor;
import com.ultrader.bot.util.MarketDataUtil;
import com.ultrader.bot.util.TradingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.num.PrecisionNum;

import java.time.*;
import java.util.HashMap;
import java.util.Map;


public class PolygonWebSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolygonWebSocketHandler.class);
    private final String key;
    private final static LocalTime MARKET_OPEN_TIME = LocalTime.parse("09:30:01");
    private final static LocalTime MARKET_CLOSE_TIME = LocalTime.parse("16:00:00");
    private ObjectMapper objectMapper;
    private long interval;
    private final PolygonMarketDataService service;
    private Map<String, Object> locks;
    private WebSocketSession session;

    public PolygonWebSocketHandler(final String key, PolygonMarketDataService service, long interval) {
        this.key = key;
        this.objectMapper = new ObjectMapper();
        this.service = service;
        this.interval = interval;
        this.locks = new HashMap<>();
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String str = message.getPayload();
            LOGGER.debug("Message Received [" + str + "]");

            if(str.indexOf("status") < 0) {
                Aggregation[] responses = objectMapper.readValue(str, Aggregation[].class);
                handlePriceUpdate(responses);
            }
        } catch (Exception e) {
            LOGGER.error("Handle Polygon web socket message failed.", e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String payload = String.format("{\"action\": \"auth\",\"params\": \"%s\"}", key);
        LOGGER.debug("Sending [" + payload + "]");
        session.sendMessage(new TextMessage(payload));
        LOGGER.info("Polygon websocket Connected.");
        this.session = session;
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        LOGGER.error("Transport Error", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        LOGGER.info("Connection Closed [" + status.getReason() +" " + status.getCode() +"]");
        if (status.getCode() != 1000) {
            service.restart();
        }
    }

    public void subscribe(String symbol) {
        try {
            String payload = String.format("{\"action\": \"subscribe\",\"params\": \"%s\"}", symbol);
            session.sendMessage(new TextMessage(payload));
        } catch (Exception e) {
            LOGGER.error("Subscribe " + symbol + " failed", e);
        }
    }

    public void unsubscribe(String symbol) {
        try {
            String payload = String.format("{\"action\": \"unsubscribe\",\"params\": \"%s\"}", symbol);
            session.sendMessage(new TextMessage(payload));
        } catch (Exception e) {
            LOGGER.error("Unsubscribe " + symbol + " failed", e);
        }
    }
    private void handlePriceUpdate(Aggregation[] aggregations) {
        for(Aggregation aggregation : aggregations) {
            try {
                if (!locks.containsKey(aggregation.getSym())) {
                    locks.put(aggregation.getSym(), new Object());
                }
                synchronized (locks.get(aggregation.getSym())) {
                    if(MarketDataMonitor.timeSeriesMap.containsKey(aggregation.getSym())) {
                        //Check if need to add a new bar;
                        Instant i = null;
                        TimeSeries timeSeries = MarketDataMonitor.timeSeriesMap.get(aggregation.getSym());
                        LOGGER.debug("Update {}, last bar end {}, message end {}",
                                aggregation.getSym(), timeSeries.getLastBar().getEndTime().toEpochSecond(), aggregation.getE() / 1000);
                        long now = LocalDateTime.now(ZoneOffset.UTC).atZone(ZoneOffset.UTC).toEpochSecond() / 1000;
                        if(timeSeries.getLastBar().getEndTime().toEpochSecond() < aggregation.getE() / 1000) {
                            //If it's newer than the last bar
                            //Calculate the start time of the new bar
                            long startEpoch = timeSeries.getLastBar().getEndTime().toEpochSecond();
                            while (startEpoch + interval < aggregation.getE() / 1000) {
                                if (timeSeries.getLastBar().getEndTime().toLocalTime().isAfter(MARKET_OPEN_TIME)
                                        && timeSeries.getLastBar().getEndTime().toLocalTime().isBefore(MARKET_CLOSE_TIME)) {
                                    //Fill the gap of bars
                                    timeSeries.addBar(MarketDataUtil.fillBarGap(timeSeries.getLastBar(), startEpoch + interval - timeSeries.getLastBar().getEndTime().toEpochSecond()));
                                }
                                startEpoch += interval;
                            }
                            i = Instant.ofEpochSecond( startEpoch + interval);
                            Bar bar = new BaseBar(Duration.ofMillis(interval),
                                    ZonedDateTime.ofInstant(i, ZoneId.of(TradingUtil.TIME_ZONE)),
                                    PrecisionNum.valueOf(aggregation.getO()),
                                    PrecisionNum.valueOf(aggregation.getH()),
                                    PrecisionNum.valueOf(aggregation.getH()),
                                    PrecisionNum.valueOf(aggregation.getC()),
                                    PrecisionNum.valueOf(aggregation.getV()),
                                    PrecisionNum.valueOf(aggregation.getV() * aggregation.getA()));
                            timeSeries.addBar(bar);
                            LOGGER.debug("Add new bar {} in {} time series, interval {}", bar, aggregation.getSym(), interval);
                        } else if (now <= timeSeries.getLastBar().getEndTime().toEpochSecond()) {
                            //Update last bar
                            Bar bar = timeSeries.getLastBar();
                            bar.addPrice(PrecisionNum.valueOf(aggregation.getO()));
                            bar.addPrice(PrecisionNum.valueOf(aggregation.getH()));
                            bar.addPrice(PrecisionNum.valueOf(aggregation.getL()));
                            bar.addPrice(PrecisionNum.valueOf(aggregation.getC()));
                            bar.addTrade(PrecisionNum.valueOf(aggregation.getV()), PrecisionNum.valueOf(aggregation.getA()));
                            LOGGER.debug("Update bar {}", bar);
                        } else {
                            LOGGER.error("Unexpected data for {}, Now Epoch {}, Last Bar End Epoch {}", aggregation.getSym(), now, timeSeries.getLastBar().getEndTime().toEpochSecond());
                        }

                    } else {
                        LOGGER.error("Cannot find time series for {} when updating", aggregation.getSym());
                        service.unsubscribe(aggregation.getSym());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Handle Polygon message failed.", e);
            }

        }

    }
}
