import * as ACTION_TYPES from "actions/Backtest/BacktestActions";

const initialState = {
    results: []
};

const backtest = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.BACKTEST_SUCCESS:
      let results = action.response.data;
      return {
        ...state,
        results: results
      };
    default:
      return state;
  }
};

export default backtest;
