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
  categoryIndicatorMap: {}
};

const global = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.CONNECTED_TO_MONITOR:
      return {
        ...state,
        stompClient: action.stompClient,
        socket: action.socket
      };
    case ACTION_TYPES.RECEIVED_MONITOR_MESSAGE:
      return {
        ...state,
        monitorMessages: [...state.monitorMessages, action.monitorMessage]
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
          /*indicatorSelectOptions[property] = _.map(
            categoryIndicatorMap[property],
            option => {
              let value =
              return { value: option, label: option };
            }
          );*/
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
