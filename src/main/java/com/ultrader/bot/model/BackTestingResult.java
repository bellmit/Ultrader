package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private double buyAndHold;
    private double totalProfit;
    private double averageHoldingDays;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
