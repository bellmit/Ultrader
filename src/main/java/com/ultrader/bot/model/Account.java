package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Account Model
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String id;
    private String status;
    private String currency;
    private double buyingPower;
    private double cash;
    private double cashWithdrawable;
    private double portfolioValue;
    private boolean isDayTrader;
    private boolean isTradingBlocked;
    private boolean isTransfersBlocked;
    private boolean isAccountBlocked;

}
