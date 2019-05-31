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
    private long interval;
    private final PolygonMarketDataService service;

    public PolygonMessageHandler(PolygonMarketDataService service, long interval) {
        Validate.notNull(service, "service is required");
        this.objectMapper = new ObjectMapper();
        this.interval = interval;
        this.service = service;
    }

    @Override
    public void onMessage(Message message) throws InterruptedException {
        String response = new String(message.getData(), StandardCharsets.UTF_8);
        try {
            Aggregation aggregation = objectMapper.readValue(response, Aggregation.class);
            if(MarketDataMonitor.timeSeriesMap.containsKey(aggregation.getSym())) {
                //Check if need to add a new bar;
                Instant i = null;
                TimeSeries timeSeries = MarketDataMonitor.timeSeriesMap.get(aggregation.getSym());
                if(timeSeries.getLastBar().getEndTime().toEpochSecond() < aggregation.getE() / 1000) {
                    long startEpoch = timeSeries.getLastBar().getEndTime().toEpochSecond();
                    while (startEpoch + interval < aggregation.getE() / 1000) {
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
                } else {
                    Bar bar = timeSeries.getLastBar();
                    bar.addPrice(PrecisionNum.valueOf(aggregation.getO()));
                    bar.addPrice(PrecisionNum.valueOf(aggregation.getH()));
                    bar.addPrice(PrecisionNum.valueOf(aggregation.getL()));
                    bar.addPrice(PrecisionNum.valueOf(aggregation.getC()));
                    bar.addTrade(PrecisionNum.valueOf(aggregation.getV()), PrecisionNum.valueOf(aggregation.getA()));
                }

            } else {
                LOGGER.error("Cannot find time series for {} when updating", aggregation.getSym());
            }

        } catch (Exception e) {
            LOGGER.error("Handle Polygon message failed.", e);
        }

    }
}
