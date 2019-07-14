package com.ultrader.bot.controller;

import com.google.common.collect.Lists;
import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.dao.StrategyDao;
import com.ultrader.bot.model.BackTestingResult;
import com.ultrader.bot.model.Strategy;
import com.ultrader.bot.model.StrategyBundle;
import com.ultrader.bot.monitor.MarketDataMonitor;
import com.ultrader.bot.monitor.TradingStrategyMonitor;
import com.ultrader.bot.service.TradingPlatform;
import com.ultrader.bot.util.TradingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ta4j.core.*;
import org.ta4j.core.analysis.CashFlow;
import org.ta4j.core.analysis.criteria.AverageProfitableTradesCriterion;
import org.ta4j.core.analysis.criteria.RewardRiskRatioCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.analysis.criteria.VersusBuyAndHoldCriterion;

import java.util.*;

/**
 * Strategy Controller
 *
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

    @Autowired
    private TradingPlatform tradingPlatform;

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

    @RequestMapping(method = RequestMethod.GET, value = "/backtest")
    @ResponseBody
    public Iterable<BackTestingResult> backTest(@RequestParam int length, @RequestParam long interval, @RequestParam String stocks, @RequestParam int buyStrategyId, @RequestParam int sellStrategyId) {
        List<BackTestingResult> results = new ArrayList<>();
        List<TimeSeries> timeSeriesList = new ArrayList<>();
        int trades = 0, tradeCount = 0, profitTradesRatioCount = 0, rewardRiskRatioCount = 0, vsBuyAndHoldCount = 0, totalProfitCount = 0;
        double profitTradesRatio = 0.0, rewardRiskRatio = 0.0, vsBuyAndHold = 0.0, totalProfit = 0.0;
        for (String stock : stocks.split(",")) {
            TimeSeries timeSeries = new BaseTimeSeries(stock);
            timeSeries.setMaximumBarCount(length);
            timeSeriesList.add(timeSeries);
        }
        try {
            //Load market data
            timeSeriesList = tradingPlatform.getMarketDataService().updateTimeSeries(timeSeriesList, interval);
        } catch (Exception e) {
            LOGGER.error("Load back test data failed.", e);
        }

        for (TimeSeries timeSeries : timeSeriesList) {

            BackTestingResult result = backTest(timeSeries, buyStrategyId, sellStrategyId);
            if (result == null) {
                continue;
            }
            trades += result.getTradingCount();
            if (result.getTradingCount() > 0) {
                tradeCount += 1;
            }
            if (!Double.isNaN(result.getProfitTradesRatio())) {
                profitTradesRatio += result.getProfitTradesRatio();
                profitTradesRatioCount += 1;
            }
            if (!Double.isNaN(result.getRewardRiskRatio())) {
                rewardRiskRatio += result.getRewardRiskRatio();
                rewardRiskRatioCount += 1;
            }
            if (!Double.isNaN(result.getTotalProfit())) {
                totalProfit += result.getTotalProfit();
                totalProfitCount += 1;
            }
            if (!Double.isNaN(result.getVsBuyAndHold())) {
                vsBuyAndHold += result.getVsBuyAndHold();
                vsBuyAndHoldCount += 1;
            }
            results.add(result);
        }
        //Add summary
        results.add(new BackTestingResult(
                "Aggregate Summary",
                trades,
                profitTradesRatio,
                rewardRiskRatio,
                vsBuyAndHold,
                totalProfit,
                null,
                null));
        results.add(new BackTestingResult(
                "Average Summary",
                tradeCount == 0 ? 0: trades / tradeCount,
                profitTradesRatioCount == 0 ? 0 : profitTradesRatio / profitTradesRatioCount,
                rewardRiskRatioCount == 0 ? 0 : rewardRiskRatio / rewardRiskRatioCount,
                vsBuyAndHoldCount == 0 ? 0 : vsBuyAndHold / vsBuyAndHoldCount,
                totalProfitCount == 0 ? 0 : totalProfit * 1.0 / totalProfitCount,
                null,
                null));

        return results;
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
            // vs total profit of a buy-and-hold strategy
            AnalysisCriterion vsBuyAndHoldCriterion = new VersusBuyAndHoldCriterion(totalProfitCriterion);
            double vsBuyAndHold = vsBuyAndHoldCriterion.calculate(series, tradingRecord).doubleValue();

            return new BackTestingResult(
                    series.getName(),
                    tradingRecord.getTradeCount(),
                    profitTradesRatio,
                    rewardRiskRatio,
                    vsBuyAndHold,
                    totalProfit,
                    series.getBar(0).getBeginTime().toString(),
                    series.getLastBar().getEndTime().toString());
        } catch (Exception e) {
            LOGGER.error("Back test {} failed. Start {} End {}",
                    series.getName(),
                    series.getBar(0).getBeginTime().toString(),
                    series.getLastBar().getEndTime().toString(),
                    e);
            return null;
        }

    }

    private List<String> pickNStocks(int n) {
        Random random = new Random(System.currentTimeMillis());
        n = n > TradingStrategyMonitor.getStrategies().size() ? TradingStrategyMonitor.getStrategies().size() : n;
        List<String> stocks = new ArrayList<>();
        List<String> allStock = new ArrayList<>(TradingStrategyMonitor.getStrategies().keySet());
        for (int i = 0; i < n; i++) {
            int index = random.nextInt(TradingStrategyMonitor.getStrategies().size());
            stocks.add(allStock.get(index));
        }
        return stocks;
    }
}
