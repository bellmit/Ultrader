package com.ultrader.bot.model.alpaca;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Alpaca Clock Entity
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Clock {
    /**
     * Current timestamp
     */
    private Date timestamp;
    /**
     * Whether or not the market is open
     */
    private Boolean is_open;
    /**
     * Next market open timestamp
     */
    private Date next_open;
    /**
     * Next market close timestamp
     */
    private Date next_close;
}
