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
import java.time.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Web Socket handler of Polygon
 * @author ytx1991
 */
public class PolygonMessageHandler implements MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolygonMessageHandler.class);
    private ObjectMapper objectMapper;
    private long interval;
    private final PolygonMarketDataService service;
    private Map<String, Object> locks;

    public PolygonMessageHandler(PolygonMarketDataService service, long interval) {
        Validate.notNull(service, "service is required");
        this.objectMapper = new ObjectMapper();
        this.interval = interval;
        this.service = service;
        this.locks = new HashMap<>();
    }

    @Override
    public void onMessage(Message message) throws InterruptedException {
        String response = new String(message.getData(), StandardCharsets.UTF_8);
        try {
            Aggregation aggregation = objectMapper.readValue(response, Aggregation.class);
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
