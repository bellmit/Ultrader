package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model for export/import strategies
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyBundle {
    private List<Strategy> strategies;
    private List<Rule> rules;
}
