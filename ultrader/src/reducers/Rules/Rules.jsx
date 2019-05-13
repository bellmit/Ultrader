import * as ACTION_TYPES from "actions/Rules/RulesActions";

const initialState = {
  rules: []
};

const rules = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.ADD_RULE_SUCCESS:
      let rule = action.response.data;
      return {
        ...state,
        rules: [...state.rules, rule]
      };

    case ACTION_TYPES.DELETE_RULE_SUCCESS:
      let index = action.index;
      var rules = [...state.rules];
      rules.splice(index, 1);
      return {
        ...state,
        rules: rules
      };
    case ACTION_TYPES.GET_RULES_SUCCESS:
      let rules = action.response.data;
      return {
        ...state,
        rules: rules
      };

    default:
      return state;
  }
};

export default rules;
