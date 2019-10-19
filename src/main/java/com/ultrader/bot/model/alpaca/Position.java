package com.ultrader.bot.model.alpaca;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Alpaca Position Entity
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Position {
    /**
     * Asset ID
     */
    private String asset_id;
    /**
     * Symbol name of the asset
     */
    private String symbol;
    /**
     * Exchange name of the asset
     */
    private String exchange;
    /**
     * Asset class name
     */
    private String asset_class;
    /**
     * Average entry price of the position
     */
    private double avg_entry_price;
    /**
     * The number of shares
     */
    private int qty;
    /**
     * “long”
     */
    private String side;
    /**
     * Total dollar amount of the position
     */
    private double market_value;
    /**
     * Total cost basis in dollar
     */
    private double cost_basis;
    /**
     * Unrealized profit/loss in dollars
     */
    private double unrealized_pl;
    /**
     * Unrealized profit/loss percent (by a factor of 1)
     */
    private double unrealized_plpc;
    /**
     * Unrealized profit/loss in dollars for the day
     */
    private double unrealized_intraday_pl;
    /**
     * Unrealized profit/loss percent (by a factor of 1)
     */
    private double unrealized_intraday_plpc;
    /**
     * Current asset price per share
     */
    private double current_price;
    /**
     * Last day’s asset price per share based on the closing value of the last trading day
     */
    private double lastday_price;
    /**
     * Percent change from last day price (by a factor of 1)
     */
    private double change_today;
}
