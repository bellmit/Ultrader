import _ from "lodash";

import * as ACTION_TYPES from "actions/AppActions";

const initialState = {
  stompClient: null,
  socket: null,
  monitorMessages: [],
  ruleTypes: [],
  ruleTypeSelectOptions: [],
  indicatorTypes: [],
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
    case ACTION_TYPES.RETRIEVED_RULE_TYPES:
      let ruleTypes = action.response.data;
      let ruleTypeSelectOptions = _.map(ruleTypes, ruleType => {
        return { value: ruleType.args, label: ruleType.name };
      });
      return {
        ...state,
        ruleTypes: ruleTypes,
        ruleTypeSelectOptions: ruleTypeSelectOptions
      };
    case ACTION_TYPES.RETRIEVED_INDICATOR_TYPES:
      let indicatorTypes = action.response.data;
      return {
        ...state,
        indicatorTypes: indicatorTypes
      };
    case ACTION_TYPES.RETRIEVED_INDICATOR_CATEGORIES:
      let indicatorCategories = action.response.data;
      return {
        ...state,
        indicatorCategories: indicatorCategories
      };

    case ACTION_TYPES.RETRIEVED_CATEGORY_INDICATOR_MAP:
      let categoryIndicatorMap = action.response.data;
      let indicatorSelectOptions = {};
      for (var property in categoryIndicatorMap) {
        if (categoryIndicatorMap.hasOwnProperty(property)) {
          indicatorSelectOptions[property] = _.map(
            categoryIndicatorMap[property],
            option => {
              return { value: option, label: option };
            }
          );
        }
      }
      console.log(indicatorSelectOptions);

      return {
        ...state,
        categoryIndicatorMap: categoryIndicatorMap,
        indicatorSelectOptions: indicatorSelectOptions
      };
    default:
      return state;
  }
};

export default global;
