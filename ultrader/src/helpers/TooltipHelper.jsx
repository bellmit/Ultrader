import React from "react";
import { Tooltip, OverlayTrigger } from "react-bootstrap";
import Button from "components/CustomButton/CustomButton.jsx";
var dictionary = {
    "UltraderKey": "Ultrader generated key, functions will be disabled if it is missing",
    "UltraderSecret": "Ultrader generated secret, functions will be disabled if it is missing",
    "AlpacaPaperKey": "Alpaca generated key for trading stocks in Alpaca paper account",
    "AlpacaPaperSecret": "Alpaca generated secret for trading stocks in Alpaca paper account",
    "AlpacaLiveKey": "Alpaca generated key for trading stocks in Alpaca live account",
    "AlpacaLiveSecret": "Alpaca generated secret for trading stocks in Alpaca live account",
    "TradingPlatform": "Stock trading platform. (Alpaca Paper / Alpaca Live)",
    "MarketDataPlatform": "Platform to get market data. (Alpaca Live account is required for Polygon)",
    "AutoTradeEnabled": "Enable auto trading or not. No stock will be bought or sold when it is disabled.",
    "TradeExchangeList": "A list of Exchanges. Only stocks in these Exchanges will be traded.",
    "TradePeriod": "Aggregate how much market data into a bar.",
    "WhiteListEnabled": "Regard user defined stock list as a white list or black list.",
    "StockList": "User defined stock list. Can be regarded as a white list or black list.",
    "TradePriceLimitMax": "Don't trade stocks which prices are higher than this value. -1 for no limit.",
    "TradePriceLimitMin": "Don't trade stocks which prices are lower than this value. -1 for no limit.",
    "TradeVolumeLimitMax": "Don't trade stocks which volume per trade period are higher than this value. -1 for no limit.",
    "TradeVolumeLimitMin": "Don't trade stocks which volume per trade period are lower than this value. -1 for no limit.",
    "TradeBuyLimitMax": "Buying power spend on each trade. If it ends with % then each trade spends that many percents of the buying power (margin may include).",
    "TradeHoldLimitMax": "How many stocks you can hold at a same time.",
    "BuyStrategy": "Strategy for buying stocks.",
    "SellStrategy": "Strategy for selling stocks.",
    "BuyOrderType": "The types of buying orders.",
    "SellOrderType": "The types of selling orders.",
    "StrategyTemplate": "A good start point to build your own strategy.",
    "DayTradingMarginCheck": "both, entry, or exit. Controls Day Trading Margin Call (DTMC) checks.",
    "UseMargin": "Use Alpaca margin in trading",
    "EmailNotification": "all or none. If none, emails for order fills are not sent.",
    "SuspendTrade": "If true, new orders are blocked.",
    "NoShortingTrade": "If true, account becomes long-only mode.",
    "AssetListsPage": "Asset list is a list of stocks or other assets. It can be used for limiting the scope of trading or back testing and optimization the trading strategy.",
    "BackTestPage": "Back test a trading strategy to evaluate the performance.",
    "OptimizationPage": "Optimize a trading strategy to maximize the optimization goal. Essentially, to make more profit. It will return a list of recommended value for some parameters.",
    "HistoryOrdersPage": "Closed orders, which mean nothing will change anymore.",
    "PendingOrdersPage": "Opening orders, the price may change over time.",
    "PositionsPage": "The assets you currently hold.",
    "RulesPage": "Trading rule is the atomic building block of a trading strategy. It described a conditional you are interested in.",
    "StrategiesPage": "Strategy is a complex combination of trading rules. It a boolean expression to describe when you want to buy or sell.",
    "ConditionalSettingsPage" : "A group of settings only take effect during a specific market trend, such as NORMAL, BULL, SELL, etc.",
    "UsersPage" : "Management other users if you want to share Ultrader with others."
}
export function tooltip(term) {
  if(term in dictionary) {
    return (
      <OverlayTrigger placement="top" overlay={<Tooltip id={term+"tooltip"}>{dictionary[term]}</Tooltip>}>
        <Button bsStyle="default" simple type="button" bsSize="xs">
          <i className="fa fa-info-circle" color="secondary"></i>
        </Button>
      </OverlayTrigger>
    );
  } else {
    return (
      <OverlayTrigger placement="top" overlay={<Tooltip id={term+"tooltip"}>{term}</Tooltip>}>
        <Button bsStyle="default" simple type="button" bsSize="xs">
          <i className="fa fa-info-circle" color="secondary"></i>
        </Button>
      </OverlayTrigger>
    );
  }
}
