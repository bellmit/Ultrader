package com.ultrader.bot.service.polygon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultrader.bot.model.polygon.Aggregation;
import com.ultrader.bot.monitor.MarketDataMonitor;
import com.ultrader.bot.util.TradingUtil;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.num.PrecisionNum;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Web Socket handler of Polygon
 * @author ytx1991
 */
public class PolygonMessageHandler implements MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolygonMessageHandler.class);
    private ObjectMapper objectMapper;
    private final PolygonMarketDataService service;

    public PolygonMessageHandler(PolygonMarketDataService service) {
        Validate.notNull(service, "service is required");
        this.objectMapper = new ObjectMapper();
        this.service = service;
    }

    @Override
    public void onMessage(Message message) throws InterruptedException {
        String response = new String(message.getData(), StandardCharsets.UTF_8);
        try {
            Aggregation aggregation = objectMapper.readValue(response, Aggregation.class);
            if(MarketDataMonitor.timeSeriesMap.containsKey(aggregation.getSym())) {
                //Check if need to add a new bar;
                Instant i = Instant.ofEpochSecond(aggregation.getE() / 1000);
                TimeSeries timeSeries = MarketDataMonitor.timeSeriesMap.get(aggregation.getSym());
                if(timeSeries.getLastBar().getEndTime().isBefore(ZonedDateTime.ofInstant(i, ZoneId.of(TradingUtil.TIME_ZONE)))) {
                    long interval = timeSeries.getLastBar().getEndTime().toEpochSecond() - timeSeries.getLastBar().getBeginTime().toEpochSecond();
                    i = Instant.ofEpochSecond(timeSeries.getLastBar().getEndTime().toEpochSecond() + interval);
                    Bar bar = new BaseBar(Duration.ofMillis(interval),
                            ZonedDateTime.ofInstant(i, ZoneId.of(TradingUtil.TIME_ZONE)),
                            PrecisionNum.valueOf(aggregation.getO()),
                            PrecisionNum.valueOf(aggregation.getH()),
                            PrecisionNum.valueOf(aggregation.getH()),
                            PrecisionNum.valueOf(aggregation.getC()),
                            PrecisionNum.valueOf(aggregation.getV()),
                            PrecisionNum.valueOf(aggregation.getV() * aggregation.getA()));
                    timeSeries.addBar(bar);
                } else {
                    Bar bar = timeSeries.getLastBar();
                    bar.addPrice(PrecisionNum.valueOf(aggregation.getO()));
                    bar.addPrice(PrecisionNum.valueOf(aggregation.getH()));
                    bar.addPrice(PrecisionNum.valueOf(aggregation.getL()));
                    bar.addPrice(PrecisionNum.valueOf(aggregation.getC()));
                    bar.addTrade(PrecisionNum.valueOf(aggregation.getV()), PrecisionNum.valueOf(aggregation.getA()));
                }

            } else {
                //Unsubscribe stock
                service.getDispatcher().unsubscribe(service.getFrequency() + aggregation.getSym());
            }

        } catch (Exception e) {
            LOGGER.error("Handle Polygon message failed.", e);
        }

    }
}
