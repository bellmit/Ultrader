package com.ultrader.bot.model.polygon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Polygon Aggregation Entity
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Aggregation {
    private String ev;
    private String sym;
    private long v;
    private long av;
    private double op;
    private double vw;
    private double o;
    private double c;
    private double h;
    private double l;
    private double a;
    private int z;
    private int n;
    private long s;
    private long e;
}
