
import * as ACTION_TYPES from 'actions/Orders/OrdersActions';

const initialState = {
    pendingOrders:[]
};

const orders = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.GET_PENDING_ORDERS_SUCCESS:
        let pendingOrders = action.response.data;
        return {
          ...state,
          pendingOrders: pendingOrders
        };
    default:
      return state
  }
}

export default orders;