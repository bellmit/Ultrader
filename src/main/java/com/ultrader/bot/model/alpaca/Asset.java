package com.ultrader.bot.model.alpaca;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Alpaca Asset Entity
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
    private String id;
    private String asset_class;
    private String exchange;
    private String symbol;
    private String status;
    private Boolean tradable;
}
