import * as ACTION_TYPES from "actions/Backtest/BacktestActions";

const initialState = {
  results: [],
  progress: {
    status: "Not Started",
    message: "Not Started",
    progress: 0
  }
};

const backtest = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.BACKTEST_SUCCESS:
      let results = action.response.data;
      return {
        ...state,
        results: results
      };
    case ACTION_TYPES.RECEIVED_BACKTEST_PROGRESS_MESSAGE:
      var messageBody = JSON.parse(action.response.body);
      return {
        ...state,
        progress: {
          status: messageBody.status,
          message: messageBody.message,
          progress: messageBody.progress
        }
      };
    case ACTION_TYPES.BACKTEST_STARTED:
      return {
        ...state,
        progress: {
          status: "Not Started",
          message: "Not Started",
          progress: 0
        }
      };
    default:
      return state;
  }
};

export default backtest;
