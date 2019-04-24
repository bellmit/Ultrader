package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Result of back testing
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BackTestingResult {
    private String stock;
    private int tradingCount;
    private double profitTradesRatio;
    private double rewardRiskRatio;
    private double vsBuyAndHold;
    private double totalProfit;
    private String startDate;
    private String endDate;
}
