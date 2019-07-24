import * as ACTION_TYPES from "actions/Positions/PositionsActions";

const initialState = {
  positions: []
};

function getProfit(position) {
  var priceDiff = position.currentPrice - position.averageCost;
  var profit = (priceDiff * position.quantity).toFixed(2);
  return profit;
}

function getProfitPercent(position) {
  var priceDiff = position.currentPrice - position.averageCost;
  var percent = priceDiff / position.averageCost;
  return percent;
}

function getHoldDays(position) {
  var today = new Date();
  var holdDatetime = today - new Date(position.buyDate);
  var aDay = 1000 * 60 * 60 * 24;
  var holdDays = Math.floor(holdDatetime / aDay);
  return holdDays;
}

const positions = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.GET_POSITIONS_SUCCESS:
      let positions = action.response.data;
      positions = positions.map(position => {
        position.profit = getProfit(position);
        position.profitPercent = getProfitPercent(position);
        position.holdDays = getHoldDays(position);
        return position;
      });
      return {
        ...state,
        positions: positions
      };
    default:
      return state;
  }
};

export default positions;
