package com.ultrader.bot.controller;

import com.ultrader.bot.dao.ChartDao;
import com.ultrader.bot.model.Chart;
import com.ultrader.bot.model.ChartResponse;
import com.ultrader.bot.model.Position;
import com.ultrader.bot.monitor.TradingAccountMonitor;
import com.ultrader.bot.util.TradingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ChartDao chartDao;

    @RequestMapping(method = RequestMethod.GET, value = "/getPortfolio")
    @ResponseBody
    public ChartResponse  getPortfolio(@RequestParam long length, @RequestParam long period) {
        try {
            return aggregateChart(length, period, chartDao);
        } catch (Exception e) {
            LOGGER.error("Get portfolio failed.", e);
            return null;
        }
    }

    public static ChartResponse aggregateChart(long length, long period, ChartDao chartDao) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusSeconds(length * period);
        long offset = now.toEpochSecond(ZoneOffset.UTC) - new Date().getTime() / 1000;
        List<Chart> charts = chartDao.getPortfolioByDate(startDate, now, "Portfolio");
        ChartResponse response = new ChartResponse();
        response.setName("Portfolio");
        List<String> labels = new ArrayList<>();
        response.setLabels(labels);
        List<Double> values = new ArrayList<>();
        response.setSeries(Collections.singletonList(values));
        if(charts.size() == 0) {

            return response;
        }
        Double lastValue = 0.0;
        DateTimeFormatter formatter;
        if (period >= 24 * 3600) {
            formatter = DateTimeFormatter.ofPattern("dd");
        } else {
            formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        }

        while (startDate.toEpochSecond(ZoneOffset.UTC) - offset < charts.get(0).getDate().getTime() / 1000) {
            startDate = startDate.plusSeconds(period);
        }
        for (Chart chart : charts) {
            LOGGER.info("start {} chart {}", startDate.toString(), chart.getDate().toString());
            if(chart.getDate().getTime() / 1000 < startDate.toEpochSecond(ZoneOffset.UTC) - offset) {
                lastValue = chart.getValue();
            } else {
                if(lastValue > 0) {
                    values.add(lastValue);
                    labels.add(startDate.format(formatter));
                }

                startDate = startDate.plusSeconds(period);
                lastValue = chart.getValue();
            }
        }
        return response;
    }

}
