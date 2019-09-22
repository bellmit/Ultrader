import * as ACTION_TYPES from "actions/Optimization/OptimizationActions";

const initialState = {
  results: [],
  iteration: 0,
  parameters: [],
  bestResults: [],
  bestParameters: [],
  progress: {
    status: "Not Started",
    message: "Not Started",
    progress: 0
  }
};

const optimization = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.OPTIMIZATION_SUCCESS:
      let optimizedResults = action.response.data;
      let iteration = optimizedResults.iterationNum;
      let results =  optimizedResults.results;
      let bestResults =  results && results[results.length-1] ? results[results.length-1]:[];
      let parameters = optimizedResults.parameters;
      let bestParameters =  parameters && parameters[parameters.length-1] ? parameters[parameters.length-1]:[];
      return {
        ...state,
        iteration: iteration,
        parameters: parameters,
        results: results,
        bestResults: bestResults,
        bestParameters: bestParameters
      };
    case ACTION_TYPES.RECEIVED_OPTIMIZATION_PROGRESS_MESSAGE:
      var messageBody = JSON.parse(action.response.body);
      return {
        ...state,
        progress: {
          status: messageBody.status,
          message: messageBody.message,
          progress: messageBody.progress
        }
      };
    case ACTION_TYPES.OPTIMIZATION_STARTED:
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

export default optimization;
