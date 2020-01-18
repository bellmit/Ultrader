package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Market Information
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketInfo {
    /**
     * Current timestamp
     */
    private Date timestamp;
    /**
     * Whether or not the market is open
     */
    private Boolean isOpen;
    /**
     * Next market open timestamp
     */
    private Date nextOpenDate;
    /**
     * Next market close timestamp
     */
    private Date nextCloseDate;
}
