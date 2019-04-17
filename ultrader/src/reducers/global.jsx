import * as ACTION_TYPES from "actions/AppActions";

const initialState = {
  stompClient: null,
  socket: null,
  monitorMessages: []
};

const global = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.CONNECTED_TO_MONITOR:
      return {
        ...state,
        stompClient: action.stompClient,
        socket: action.socket
      };
    case ACTION_TYPES.RECEIVED_MONITOR_MESSAGE:
      return {
        ...state,
        monitorMessages: [...state.monitorMessages, action.monitorMessage]
      };
    default:
      return state;
  }
};

export default global;
