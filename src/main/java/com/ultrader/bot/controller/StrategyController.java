package com.ultrader.bot.controller;

import com.google.common.collect.Lists;
import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.dao.StrategyDao;
import com.ultrader.bot.model.*;
import com.ultrader.bot.model.Strategy;
import com.ultrader.bot.monitor.MarketDataMonitor;
import com.ultrader.bot.monitor.TradingStrategyMonitor;
import com.ultrader.bot.service.TradingPlatform;
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
import org.ta4j.core.Trade;
import org.ta4j.core.analysis.CashFlow;
import org.ta4j.core.analysis.criteria.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Strategy Controller
 *
 * @author ytx1991
 */

@RequestMapping("/api/strategy")
@RestController("StrategyController")
public class StrategyController {
    private static Logger LOGGER = LoggerFactory.getLogger(StrategyController.class);

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
        int trades = 0, rewardRiskRatioCount = 0;
        double profitTradesRatio = 0.0, rewardRiskRatio = 0.0, vsBuyAndHold = 0.0, totalProfit = 0.0, averageHoldingDays = 0.0;
        for (String stock : stocks.split(",")) {
            TimeSeries timeSeries = new BaseTimeSeries(stock);
            timeSeriesList.add(timeSeries);
        }
        try {
            //Load market data
            tradingPlatform.getMarketDataService().getTimeSeries(timeSeriesList, interval, startDate, endDate);
            notifier.convertAndSend("/topic/progress/backtest", new ProgressMessage("InProgress", "Loading history market data",50));
        } catch (Exception e) {
            LOGGER.error("Load back test data failed.", e);
            notifier.convertAndSend("/topic/progress/backtest", new ProgressMessage("Error", "Loading history market data failed",50));
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
            notifier.convertAndSend("/topic/progress/backtest", new ProgressMessage("InProgress", "Back test " + timeSeries.getName(),50 + 50 * count / timeSeriesList.size()));
            BackTestingResult result = backTest(timeSeries, buyStrategyId, sellStrategyId);
            if (result == null) {
                continue;
            }
            trades += result.getTradingCount();
            totalProfit += result.getTotalProfit();
            vsBuyAndHold += result.getBuyAndHold();
            if (result.getTradingCount() > 0) {
                profitTradesRatio += result.getProfitTradesRatio() * result.getTradingCount();
                rewardRiskRatio += result.getRewardRiskRatio();
                rewardRiskRatioCount += 1;
                averageHoldingDays += result.getAverageHoldingDays();
            }
            results.add(result);
            count++;
        }
        notifier.convertAndSend("/topic/progress/backtest", new ProgressMessage("Completed", "",100));
        return new ResponseEntity<Iterable<BackTestingResult>>(results, HttpStatus.OK);
    }

    private BackTestingResult backTest(TimeSeries series, int buyStrategyId, int sellStrategyId) {
        try {
            TimeSeriesManager manager = new TimeSeriesManager(series);
            //Get strategy
            org.ta4j.core.Strategy strategy = new BaseStrategy(series.getName(),
                    TradingUtil.generateTradingStrategy(strategyDao, ruleDao, buyStrategyId, series),
                    TradingUtil.generateTradingStrategy(strategyDao, ruleDao, sellStrategyId, series));

            TradingRecord tradingRecord = manager.run(strategy);
            // Getting the cash flow of the resulting trades
            CashFlow cashFlow = new CashFlow(series, tradingRecord);
            // Getting the profitable trades ratio
            AnalysisCriterion profitTradesRatioCriterion = new AverageProfitableTradesCriterion();
            double profitTradesRatio = profitTradesRatioCriterion.calculate(series, tradingRecord).doubleValue();

            // Getting the reward-risk ratio
            AnalysisCriterion rewardRiskRatioCriterion = new RewardRiskRatioCriterion();
            double rewardRiskRatio = rewardRiskRatioCriterion.calculate(series, tradingRecord).doubleValue();
            // Total profit of our strategy
            TotalProfitCriterion totalProfitCriterion = new TotalProfitCriterion();
            double totalProfit = totalProfitCriterion.calculate(series, tradingRecord).doubleValue();
            // buy and hold profit
            AnalysisCriterion vsBuyAndHoldCriterion = new BuyAndHoldCriterion();
            double buyAndHold = vsBuyAndHoldCriterion.calculate(series, tradingRecord).doubleValue();
            double holdingDays = 0.0;
            for (Trade trade : tradingRecord.getTrades()) {
                holdingDays += (series.getBar(trade.getExit().getIndex()).getEndTime().toEpochSecond()
                        - series.getBar(trade.getEntry().getIndex()).getEndTime().toEpochSecond()) / 24 / 3600;
            }
            return new BackTestingResult(
                    series.getName(),
                    tradingRecord.getTradeCount(),
                    profitTradesRatio,
                    rewardRiskRatio,
                    buyAndHold - 1,
                    totalProfit - 1,
                    holdingDays / tradingRecord.getTrades().size(),
                    series.getBar(0).getBeginTime().toLocalDateTime(),
                    series.getLastBar().getEndTime().toLocalDateTime());
        } catch (Exception e) {
            LOGGER.error("Back test {} failed.",
                    series.getName(),
                    e);
            return null;
        }

    }

}
