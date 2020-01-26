package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradingNotification {
    private String strategyId;
    private String symbol;
    private String side;
    private double price;
    private String type;
    private String amount;
    private List<String> currentPositions;
}
