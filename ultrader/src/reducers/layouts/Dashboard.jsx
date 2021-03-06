import _ from "lodash";

import * as ACTION_TYPES from "actions/layouts/DashboardActions";
import { parseMoney, parsePercentage } from "helpers/ParseHelper";
const initialState = {
  stompClient: null,
  socket: null,
  monitorMessages: [],
  ruleTypes: [],
  ruleTypeSelectOptions: [],
  indicatorTypes: [],
  indicatorTypesSelectOptions: [],
  indicatorCategories: {},
  categoryIndicatorMap: {},
  portfolio: {
    value: 0,
    buyingPower: 0,
    withdrawableCash: 0
  },
  trades: {
    buyAmount: 0,
    sellAmount: 0,
    buyCount: 0,
    sellCount: 0
  },
  positions: {
    holds: 0,
    profitStocks: 0,
    profit: 0
  },
  profit: {
    "1": {
      periodDays: 1,
      totalTrades: 0,
      totalProfit: 0,
      averageProfit: 0,
      averageProfitRatio: 0
    },
    "7": {
      periodDays: 7,
      totalTrades: 0,
      totalProfit: 0,
      averageProfit: 0,
      averageProfitRatio: 0
    },
    "30": {
      periodDays: 30,
      totalTrades: 0,
      totalProfit: 0,
      averageProfit: 0,
      averageProfitRatio: 0
    },
    "365": {
      periodDays: 365,
      totalTrades: 0,
      totalProfit: 0,
      averageProfit: 0,
      averageProfitRatio: 0
    }
  },
  systemStatus: {
    data: {
      status: "loading",
      detail: "Data Update Status Detail: Status is not updated yet."
    },
    market: {
      status: "loading",
      detail: "Market Status Detail: Status is not updated yet."
    },
    account: {
      status: "loading",
      detail: "Account Status Detail: Status is not updated yet."
    },
    bot: {
      status: "loading",
      detail: "Bot Status Detail: Status is not updated yet."
    }
  },
  notifications: [
    /*
    {
      level: "error",
      message: "This is a test notification"
    },
    {
      level: "error",
      message: "This is a test notification 2"
    }
    */
  ],
  strategyTemplates: [],
  strategyTemplateOptions: []
};

const global = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.CONNECTED_TO_MONITOR:
      return {
        ...state,
        stompClient: action.stompClient,
        socket: action.socket
      };
    case ACTION_TYPES.RECEIVED_DATA_STATUS_MESSAGE:
      var messageBody = JSON.parse(action.response.body);
      return {
        ...state,
        systemStatus: {
          ...state.systemStatus,
          data: {
            status: messageBody.status,
            detail: "Data Update Status Detail: " + messageBody.message
          }
        }
      };
    case ACTION_TYPES.RECEIVED_MARKET_STATUS_MESSAGE:
      var messageBody = JSON.parse(action.response.body);
      var marketStatus = messageBody.status == "closed" ? "error" : "success";
      return {
        ...state,
        systemStatus: {
          ...state.systemStatus,
          market: {
            status: marketStatus,
            detail: "Market Status Detail: " + messageBody.message
          }
        }
      };
    case ACTION_TYPES.RECEIVED_BOT_STATUS_MESSAGE:
      var live = action.response;

      var botStatus = live ? "success" : "error";
      var botStatusMessage = live
        ? "Bot is running on the backend."
        : "Bot is not running on the backend.";
      return {
        ...state,
        systemStatus: {
          ...state.systemStatus,
          bot: {
            status: botStatus,
            detail: "Bot Status Detail: " + botStatusMessage
          }
        }
      };
    case ACTION_TYPES.RECEIVED_PORTFOLIO_MONITOR_MESSAGE:
      var messageBody = JSON.parse(action.response.body).data;
      var accountStatus = messageBody.IsTradingBlocked ? "error" : "success";
      var changePercentage =
        messageBody.Change / (messageBody.Portfolio - messageBody.Change);
      var signal = messageBody.Change >= 0 ? "+" : "";
      var accountStatusMessage = messageBody.IsTradingBlocked
        ? "Trading is blocked in this account."
        : "Account is able to trade.";
      return {
        ...state,
        portfolio: {
          value: messageBody.Portfolio,
          buyingPower: messageBody.BuyingPower,
          change: messageBody.Change
        },
        systemStatus: {
          ...state.systemStatus,
          account: {
            status: accountStatus,
            detail: "Account Status Detail: " + accountStatusMessage
          }
        }
      };
    case ACTION_TYPES.RECEIVED_TRADES_MONITOR_MESSAGE:
      var messageBody = JSON.parse(action.response.body).data;
      return {
        ...state,
        trades: {
          buyAmount: messageBody.BuyAmount,
          sellAmount: messageBody.SellAmount,
          buyCount: messageBody.BuyCount,
          sellCount: messageBody.SellCount
        }
      };
    case ACTION_TYPES.RECEIVED_PROFIT_MONITOR_MESSAGE:
      var messageBody = JSON.parse(action.response.body).data;
      return {
        ...state,
        profit: {
          ...state.profit,
          ["" + messageBody.PeriodDays]: {
            ...state.profit["" + messageBody.PeriodDays],
            periodDays: messageBody.PeriodDays,
            totalTrades: messageBody.TotalTrades,
            totalProfit: messageBody.TotalProfit,
            averageProfit: messageBody.AverageProfit,
            averageProfitRatio: messageBody.AverageProfitRatio
          }
        }
      };
    case ACTION_TYPES.RECEIVED_POSITION_MONITOR_MESSAGE:
      var messageBody = JSON.parse(action.response.body).data;
      return {
        ...state,
        positions: {
          holds: messageBody.Holds,
          profitStocks: messageBody.ProfitableStock,
          profit: messageBody.Profit
        }
      };
    case ACTION_TYPES.RETRIEVED_NOTIFICATION_MESSAGE:
      var messageBody = JSON.parse(action.response.body);
      var level = "info";
      var icon = "pe-7s-info";
      switch (messageBody.type) {
        case "BUY":
          level = "#28a745";
          icon = "pe-7s-plus";
          break;
        case "SELL":
          level = "#28a745";
          icon = "pe-7s-less";
          break;
        case "WARN":
          level = "#ffc107";
          icon = "pe-7s-speaker";
          break;
        case "ERROR":
          level = "#dc3545";
          icon = "pe-7s-speaker";
          break;
        default:
          break;
      }
      return {
        ...state,
        notifications: [
          ...state.notifications,
          {
            level: level,
            icon: icon,
            new: true,
            message: messageBody
          }
        ]
      };
    case ACTION_TYPES.RETRIEVED_STRATEGY_TEMPLATE:
      let strategyTemplates = action.response.data;
      const strategyTemplateOptions = strategyTemplates.map(
        strategyTemplate => {
          return {
            ...strategyTemplate,
            value: strategyTemplate.name,
            label: strategyTemplate.name
          };
        }
      );
      return {
        ...state,
        strategyTemplates: strategyTemplates,
        strategyTemplateOptions: strategyTemplateOptions
      };
    case ACTION_TYPES.RETRIEVED_STRATEGY_METADATA:
      let strategyMetadata = action.response.data;

      let indicatorTypes = strategyMetadata.indicatorTypes;
      let indicatorTypesSelectOptions = [];

      let ruleTypes = strategyMetadata.ruleTypes;
      let ruleTypeSelectOptions = _.map(ruleTypes, ruleType => {
        return {
          value: ruleType.args,
          label: ruleType.name,
          classz: ruleType.classz,
          descriptions: ruleType.descriptions,
          argName: ruleType.argName
        };
      });

      let categoryIndicatorMap = strategyMetadata.categoryIndicatorMap;
      let indicatorSelectOptions = {};
      for (var property in categoryIndicatorMap) {
        if (categoryIndicatorMap.hasOwnProperty(property)) {
          let indicatorSelectOptionsForProp = [];
          for (var categoryIndicator of categoryIndicatorMap[property]) {
            let indicatorWithTypes = indicatorTypes.find(
              el => el.classz == categoryIndicator
            );
            let indicatorWithEachType = _.map(
              indicatorWithTypes.args,
              (args, index) => {
                return {
                  value: {
                    label: categoryIndicator,
                    args: args,
                    argName: indicatorWithTypes.argName[index],
                    description: indicatorWithTypes.descriptions[index]
                  },
                  label:
                    categoryIndicator +
                    " (" +
                    indicatorWithTypes.argName[index] +
                    ")"
                };
              }
            );
            indicatorSelectOptionsForProp = indicatorSelectOptionsForProp.concat(
              indicatorWithEachType
            );
          }
          indicatorSelectOptions[property] = indicatorSelectOptionsForProp;
        }
      }
      return {
        ...state,
        categoryIndicatorMap: categoryIndicatorMap,
        indicatorSelectOptions: indicatorSelectOptions,
        indicatorTypes: indicatorTypes,
        indicatorTypesSelectOptions: indicatorTypesSelectOptions,
        ruleTypes: ruleTypes,
        ruleTypeSelectOptions: ruleTypeSelectOptions
      };
    case ACTION_TYPES.RETRIEVED_DASHBOARD_NOTIFICATIONS:
      return {
        ...state
      };
    default:
      return state;
  }
};

export default global;
