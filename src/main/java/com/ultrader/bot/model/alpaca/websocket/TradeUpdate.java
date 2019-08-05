package com.ultrader.bot.model.alpaca.websocket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ultrader.bot.model.alpaca.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Alpaca web socket trade update entity
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeUpdate {
    private String event;
    private int position_qty;
    private double price;
    private Date timestamp;
    private Order order;
}
