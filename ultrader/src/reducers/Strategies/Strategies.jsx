
import * as ACTION_TYPES from 'actions/Strategies/StrategiesActions';

const initialState = {
    strategies:[]
};

const strategies = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.GET_STRATEGIES_SUCCESS:
        let strategies = action.response.data;
        return {
          ...state,
          strategies: strategies
        };
    default:
      return state
  }
}

export default strategies;