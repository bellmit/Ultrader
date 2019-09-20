package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationResult {
    private int iterationNum;
    private List<List<Double>> parameters;
    private List<BackTestingResult> results;
    private List<Double> avgProfit;
    private List<String> parameterNames;
}
