package com.ultrader.bot.util;

/**
 * Enum of Settings
 * @author ytx1991
 */
public enum SettingConstant {
    //Alpaca Live trading key
    ALPACA_KEY_NAME("KEY_ALPACA_KEY"),
    //Alpaca Live trading secret
    ALPACA_SECRET_NAME("KEY_ALPACA_SECRET"),
    //Alpaca paper trading key
    ALPACA_PAPER_KEY_NAME("KEY_ALPACA_PAPER_KEY"),
    //Alpaca paper trading secret
    ALPACA_PAPER_SECRET_NAME("KEY_ALPACA_PAPER_SECRET"),
    //Ultrader key
    BOT_KEY_NAME("KEY_ULTRADER_KEY"),
    //Ultrader secret
    BOT_SECRET_NAME("KEY_ULTRADER_SECRET"),
    //Trading platform name
    TRADING_PLATFORM_NAME("GLOBAL_TRADING_PLATFORM"),
    //Regards the customized stock list as white list
    WHITE_LIST_ENABLE_NAME("TRADE_WHITE_LIST_ENABLE"),
    //User customized stock list
    TRADE_STOCK_LIST_NAME("TRADE_STOCK_LIST"),
    //Stock exchanges that need to be considered
    TRADE_EXCHANGE_NAME("TRADE_EXCHANGE_LIST"),
    //Market data platform name
    MARKET_DATA_PLATFORM_NAME("GLOBAL_MARKETDATA_PLATFORM"),
    //The max bars that indicator can use to calculate
    INDICATOR_MAX_LENGTH("INDICATOR_MAX_LENGTH"),
    //Trading period, 1min, 5min, etc
    TRADE_PERIOD_SECOND("TRADE_PERIOD_SECOND"),
    //Enabled auto trading
    AUTO_TRADING_ENABLE("GLOBAL_AUTO_TRADING_ENABLE"),
    //The max stock price filter
    TRADE_PRICE_LIMIT_MAX("TRADE_PRICE_LIMIT_MAX"),
    //The min stock price filter
    TRADE_PRICE_LIMIT_MIN("TRADE_PRICE_LIMIT_MIN"),
    //The max stock volume filter
    TRADE_VOLUME_LIMIT_MAX("TRADE_VOLUME_LIMIT_MAX"),
    //The min stock price filter
    TRADE_VOLUME_LIMIT_MIN("TRADE_VOLUME_LIMIT_MIN"),
    //The max cost for each buy
    TRADE_BUY_MAX_LIMIT("TRADE_BUY_MAX_LIMIT"),
    //The max number of stocks can hold
    TRADE_BUY_HOLDING_LIMIT("TRADE_BUY_HOLDING_LIMIT"),
    //Strategy id of buying
    TRADE_BUY_STRATEGY("TRADE_BUY_STRATEGY"),
    //Strategy id of selling
    TRADE_SELL_STRATEGY("TRADE_SELL_STRATEGY"),
    //Buy order type, limit, market, etc
    TRADE_BUY_ORDER_TYPE("TRADE_BUY_ORDER_TYPE"),
    //Sell order type, limit, market, etc
    TRADE_SELL_ORDER_TYPE("TRADE_SELL_ORDER_TYPE");

    public static final String DELIMITER = ",";
    private String name;

    SettingConstant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
