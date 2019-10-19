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
    /**
     * Asset ID.
     */
    private String id;
    /**
     *“us_equity”
     */
    private String asset_class;
    /**
     * AMEX, ARCA, BATS, NYSE, NASDAQ or NYSEARCA
     */
    private String exchange;
    /**
     * Asset symbol
     */
    private String symbol;
    /**
     * active or inactive
     */
    private String status;
    /**
     * Asset is tradable on Alpaca or not.
     */
    private Boolean tradable;
    /**
     * Asset is marginable or not.
     */
    private Boolean marginable;
    /**
     * Asset is shortable or not.
     */
    private Boolean shortable;
    /**
     * Asset is easy-to-borrow or not (filtering for easy_to_borrow = True is the best way to check whether the name is currently available to short at Alpaca).
     */
    private Boolean easy_to_borrow;
}
