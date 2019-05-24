package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Trade model
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trade {
    private String symbol;
    private int qty;
    private Date buyDate;
    private Date sellDate;
    private Double buyPrice;
    private Double sellPrice;
    private Double profit;
}
