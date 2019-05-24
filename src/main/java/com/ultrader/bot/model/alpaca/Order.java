package com.ultrader.bot.model.alpaca;

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
public class Order {
    private String id;
    private String client_order_id;
    private Date created_at;
    private Date updated_at;
    private Date submitted_at;
    private Date filled_at;
    private Date failed_at;
    private Date expired_at;
    private Date canceled_at;
    private String asset_id;
    private String symbol;
    private String asset_class;
    private Integer qty;
    private Integer filled_qty;
    private String type;
    private String order_type;
    private String side;
    private String time_in_force;
    private Double limit_price;
    private Double stop_price;
    private Double filled_avg_price;
    private String status;
}
