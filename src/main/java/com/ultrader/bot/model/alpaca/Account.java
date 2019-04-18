package com.ultrader.bot.model.alpaca;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Alpaca Account Entity
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String id;
    private String status;
    private String currency;
    private double buying_power;
    private double cash;
    private double cash_withdrawable;
    private double portfolio_value;
    private boolean pattern_day_trader;
    private boolean trading_blocked;
    private boolean transfers_blocked;
    private boolean account_blocked;
    private Date created_at;
}
