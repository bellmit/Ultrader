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
    private String asset_id;
    private String symbol;
    private String exchange;
    private String asset_class;
    private double avg_entry_price;
    private int qty;
    private String side;
    private double market_value;
    private double cost_basis;
    private double unrealized_pl;
    private double unrealized_plpc;
    private double unrealized_intraday_pl;
    private double unrealized_intraday_plpc;
    private double current_price;
    private double lastday_price;
    private double change_today;
}
