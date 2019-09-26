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
    /**
     *     Account ID.
     */
    private String id;
    /**
     * See Account Status
     */
    private String status;
    /**
     * "USD"
     */
    private String currency;
    /**
     * Current available $ buying power;
     * If multiplier = 4, this is your daytrade buying power which is calculated as (last_equity - (last) maintenance_margin) * 4;
     * If multiplier = 2, buying_power = max(equity – initial_margin,0) * 2;
     * If multiplier = 1, buying_power = cash
     */
    private Double buying_power;
    /**
     * Cash balance
     */
    private Double cash;
    /**
     * Total value of cash + holding positions (This field is deprecated. It is equivalent to the equity field.)
     */
    private Double portfolio_value;
    /**
     * Whether or not the account has been flagged as a pattern day trader
     */
    private Boolean pattern_day_trader;
    /**
     * If true, the account is not allowed to place orders.
     */
    private Boolean trading_blocked;
    /**
     * If true, the account is not allowed to request money transfers.
     */
    private Boolean transfers_blocked;
    /**
     * If true, the account activity by user is prohibited.
     */
    private Boolean account_blocked;
    /**
     * Timestamp this account was created at
     */
    private Date created_at;
    /**
     * Account number.
     */
    private String account_number;
    /**
     * The current number of daytrades that have been made in the last 5 trading days (inclusive of today)
     */
    private Integer daytrade_count;
    /**
     * Your buying power for day trades (continuously updated value)
     */
    private Double daytrading_buying_power;
    /**
     * Cash + long_market_value + short_market_value
     */
    private Double equity;
    /**
     * User setting. If true, the account is not allowed to place orders.
     */
    private Boolean trade_suspended_by_user;
    /**
     * Flag to denote whether or not the account is permitted to short
     */
    private Boolean shorting_enabled;
    /**
     * Real-time MtM value of all long positions held in the account
     */
    private Double long_market_value;
    /**
     * Real-time MtM value of all short positions held in the account
     */
    private Double short_market_value;
    /**
     * Equity as of previous trading day at 16:00:00 ET
     */
    private Double last_equity;
    /**Buying power multiplier that represents account margin classification;
     * valid values 1 (standard limited margin account with 1x buying power),
     * 2 (reg T margin account with 2x intraday and overnight buying power;
     * this is the default for all non-PDT accounts with $2,000 or more equity),
     * 4 (PDT account with 4x intraday buying power and 2x reg T overnight buying power)
     */
    private Integer multiplier;
    /**
     * Reg T initial margin requirement (continuously updated value)
     */
    private Double initial_margin;
    /**
     * Maintenance margin requirement (continuously updated value)
     */
    private Double maintenance_margin;
    /**
     * Value of special memorandum account (will be used at a later date to provide additional buying_power)
     */
    private Double sma;
    /**
     * Your maintenance margin requirement on the previous trading day
     */
    private Double last_maintenance_margin;
    /**
     * Your buying power under Regulation T (your excess equity - equity minus margin value - times your margin multiplier)
     */
    private Double regt_buying_power;
}
