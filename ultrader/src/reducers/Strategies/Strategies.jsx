import * as ACTION_TYPES from "actions/Strategies/StrategiesActions";

const initialState = {
  strategies: []
};

const strategies = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.ADD_STRATEGY_SUCCESS:
      let strategy = action.response.data;
      return {
        ...state,
        strategies: [...state.strategies, strategy]
      };
    case ACTION_TYPES.GET_STRATEGIES_SUCCESS:
      let strategies = action.response.data;
      return {
        ...state,
        strategies: strategies
      };

    case ACTION_TYPES.DELETE_STRATEGY_SUCCESS:
      let index = action.index;
      var strategies = [...state.strategies];
      strategies.splice(index, 1);
      return {
        ...state,
        strategies: strategies
      };
    default:
      return state;
  }
};

export default strategies;
