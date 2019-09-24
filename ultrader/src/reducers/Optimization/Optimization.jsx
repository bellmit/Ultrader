import * as ACTION_TYPES from "actions/Optimization/OptimizationActions";

const initialState = {
  optimization: {
     iteration: 0,
     parameterNames: [],
     bestParameters: {},
     results: []
  },

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
      let parameters = optimizedResults.parameters;
      let table = [];
      let max = -99999999;
      let bestParameters;
      for (var i in results) {
         var row = {};
         row.iteration = parseInt(i) + 1 ;
         row.parameters = parameters[i].join('|');
         row.optimizationGoal = optimizedResults.optimizationGoal[i];
         row.backtest = optimizedResults.results[i];
         if (row.optimizationGoal > max) {
            max = row.optimizationGoal;
            bestParameters = row;
         }
         table.push(row);
      }
      let bestTable = [];
      for (var i in optimizedResults.parameterNames) {
        let row = {};
        let attr = optimizedResults.parameterNames[i].split(",");
        row.strategyName = attr[0].substring(1, attr[0].length);
        row.ruleName = attr[1];
        row.parameterType = attr[2].substring(0, attr[2].length-1);
        row.value = bestParameters.parameters.split("|")[i];
        bestTable.push(row);
      }
      return {
        ...state,
        optimization: {
            iteration: iteration,
            parameterNames: optimizedResults.parameterNames.join('|'),
            bestParameters: bestTable,
            results: table
        }
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
