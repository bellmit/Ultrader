package com.ultrader.bot.model.alpaca;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Alpaca Order Entity
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    public static final String SELL = "sell";
    public static final String BUY = "buy";
    /**
     * order id
     */
    private String id;
    /**
     * client unique order id
     */
    private String client_order_id;
    /**
     * string<timestamp>
     */
    private Date created_at;
    /**
     * string<timestamp> (Nullable)
     */
    private Date updated_at;
    /**
     * string<timestamp> (Nullable)
     */
    private Date submitted_at;
    /**
     * string<timestamp> (Nullable)
     */
    private Date filled_at;
    /**
     * string<timestamp> (Nullable)
     */
    private Date expired_at;
    /**
     * string<timestamp> (Nullable)
     */
    private Date canceled_at;
    /**
     * string<timestamp> (Nullable)
     */
    private Date failed_at;
    /**
     * asset ID
     */
    private String asset_id;
    /**
     * Asset symbol
     */
    private String symbol;
    /**
     * Asset class
     */
    private String asset_class;
    /**
     * Ordered quantity
     */
    private Integer qty;
    /**
     * Filled quantity
     */
    private Integer filled_qty;
    /**
     * Valid values: market, limit, stop, stop_limit
     */
    private String type;
    private String order_type;
    /**
     * Valid values: buy, sell
     */
    private String side;
    /**
     * See https://docs.alpaca.markets/orders/#order-lifecycle page
     */
    private String time_in_force;
    /**
     * Limit price
     */
    private Double limit_price;
    /**
     * Stop price
     */
    private Double stop_price;
    private Double filled_avg_price;
    /**
     * See https://docs.alpaca.markets/orders/#order-lifecycle page
     */
    private String status;
    /**
     * If true, eligible for execution outside regular trading hours.
     */
    private Boolean extended_hours;
}
