import * as ACTION_TYPES from "actions/Rules/RulesActions";

const initialState = {
  rules: []
};

const rules = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.ADD_RULE_SUCCESS:
      var rule = action.response.data;
      return {
        ...state,
        rules: [...state.rules, rule]
      };
    case ACTION_TYPES.EDIT_RULE_SUCCESS:
      var rule = action.response.data;
      var rules = [...state.rules];
      var index = rules.map(function(e) { return e.id; }).indexOf(rule.id);
      rules[index] = rule;
      return {
        ...state,
        rules: rules
      };
    case ACTION_TYPES.DELETE_RULE_SUCCESS:
      var index = action.index;
      var rules = [...state.rules];
      rules.splice(index, 1);
      return {
        ...state,
        rules: rules
      };
    case ACTION_TYPES.GET_RULES_SUCCESS:
      var rules = action.response.data;
      return {
        ...state,
        rules: rules
      };

    default:
      return state;
  }
};

export default rules;
