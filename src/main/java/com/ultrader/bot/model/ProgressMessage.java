package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Websocket progress message
 * @author ytx1991
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressMessage {
    private String status;
    private String message;
    private int progress;
}
