import * as ACTION_TYPES from "actions/HistoryMarketData/HistoryMarketDataActions";

const initialState = {
  historyMarketDatas: [],
  isDownloading: false,
  progress: {
    status: "Not Started",
    message: "Not Started",
    progress: 0
  }
};

const historyMarketData = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.ADD_HISTORY_MARKET_DATA_SUCCESS:
      var historyMarketData = action.response.data;
      return {
        ...state,
        historyMarketDatas: [...state.historyMarketDatas, historyMarketData]
      };
    case ACTION_TYPES.EDIT_HISTORY_MARKET_DATA_SUCCESS:
      var historyMarketData = action.response.data;
      var historyMarketDatas = [...state.historyMarketDatas];
      var index = historyMarketDatas.map(function(e) { return e.id; }).indexOf(historyMarketData.id);
      historyMarketDatas[index] = historyMarketData;
      return {
        ...state,
        historyMarketDatas: historyMarketDatas
      };
    case ACTION_TYPES.DELETE_HISTORY_MARKET_DATA_SUCCESS:
      var index = action.index;
      var historyMarketDatas = [...state.historyMarketDatas];
      historyMarketDatas.splice(index, 1);
      return {
        ...state,
        historyMarketDatas: historyMarketDatas
      };
    case ACTION_TYPES.GET_HISTORY_MARKET_DATA_SUCCESS:
      var historyMarketDatas = action.response.data;
      return {
        ...state,
        historyMarketDatas: historyMarketDatas
      };
    case ACTION_TYPES.RECEIVED_HISTORY_MARKET_DATA_DOWNLOAD_PROGRESS_MESSAGE:
      var messageBody = JSON.parse(action.response.body);
      var isDownloading = messageBody.progress < 100;
      return {
        ...state,
        isDownloading: isDownloading,
        progress: {
          status: messageBody.status,
          message: messageBody.message,
          progress: messageBody.progress
        }
      };
    default:
      return state;
  }
};

export default historyMarketData;
