package com.ultrader.bot.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * General Message for Dashboard data
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDataMessage {
    private Map<String, String> data;
}
