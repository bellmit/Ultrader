package com.ultrader.bot.controller;

import com.google.common.collect.Lists;
import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.dao.StrategyDao;
import com.ultrader.bot.model.*;
import com.ultrader.bot.model.Strategy;
import com.ultrader.bot.monitor.TradingAccountMonitor;
import com.ultrader.bot.service.TradingPlatform;
import com.ultrader.bot.util.GradientDescentOptimizer;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.ta4j.core.*;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static com.ultrader.bot.util.TradingUtil.backTest;

/**
 * Strategy Controller
 *
 * @author ytx1991
 */

@RequestMapping("/api/strategy")
@RestController("StrategyController")
public class StrategyController {
    private static Logger LOGGER = LoggerFactory.getLogger(StrategyController.class);
    private static final String BACKTEST_TOPIC = "/topic/progress/backtest";
    private static final String OPTIMIZATION_TOPIC = "/topic/progress/optimize";
    @Autowired
    private StrategyDao strategyDao;

    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private SettingDao settingDao;
    @Autowired
    private TradingPlatform tradingPlatform;
    @Autowired
    private SimpMessagingTemplate notifier;

    @RequestMapping(method = RequestMethod.POST, value = "/addStrategy")
    @ResponseBody
    public Strategy saveStrategy(@RequestBody Strategy strategy) {
        try {
            Strategy savedStrategy = strategyDao.save(strategy);
            return savedStrategy;
        } catch (Exception e) {
            LOGGER.error("Save strategy failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getStrategy/{id}")
    @ResponseBody
    public Strategy getStrategy(@PathVariable Long id) {
        try {
            Optional<Strategy> strategy = strategyDao.findById(id);
            return strategy.get();
        } catch (Exception e) {
            LOGGER.error("Get strategy failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getStrategyParameters")
    @ResponseBody
    public List<StrategyParameter> getStrategyParameters(@RequestParam long buyStrategyId, @RequestParam long sellStrategyId) {
        try {
            return TradingUtil.extractParameters(strategyDao, ruleDao, buyStrategyId, sellStrategyId);
        } catch (Exception e) {
            LOGGER.error("Get strategy failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getStrategies")
    @ResponseBody
    public Iterable<Strategy> getStrategies() {
        try {
            Iterable<Strategy> strategies = strategyDao.findAll();
            return strategies;
        } catch (Exception e) {
            LOGGER.error("Get strategies failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/export")
    @ResponseBody
    public StrategyBundle exportStrategies() {
        try {
            StrategyBundle strategyBundle = new StrategyBundle(Lists.newArrayList(strategyDao.findAll()), Lists.newArrayList(ruleDao.findAll()));
            return strategyBundle;
        } catch (Exception e) {
            LOGGER.error("Export strategies failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import")
    @ResponseBody
    public boolean importStrategies(@RequestBody StrategyBundle bundle) {
        try {
            ruleDao.saveAll(bundle.getRules());
            strategyDao.saveAll(bundle.getStrategies());
            //Apply the strategy;
            for (Strategy strategy : strategyDao.findAll()) {
                if (strategy.getType().equals("Buy")) {
                    Setting buySetting = new Setting();
                    buySetting.setName(SettingConstant.TRADE_BUY_STRATEGY.name());
                    buySetting.setValue(String.valueOf(strategy.getId()));
                    settingDao.save(buySetting);
                } else {
                    Setting sellSetting = new Setting();
                    sellSetting.setName(SettingConstant.TRADE_SELL_STRATEGY.name());
                    sellSetting.setValue(String.valueOf(strategy.getId()));
                    settingDao.save(sellSetting);
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Export strategies failed.", e);
            return false;
        }
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteStrategy/{id}")
    @ResponseBody
    public void deleteStrategies(@PathVariable Long id) {
        try {
            strategyDao.deleteById(id);
        } catch (Exception e) {
            LOGGER.error("Delete strategy failed.", e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/reload")
    @ResponseBody
    public boolean reload() {
        try {
            return true;
        } catch (Exception e) {
            LOGGER.error("Reload strategy failed.", e);
            return false;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/backtestByDate")
    @ResponseBody
    public ResponseEntity<Iterable<BackTestingResult>> backTestByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam long interval,
            @RequestParam String stocks,
            @RequestParam int buyStrategyId,
            @RequestParam int sellStrategyId) {
        interval = interval * 1000;
        List<BackTestingResult> results = new ArrayList<>();
        List<TimeSeries> timeSeriesList = new ArrayList<>();
        for (String stock : stocks.split(",")) {
            TimeSeries timeSeries = new BaseTimeSeries(stock);
            timeSeriesList.add(timeSeries);
        }
        try {
            //Load market data
            tradingPlatform.getMarketDataService().getTimeSeries(timeSeriesList, interval, startDate, endDate, notifier, BACKTEST_TOPIC);
            notifier.convertAndSend(BACKTEST_TOPIC, new ProgressMessage("InProgress", "Loading history market data",50));
        } catch (Exception e) {
            LOGGER.error("Load back test data failed.", e);
            notifier.convertAndSend(BACKTEST_TOPIC, new ProgressMessage("Error", "Loading history market data failed",50));
            return new ResponseEntity<Iterable<BackTestingResult>>(HttpStatus.FAILED_DEPENDENCY);
        }
        //Check time series
        boolean hasResponse = false;
        for (TimeSeries timeSeries : timeSeriesList) {
            if (timeSeries.getBarCount() > 0) {
                hasResponse = true;
            }
        }
        if (!hasResponse) {
            LOGGER.error("Cannot load history data for {}", stocks);
            return new ResponseEntity<Iterable<BackTestingResult>>(HttpStatus.FAILED_DEPENDENCY);
        }
        int count = 0;
        for (TimeSeries timeSeries : timeSeriesList) {
            notifier.convertAndSend(BACKTEST_TOPIC, new ProgressMessage("InProgress", "Back test " + timeSeries.getName(),50 + 50 * count / timeSeriesList.size()));
            BackTestingResult result = backTest(timeSeries, buyStrategyId, sellStrategyId, strategyDao, ruleDao, null);
            if (result == null) {
                continue;
            }
            results.add(result);
            count++;
        }
        notifier.convertAndSend(BACKTEST_TOPIC, new ProgressMessage("Completed", "Back Test Completed",100));
        return new ResponseEntity<Iterable<BackTestingResult>>(results, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/optimizeStrategyByDate")
    @ResponseBody
    public ResponseEntity<OptimizationResult> optimizeStrategyByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam long interval,
            @RequestParam String stocks,
            @RequestParam int buyStrategyId,
            @RequestParam int sellStrategyId,
            @RequestParam double probeValue,
            @RequestParam double learningRate,
            @RequestParam double convergeThreshold,
            @RequestParam int maxIteration,
            @RequestParam String optimizeGoal) {
        interval = interval * 1000;
        List<BackTestingResult> results = new ArrayList<>();
        List<TimeSeries> timeSeriesList = new ArrayList<>();
        int trades = 0, rewardRiskRatioCount = 0;
        double profitTradesRatio = 0.0, rewardRiskRatio = 0.0, vsBuyAndHold = 0.0, totalProfit = 0.0, averageHoldingDays = 0.0;
        for (String stock : stocks.split(",")) {
            TimeSeries timeSeries = new BaseTimeSeries(stock);
            timeSeriesList.add(timeSeries);
        }
        try {
            //Load market data
            tradingPlatform.getMarketDataService().getTimeSeries(timeSeriesList, interval, startDate, endDate, notifier, OPTIMIZATION_TOPIC);
            notifier.convertAndSend(OPTIMIZATION_TOPIC, new ProgressMessage("InProgress", "Loading history market data",50));
        } catch (Exception e) {
            LOGGER.error("Load back test data failed.", e);
            notifier.convertAndSend(OPTIMIZATION_TOPIC, new ProgressMessage("Error", "Loading history market data failed",50));
            return new ResponseEntity<OptimizationResult>(HttpStatus.FAILED_DEPENDENCY);
        }
        //Check time series
        boolean hasResponse = false;
        for (TimeSeries timeSeries : timeSeriesList) {
            if (timeSeries.getBarCount() > 0) {
                hasResponse = true;
            }
        }
        if (!hasResponse) {
            LOGGER.error("Cannot load history data for {}", stocks);
            return new ResponseEntity<OptimizationResult>(HttpStatus.FAILED_DEPENDENCY);
        }
        List<StrategyParameter> parameters = TradingUtil.extractParameters(
                strategyDao,
                ruleDao,
                buyStrategyId,
                sellStrategyId);
        String percentPerTrade = RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_BUY_MAX_LIMIT.getName(), "5%");
        Double percent = percentPerTrade.indexOf("%") > 0 ?
                (Double.parseDouble(percentPerTrade.substring(0, percentPerTrade.length() - 1)) / 100) :
                (Double.parseDouble(percentPerTrade) / TradingAccountMonitor.getAccount().getPortfolioValue());
        Double maxHolds = Double.parseDouble(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_BUY_HOLDING_LIMIT.getName(), "-1"));
        if (maxHolds < 0) {
            maxHolds = 1 / percent;
        }
        GradientDescentOptimizer optimizer = new GradientDescentOptimizer(
                strategyDao,
                ruleDao,
                timeSeriesList,
                parameters.stream().map(p -> p.getValue()).collect(Collectors.toList()),
                parameters.stream().map(p -> p.getKey()).collect(Collectors.toList()),
                buyStrategyId,
                sellStrategyId,
                probeValue,
                learningRate,
                convergeThreshold,
                maxIteration,
                optimizeGoal,
                percent,
                (endDate.toEpochSecond(ZoneOffset.UTC) - startDate.toEpochSecond(ZoneOffset.UTC)) / 3600 / 24,
                (int)Math.round(maxHolds),
                notifier,
                OPTIMIZATION_TOPIC);
        notifier.convertAndSend(OPTIMIZATION_TOPIC, new ProgressMessage("Completed", "Optimization Completed",100));
        return new ResponseEntity<OptimizationResult>(optimizer.optimize(), HttpStatus.OK);
    }

}
