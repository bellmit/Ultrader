package com.ultrader.bot.util;

import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.dao.StrategyDao;
import com.ultrader.bot.model.BackTestingResult;
import com.ultrader.bot.model.OptimizationResult;

import com.ultrader.bot.model.ProgressMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    /**
     * Apply on step, make the step smaller
     */
    private static final double SUPPRESSOR = 0.8;
    /**
     * Max Inertia steps
     */
    private static final double MAX_INERTIA_STEPS = 10;
    private StrategyDao strategyDao;
    private RuleDao ruleDao;
    private List<TimeSeries> timeSeries;
    private List<Double> parameters;
    private List<String> parameterNames;
    private int buyStrategyId;
    private int sellStrategyId;
    private double step;
    private double convergeThreshold;
    private int iteration;
    /**
     * Optimization Goal
     * AVG_PROFIT - Maximize profit per trade
     * COMPOUND_PROFIT - profit considered trades num, expected profit
     */
    private String optimizeGoal;
    private Double percentPerTrade;
    private Long holdDays;
    private Integer maxHolds;
    private SimpMessagingTemplate notifier;
    private String topic;

    /**
     * Optimize trading strategy based on GD algorithm
     *
     * @return
     */
    public OptimizationResult optimize() {
        //init
        OptimizationResult result = new OptimizationResult();
        result.setParameterNames(parameterNames);
        List<List<Double>> parameterHistory = new ArrayList<>();
        List<Double> optimizationGoals = new ArrayList<>();
        result.setOptimizationGoal(optimizationGoals);
        result.setParameters(parameterHistory);
        List<BackTestingResult> backTestingResults = new ArrayList<>();
        result.setResults(backTestingResults);
        parameterHistory.add(parameters);
        BackTestingResult lastResult = null;
        BackTestingResult currentResult = evaluateCost(parameters);
        optimizationGoals.add(calculateOptimizationGoal(currentResult));
        backTestingResults.add(currentResult);
        int iterationCount = 0;
        //Randomize step
        //step = new Random(new Date().getTime()).nextInt(iteration);
        LOGGER.info("Start Optimization. {} assets, BuyStrategyId: {}, SellStrategyId: {}, learningRate: {}, CovergeThreshold: {}, MaxIteration: {}, OptimizationGoal: {}, PercentPerTrade: {}, HoldDays: {}, MaxHolds: {}",
                timeSeries.size(), buyStrategyId, sellStrategyId, step, convergeThreshold, iteration, optimizeGoal, percentPerTrade, holdDays, maxHolds);
        //start iteration
        do {
            iterationCount++;
            lastResult = currentResult;
            //Calculate gradient
            List<Double> gradients = calculateGradient(parameters);
            if (!checkGradient(gradients)) {
                LOGGER.error("Invalid gradient {}, Iteration {}", gradients, iterationCount);
                break;
            }
            //Adjust step size based on gradient
            adjustStepSize(gradients);
            //Update Parameters
            parameters = updateParameters(gradients);

            //Evaluate new parameters
            currentResult = evaluateCost(parameters);


            int inertiaCount = 0;
            //If no change just go further
            while (Math.abs(currentResult.getTotalProfit() - lastResult.getTotalProfit()) < 0.00000001 && MAX_INERTIA_STEPS > inertiaCount) {
                parameters = updateParameters(gradients);
                currentResult = evaluateCost(parameters);
                inertiaCount++;
            }
            //Recording


            parameterHistory.add(parameters);
            backTestingResults.add(currentResult);
            Double optimizationGoal = calculateOptimizationGoal(currentResult);
            optimizationGoals.add(optimizationGoal);
            LOGGER.info("Iteration {} finished. {}: {}, Parameters:{}, Step:{}", iterationCount, optimizeGoal, optimizationGoal, parameters, step);
            notifier.convertAndSend(topic, new ProgressMessage("InProgress", String.format("Optimizing your strategy: Iteration [%d], %s [%f]", iterationCount, optimizeGoal, optimizationGoal), 50 + 50 * iterationCount / iteration));
        } while (!isConverged(lastResult, currentResult, iterationCount));
        result.setIterationNum(iterationCount);
        return result;
    }

    /**
     * Calculate parameter gradient based on a probe way
     *
     * @param parameters
     * @return
     */
    private List<Double> calculateGradient(List<Double> parameters) {
        List<Double> gradients = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder("Gradients:");
        //Calculate derivative
        for (int i = 0; i < parameters.size(); i++) {
            Double probeValue = Math.abs(parameters.get(i) / 10);
            List<Double> x1 = parameters.stream().map(p -> p.doubleValue()).collect(Collectors.toList());
            x1.set(i, x1.get(i) + probeValue);
            List<Double> x2 = parameters.stream().map(p -> p.doubleValue()).collect(Collectors.toList());
            x2.set(i, x2.get(i) - probeValue);
            BackTestingResult y1 = evaluateCost(x1);
            BackTestingResult y2 = evaluateCost(x2);
            //This is for searching the MAX point
            Double gradient = (calculateOptimizationGoal(y1) - calculateOptimizationGoal(y2)) / (2 * probeValue);
            gradients.add(gradient);
            stringBuilder.append(" " + gradient);
        }
        LOGGER.info(stringBuilder.toString());
        return gradients;
    }

    /**
     * Input the parameter to evaluate the strategy performance
     *
     * @param parameters
     * @return
     */
    private BackTestingResult evaluateCost(List<Double> parameters) {
        int rewardRiskRatioCount = 0, tradingCount = 0;
        double profitTradesRatio = 0.0, rewardRiskRatio = 0.0, vsBuyAndHold = 0.0, totalProfit = 0.0, averageHoldingDays = 0.0;

        for (TimeSeries asset : timeSeries) {
            if (asset.getBarCount() == 0) {
                continue;
            }
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
                tradingCount == 0 ? 0 : (profitTradesRatio / tradingCount),
                rewardRiskRatioCount == 0 ? 0 : rewardRiskRatio / rewardRiskRatioCount,
                vsBuyAndHold / timeSeries.size(),
                totalProfit,
                rewardRiskRatioCount == 0 ? 0 : (averageHoldingDays / rewardRiskRatioCount),
                null,
                null);
    }

    /**
     * Update parameters based on the gradient
     *
     * @param gradients
     * @return
     */
    private List<Double> updateParameters(List<Double> gradients) {
        List<Double> newParameters = new ArrayList<>();
        for (int i = 0; i < gradients.size(); i++) {
            newParameters.add(parameters.get(i) + step * gradients.get(i));
        }
        return newParameters;
    }

    /**
     * Check if learning is converged
     *
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

    /**
     * Check if gradient is valid
     *
     * @param gradients
     * @return
     */
    private boolean checkGradient(List<Double> gradients) {
        for (Double gradient : gradients) {
            if (gradient.isNaN()) {
                return false;
            }
        }
        return true;
    }

    private void adjustStepSize(List<Double> gradients) {
        //If it's first iteration, adjust step dynamically
        boolean suitable = false;
        while (!suitable) {
            suitable = true;
            for (int i = 0; i < gradients.size(); i++) {
                if ((Math.abs(parameters.get(i)) + 0.1) /2 < Math.abs(gradients.get(i) * step)) {
                    //If any change for any parameter is bigger than parameter itself
                    //Then it's a big change should decrease step size
                    //Plus SUPPRESSOR to handle parameter = 0 case
                    suitable = false;
                    break;
                }
            }
            step = step * SUPPRESSOR;
        }
    }

    /**
     * Calculate loss based on the optimization goal
     *
     * @param result
     * @return
     */
    private Double calculateOptimizationGoal(BackTestingResult result) {
        if (optimizeGoal.equals(OptimizationType.AVG_PROFIT.name())) {
            return result.getTotalProfit() / result.getTradingCount();
        } else {
            return result.getTotalProfit();
        }
    }
}
