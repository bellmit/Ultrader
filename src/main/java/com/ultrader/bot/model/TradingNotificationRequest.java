package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradingNotificationRequest {
    private String strategyId;
    private String order;
    private String token;
    private Long timestamp;
}
