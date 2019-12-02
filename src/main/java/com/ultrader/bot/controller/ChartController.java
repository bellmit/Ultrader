package com.ultrader.bot.controller;

import com.ultrader.bot.dao.ChartDao;
import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.model.Chart;
import com.ultrader.bot.model.ChartResponse;
import com.ultrader.bot.model.Position;
import com.ultrader.bot.monitor.TradingAccountMonitor;
import com.ultrader.bot.service.NotificationService;
import com.ultrader.bot.util.ChartType;
import com.ultrader.bot.util.TradingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Position Controller
 * @author ytx1991
 */

@RequestMapping("/api/chart")
@RestController("ChartController")
public class ChartController {
    private static Logger LOGGER = LoggerFactory.getLogger(ChartController.class);

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(method = RequestMethod.GET, value = "/getPortfolio")
    public ResponseEntity<ChartResponse> getPortfolio(@RequestParam int length, @RequestParam long period, @RequestParam int step) {
        try {
            return ResponseEntity.ok(notificationService.aggregatePortfolioChart(length, period, step));
        } catch (Exception e) {
            LOGGER.error("Get portfolio chart failed.", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getProfit")
    public ResponseEntity<ChartResponse> getProfit(@RequestParam int length, @RequestParam long period, @RequestParam int step) {
        try {
            return ResponseEntity.ok(notificationService.aggregateProfitChart(length, period, step));
        } catch (Exception e) {
            LOGGER.error("Get profit chart failed.", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getTrades")
    public ResponseEntity<ChartResponse> getTrades(@RequestParam int length, @RequestParam long period,  @RequestParam int step) {
        try {
            return ResponseEntity.ok(notificationService.aggregateTradeChart(length, period, step));
        } catch (Exception e) {
            LOGGER.error("Get trade chart failed.", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getPortfolioPie")
    public ResponseEntity<ChartResponse> getPortfolioPie(@RequestParam String type) {
        try {
            List<String> labels = new ArrayList<>();
            List<Double> percentages = new ArrayList<>();
            List<List<Double>> series = Collections.singletonList(percentages);
            ChartResponse response = new ChartResponse("PortfolioPie", labels, series);
            if (type.equals("GainLoss")) {
                Double loss = 0.0,gain = 0.0;
                for (Position position : TradingAccountMonitor.getPositions().values()) {
                    if (position.getQuantity() > 0) {
                        //Long case
                        if (position.getAverageCost() < position.getCurrentPrice()) {
                            gain += position.getCurrentPrice() * position.getQuantity();
                        } else {
                            loss += position.getCurrentPrice() * position.getQuantity();
                        }
                    } else {
                        //Short case
                        if (position.getAverageCost() < position.getCurrentPrice()) {
                            loss += -position.getCurrentPrice() * position.getQuantity();
                        } else {
                            gain += -position.getCurrentPrice() * position.getQuantity();
                        }
                    }
                }
                if (TradingAccountMonitor.getAccount().getCash() > 0.1) {
                    labels.add("Cash");
                    percentages.add(TradingAccountMonitor.getAccount().getCash());
                }
                if (gain > 0.1) {
                    labels.add("Gained Stocks");
                    percentages.add(gain);
                }
                if (loss > 0.1) {
                    labels.add("Lossed Stocks");
                    percentages.add(loss);
                }
            } else if (type.equals("Margin")) {
                if (TradingAccountMonitor.getAccount().getCash() >= 0) {
                    labels.add("Deposit");
                    percentages.add(100.0);
                } else {
                    Double total = 0.0;
                    for (Position position : TradingAccountMonitor.getPositions().values()) {
                        total += position.getCurrentPrice() * Math.abs(position.getQuantity());
                    }
                    labels.add("Deposit");
                    percentages.add(total + TradingAccountMonitor.getAccount().getCash());
                    labels.add("Loan");
                    percentages.add(-TradingAccountMonitor.getAccount().getCash());
                }
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Get portfolio pie chart failed.", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
