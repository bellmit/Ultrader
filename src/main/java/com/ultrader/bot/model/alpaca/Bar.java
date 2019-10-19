package com.ultrader.bot.model.alpaca;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Alpaca Bar Entity
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bar {
    /**
     * the beginning time of this bar as a Unix epoch in seconds
     */
    private long t;
    /**
     * open price
     */
    private float o;
    /**
     * high price
     */
    private float h;
    /**
     * low price
     */
    private float l;
    /**
     * close price
     */
    private float c;
    /**
     * volume
     */
    private float v;
}
