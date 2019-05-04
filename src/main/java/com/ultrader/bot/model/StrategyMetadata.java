package com.ultrader.bot.model;

import com.ultrader.bot.util.IndicatorType;
import com.ultrader.bot.util.RuleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Trading Strategy, Rule, Indicator metadata
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyMetadata {
    private RuleType[] ruleTypes;
    private IndicatorType[] indicatorTypes;
    private Map<String, List<String>> categoryIndicatorMap;
}
