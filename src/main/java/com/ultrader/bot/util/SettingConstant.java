package com.ultrader.bot.util;

/**
 * Enum of Settings
 * @author ytx1991
 */
public enum SettingConstant {
    //Alpaca Live trading key
    ALPACA_KEY("KEY_ALPACA_KEY"),
    //Alpaca Live trading secret
    ALPACA_SECRET("KEY_ALPACA_SECRET"),
    //Alpaca paper trading key
    ALPACA_PAPER_KEY("KEY_ALPACA_PAPER_KEY"),
    //Alpaca paper trading secret
    ALPACA_PAPER_SECRET("KEY_ALPACA_PAPER_SECRET"),
    //Ultrader key
    BOT_KEY("KEY_ULTRADER_KEY"),
    //Ultrader secret
    BOT_SECRET("KEY_ULTRADER_SECRET"),
    //Trading platform name
    TRADING_PLATFORM("GLOBAL_TRADING_PLATFORM"),
    //Market data platform name
    MARKET_DATA_PLATFORM("GLOBAL_MARKETDATA_PLATFORM"),
    //Max tolerance of the time gap between two continuous data point
    MARKET_DATA_MAX_GAP("GLOBAL_MARKETDATA_MAX_GAP"),
    //Enabled auto trading
    AUTO_TRADING_ENABLE("GLOBAL_AUTO_TRADING_ENABLE"),
    //Trading period, 1min, 5min, etc
    TRADE_PERIOD_SECOND("TRADE_PERIOD_SECOND"),
    //Regards the customized stock list as white list
    WHITE_LIST_ENABLE("TRADE_WHITE_LIST_ENABLE"),
    //User customized stock list
    TRADE_STOCK_LIST("TRADE_STOCK_LIST"),
    //White list
    TRADE_WHITE_LIST("TRADE_WHITE_LIST"),
    //Black list
    TRADE_BLACK_LIST("TRADE_BLACK_LIST"),
    //Stock exchanges that need to be considered
    TRADE_EXCHANGE("TRADE_EXCHANGE_LIST"),
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
    TRADE_SELL_ORDER_TYPE("TRADE_SELL_ORDER_TYPE"),
    //Sell order type, limit, market, etc
    TRADE_MARKET_TREND("TRADE_MARKET_TREND"),
    //Sell for cover margin automatically
    TRADE_AUTO_COVER("TRADE_AUTO_COVER"),
    //The max bars that indicator can use to calculate
    INDICATOR_MAX_LENGTH("INDICATOR_MAX_LENGTH"),
    //both, entry, or exit. Controls Day Trading Margin Call (DTMC) checks.
    ALPACA_DTMC("ALPACA_DTMC"),
    //Use Alpaca margin in trading
    ALPACA_USE_MARGIN("ALPACA_USE_MARGIN"),
    //all or none. If none, emails for order fills are not sent.
    ALPACA_TRADE_CONFIRM_EMAIL("ALPACA_TRADE_CONFIRM_EMAIL"),
    //If true, new orders are blocked.
    ALPACA_SUSPEND_TRADE("ALPACA_SUSPEND_TRADE"),
    //If true, account becomes long-only mode.
    ALPACA_NO_SHORTING("ALPACA_NO_SHORTING"),
    //Polygon web socket update frequency.
    POLYGON_WS_FREQUENCY("POLYGON_WS_FREQUENCY");

    public static final String DELIMITER = ",";
    private String name;

    SettingConstant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
