package com.ultrader.bot.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.ta4j.core.Bar;
import org.ta4j.core.TimeSeries;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Market data service interface
 * @author ytx1991
 */
public interface MarketDataService {
    /**
     * Update a list of time series
     * @param stocks
     * @param interval
     * @return
     * @throws InterruptedException
     */
    List<TimeSeries> updateTimeSeries(List<TimeSeries> stocks, Long interval) throws InterruptedException;
    void getTimeSeries(List<TimeSeries> stocks, Long interval, LocalDateTime startDate, LocalDateTime endDate, SimpMessagingTemplate notifier, String topic) throws InterruptedException;
    void subscribe(String symbol);
    void unsubscribe(String symbol);
    void restart();
    void destroy();
}
