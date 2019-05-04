package com.ultrader.bot.controller;

import com.google.common.collect.Lists;
import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.dao.StrategyDao;
import com.ultrader.bot.model.BackTestingResult;
import com.ultrader.bot.model.Strategy;
import com.ultrader.bot.model.StrategyBundle;
import com.ultrader.bot.monitor.MarketDataMonitor;
import com.ultrader.bot.monitor.TradingStrategyMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ta4j.core.AnalysisCriterion;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.TimeSeriesManager;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.analysis.CashFlow;
import org.ta4j.core.analysis.criteria.AverageProfitableTradesCriterion;
import org.ta4j.core.analysis.criteria.RewardRiskRatioCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.analysis.criteria.VersusBuyAndHoldCriterion;

import java.util.*;

/**
 * Strategy Controller
 * @author ytx1991
 */

@RequestMapping("/api/strategy")
@RestController
public class StrategyController {
    private static Logger LOGGER = LoggerFactory.getLogger(StrategyController.class);

    @Autowired
    private StrategyDao strategyDao;

    @Autowired
    private RuleDao ruleDao;

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
            StrategyBundle strategyBundle = new StrategyBundle(Lists.newArrayList(strategyDao.findAll()),Lists.newArrayList(ruleDao.findAll()));
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
            synchronized (TradingStrategyMonitor.getLock()) {
                TradingStrategyMonitor.getStrategies().clear();
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Reload strategy failed.", e);
            return false;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/backtest/{num}")
    @ResponseBody
    public Iterable<BackTestingResult> backTest(@PathVariable int num) {
        List<BackTestingResult> results = new ArrayList<>();
        synchronized(TradingStrategyMonitor.getLock()) {
            List<String> stocks = pickNStocks(num);
            for(String stock : stocks) {
                results.add(backTest(stock));
            }
        }
        return results;
    }

    private BackTestingResult backTest(String stock) {
        TimeSeries series = MarketDataMonitor.timeSeriesMap.get(stock);
        TimeSeriesManager manager = new TimeSeriesManager(series);
        TradingRecord tradingRecord = manager.run(TradingStrategyMonitor.getStrategies().get(stock));
        // Getting the cash flow of the resulting trades
        CashFlow cashFlow = new CashFlow(series, tradingRecord);
        // Getting the profitable trades ratio
        AnalysisCriterion profitTradesRatio = new AverageProfitableTradesCriterion();

        // Getting the reward-risk ratio
        AnalysisCriterion rewardRiskRatio = new RewardRiskRatioCriterion();

        // Total profit of our strategy
        TotalProfitCriterion totalProfitCriterion = new TotalProfitCriterion();
        // vs total profit of a buy-and-hold strategy
        AnalysisCriterion vsBuyAndHold = new VersusBuyAndHoldCriterion(totalProfitCriterion);

        return new BackTestingResult(
                stock,
                tradingRecord.getTradeCount(),
                profitTradesRatio.calculate(series, tradingRecord).doubleValue(),
                rewardRiskRatio.calculate(series, tradingRecord).doubleValue(),
                vsBuyAndHold.calculate(series, tradingRecord).doubleValue(),
                totalProfitCriterion.calculate(series, tradingRecord).doubleValue(),
                series.getBar(0).getBeginTime().toString(),
                series.getLastBar().getEndTime().toString());
    }

    private List<String> pickNStocks(int n) {
        Random random = new Random(System.currentTimeMillis());
        n  = n > TradingStrategyMonitor.getStrategies().size() ? TradingStrategyMonitor.getStrategies().size() : n;
        List<String> stocks = new ArrayList<>();
        List<String> allStock = new ArrayList<>(TradingStrategyMonitor.getStrategies().keySet());
        for(int i = 0; i < n; i++) {
            int index = random.nextInt(TradingStrategyMonitor.getStrategies().size());
            stocks.add(allStock.get(index));
        }
        return stocks;
    }
}
