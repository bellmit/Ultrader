import * as ACTION_TYPES from 'actions/Rules/RulesActions';

const initialState = {
    rules:[]
};

const addStrategy = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.GET_RULES_SUCCESS:
        let rules = action.response.data;
        return {
          ...state,
          rules: rules
        };
    default:
      return state
  }
}

export default addStrategy;