package com.ultrader.bot.util;

/**
 * Enum of Settings
 */
public enum SettingConstant {
    ALPACA_KEY_NAME("KEY_ALPACA_KEY"),
    ALPACA_SECRET_NAME("KEY_ALPACA_SECRET"),
    BOT_KEY_NAME("KEY_ULTRADER_KEY"),
    BOT_SECRET_NAME("KEY_ULTRADER_SECRET"),
    TRADING_PLATFORM_NAME("GLOBAL_TRADING_PLATFORM"),
    WHITE_LIST_ENABLE_NAME("TRADE_WHITE_LIST_ENABLE"),
    TRADE_STOCK_LIST_NAME("TRADE_STOCK_LIST"),
    TRADE_EXCHANGE_NAME("TRADE_EXCHANGE_LIST"),
    MARKET_DATA_PLATFORM_NAME("GLOBAL_MARKETDATA_PLATFORM"),
    INDICATOR_MAX_LENGTH("INDICATOR_MAX_LENGTH"),
    TRADE_PERIOD_SECOND("TRADE_PERIOD_SECOND");

    public static final String DELIMITER = ",";
    private String name;

    SettingConstant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
