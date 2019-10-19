package com.ultrader.bot.model.alpaca;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountConfiguration {
    /**
     * both, entry, or exit. Controls Day Trading Margin Call (DTMC) checks.
     */
    private String dtbp_check;

    /**
     * If true, account becomes long-only mode.
     */
    private boolean no_shorting;

    /**
     * all or none. If none, emails for order fills are not sent.
     */
    private String trade_confirm_email;

    /**
     * If true, new orders are blocked.
     */
    private boolean suspend_trade;
}
