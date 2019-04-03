package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyVerificationRequest {
    private String ultraderKey;
    private String platformKey;
    private String platformName;
    private String token;
    private Long timestamp;
}
