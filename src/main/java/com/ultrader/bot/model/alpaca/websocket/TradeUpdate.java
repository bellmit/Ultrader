package com.ultrader.bot.model.alpaca.websocket;

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
public class TradeUpdate {
    private String event;
    private int qty;
    private double price;
    private Date timestamp;
    private Order order;
}
