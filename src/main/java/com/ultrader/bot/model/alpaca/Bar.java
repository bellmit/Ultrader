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
    private int t;
    private float o;
    private float h;
    private float l;
    private float c;
    private float v;
}
