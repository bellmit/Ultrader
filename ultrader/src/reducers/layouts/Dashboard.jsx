import _ from "lodash";

import * as ACTION_TYPES from "actions/layouts/DashboardActions";

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
  daily: {
    netIncome: 0,
    averageProfit: 0,
    averageProfitRate: 0
  },
  performance: {
    market: 0,
    portfolio: 0,
    comparison: 0
  },
  systemStatus: {
    data: {
      status: "loading",
      detail: "Data Update Status Detail: Status is not updated yet."
    },
    market: {
      status: "loading",
      detail: "Market Status Detail: Status is not updated yet."
    }
  }
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
      return {
        ...state,
        systemStatus: {
          ...state.systemStatus,
          market: {
            status: messageBody.status,
            detail: "Market Status Detail: " + messageBody.message
          }
        }
      };
    case ACTION_TYPES.RECEIVED_PORTFOLIO_MONITOR_MESSAGE:
      var messageBody = JSON.parse(action.response.body).data;
      return {
        ...state,
        portfolio: {
          value: messageBody.Portfolio,
          buyingPower: messageBody.BuyingPower,
          withdrawableCash: messageBody.Cash
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
        daily: {
          netIncome: messageBody.TotalProfit,
          averageProfit: messageBody.AverageProfit,
          averageProfitRate: messageBody.AverageProfitRatio
        }
      };
    case ACTION_TYPES.RETRIEVED_STRATEGY_METADATA:
      let strategyMetadata = action.response.data;

      let indicatorTypes = strategyMetadata.indicatorTypes;
      let indicatorTypesSelectOptions = [];

      let ruleTypes = strategyMetadata.ruleTypes;
      let ruleTypeSelectOptions = _.map(ruleTypes, ruleType => {
        return { value: ruleType.args, label: ruleType.name };
      });

      let categoryIndicatorMap = strategyMetadata.categoryIndicatorMap;
      let indicatorSelectOptions = {};
      for (var property in categoryIndicatorMap) {
        if (categoryIndicatorMap.hasOwnProperty(property)) {
          let indicatorSelectOptionsForProp = [];
          for (var categoryIndicator of categoryIndicatorMap[property]) {
            let indicatorWithTypes = indicatorTypes.find(
              el => el.name == categoryIndicator
            );
            let indicatorWithEachType = _.map(indicatorWithTypes.args, args => {
              return {
                value: { label: categoryIndicator, args: args },
                label: categoryIndicator + "(" + args + ")"
              };
            });
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

    default:
      return state;
  }
};

export default global;
