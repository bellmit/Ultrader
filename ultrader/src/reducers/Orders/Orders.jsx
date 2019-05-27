import * as ACTION_TYPES from "actions/Orders/OrdersActions";

const initialState = {
  pendingOrders: [],
  historyOrders: []
};

const orders = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.GET_PENDING_ORDERS_SUCCESS:
      let pendingOrders = action.response.data;
      return {
        ...state,
        pendingOrders: pendingOrders
      };
    case ACTION_TYPES.GET_HISTORY_ORDERS_SUCCESS:
      let historyOrders = action.response.data;
      return {
        ...state,
        historyOrders: historyOrders
      };
    default:
      return state;
  }
};

export default orders;
