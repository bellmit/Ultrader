package com.ultrader.bot.model.polygon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Polygon Trade Entity
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trade {
    private String ev;
    private String sym;
    private int x;
    private double p;
    private long s;
    private int[] c;
    private long t;
}
