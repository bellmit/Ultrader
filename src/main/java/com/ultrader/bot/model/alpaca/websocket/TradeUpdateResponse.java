package com.ultrader.bot.model.alpaca.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Alpaca web socket response entity
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeUpdateResponse {
    private String stream;
    private TradeUpdate data;
}
