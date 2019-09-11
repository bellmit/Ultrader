package com.ultrader.bot.util;

import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.dao.StrategyDao;
import com.ultrader.bot.model.BackTestingResult;
import com.ultrader.bot.model.OptimizationResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.TimeSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Machine Learning tool to find the best parameters combination
 *
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradientDescentOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradientDescentOptimizer.class);
    private StrategyDao strategyDao;
    private RuleDao ruleDao;
    private List<TimeSeries> timeSeries;
    private List<Double> parameters;
    private int buyStrategyId;
    private int sellStrategyId;
    private double probeValue;
    private double step;
    private double convergeThreshold;
    private int iteration;

    /**
     * Optimize trading strategy based on GD algorithm
     * @return
     */
    public OptimizationResult optimize() {
        //init
        OptimizationResult result = new OptimizationResult();
        List<List<Double>> parameterHistory = new ArrayList<>();
        result.setParameters(parameterHistory);
        List<BackTestingResult> backTestingResults = new ArrayList<>();
        result.setResults(backTestingResults);
        parameterHistory.add(parameters);
        BackTestingResult lastResult = null;
        BackTestingResult currentResult = evaluateCost(parameters);
        backTestingResults.add(currentResult);
        int iterationCount = 0;
        LOGGER.info("Start Optimization. {} assets, BuyStrategyId: {}, SellStrategyId: {}, probeValue: {}, learningRate: {}, CovergeThreshold: {}, MaxIteration: {}",
                timeSeries.size(), buyStrategyId, sellStrategyId, probeValue, step, convergeThreshold, iteration);
        //start iteration
        do {
            lastResult = currentResult;
            //Calculate gradient
            List<Double> gradients = calculateGradient(parameters);
            //Update Parameters
            parameters = updateParameters(gradients);
            //Evaluate new parameters
            currentResult = evaluateCost(parameters);
            iterationCount++;

            //If no change just go further
            while (Math.abs(currentResult.getTotalProfit() - lastResult.getTotalProfit()) < 0.00000001) {
                parameters = updateParameters(gradients);
                currentResult = evaluateCost(parameters);
            }
            //Recording
            parameterHistory.add(parameters);
            backTestingResults.add(currentResult);
            LOGGER.info("Iteration {} finished. Avg Profit: {}", iterationCount, currentResult.getTotalProfit() / currentResult.getTradingCount());
        } while (!isConverged(lastResult, currentResult, iterationCount));
        result.setIterationNum(iterationCount);
        return result;
    }

    /**
     * Calculate parameter gradient based on a probe way
     * @param parameters
     * @return
     */
    private List<Double> calculateGradient(List<Double> parameters) {
        List<Double> gradients = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder("Gradients:");
        //Calculate derivative
        for (int i = 0; i < parameters.size(); i++) {
            List<Double> x1 = parameters.stream().map(p -> p.doubleValue()).collect(Collectors.toList());
            x1.set(i, x1.get(i) + probeValue);
            List<Double> x2 = parameters.stream().map(p -> p.doubleValue()).collect(Collectors.toList());
            x2.set(i, x2.get(i) - probeValue);
            BackTestingResult y1 = evaluateCost(x1);
            BackTestingResult y2 = evaluateCost(x2);
            //This is for searching the MAX point
            Double gradient = (y1.getTotalProfit() / y1.getTradingCount() - y2.getTotalProfit() / y2.getTradingCount()) / (2 * probeValue);
            gradients.add(gradient);
            stringBuilder.append(" " + gradient);
        }
        LOGGER.info(stringBuilder.toString());
        return gradients;
    }

    /**
     * Input the parameter to evaluate the strategy performance
     * @param parameters
     * @return
     */
    private BackTestingResult evaluateCost(List<Double> parameters) {
        int rewardRiskRatioCount = 0, tradingCount = 0;
        double profitTradesRatio = 0.0, rewardRiskRatio = 0.0, vsBuyAndHold = 0.0, totalProfit = 0.0, averageHoldingDays = 0.0;

        for (TimeSeries asset : timeSeries) {
            BackTestingResult result = TradingUtil.backTest(asset, buyStrategyId, sellStrategyId, strategyDao, ruleDao, parameters);
            if (result != null) {
                LOGGER.debug(result.toString());
                tradingCount += result.getTradingCount();
                totalProfit += result.getTotalProfit();
                vsBuyAndHold += result.getBuyAndHold();
                if (result.getTradingCount() > 0) {
                    profitTradesRatio += result.getProfitTradesRatio() * result.getTradingCount();
                    rewardRiskRatio += result.getRewardRiskRatio();
                    rewardRiskRatioCount += 1;
                    averageHoldingDays += result.getAverageHoldingDays();
                }
            }
        }
        return new BackTestingResult(
                "All",
                tradingCount,
                tradingCount == 0 ? 0 : (profitTradesRatio/tradingCount),
                rewardRiskRatioCount == 0 ? 0 : rewardRiskRatio / rewardRiskRatioCount,
                vsBuyAndHold / timeSeries.size(),
                totalProfit,
                rewardRiskRatioCount == 0 ? 0 : (averageHoldingDays / rewardRiskRatioCount),
                null,
                null);
    }

    /**
     * Update parameters based on the gradient
     * @param gradients
     * @return
     */
    private List<Double> updateParameters(List<Double> gradients) {
        List<Double> newParameters = new ArrayList<>();
        for (int i = 0 ; i < gradients.size(); i++) {
            newParameters.add(parameters.get(i) + step * gradients.get(i));
        }
        return newParameters;
    }

    /**
     * Check if learning is converged
     * @param lastResult
     * @param currentResult
     * @param iterationCount
     * @return
     */
    private boolean isConverged(BackTestingResult lastResult, BackTestingResult currentResult, int iterationCount) {
        if (lastResult == null || currentResult == null) {
            return false;
        }
        if (iterationCount == iteration || Math.abs(lastResult.getTotalProfit() / lastResult.getTradingCount() - currentResult.getTotalProfit() / currentResult.getTradingCount()) <= convergeThreshold) {
            return true;
        } else {
            return false;
        }
    }
}
