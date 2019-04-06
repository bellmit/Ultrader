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
    private Date timestamp;
    private Boolean is_open;
    private Date next_open;
    private Date next_close;
}
