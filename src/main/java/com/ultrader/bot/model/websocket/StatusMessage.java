package com.ultrader.bot.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Notification of each component status
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusMessage {
    private String status;
    private String message;
}
