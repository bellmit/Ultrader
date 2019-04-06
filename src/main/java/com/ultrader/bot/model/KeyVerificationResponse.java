package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
/**
 * License verification response model
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyVerificationResponse {
    private String token;
    private Long timestamp;
    private String message;
}
