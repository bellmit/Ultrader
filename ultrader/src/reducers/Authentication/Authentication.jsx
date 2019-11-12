import * as ACTION_TYPES from "actions/Authentication/AuthenticationActions";

let user = JSON.parse(localStorage.getItem("user"));
const initialState = user ? { loggedIn: true, user } : {};

const authentication = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.LOGIN_SUCCESS:
      return {
        loggedIn: true,
        user: action.user
      };
    case ACTION_TYPES.LOGIN_FAILURE:
      return {};
    case ACTION_TYPES.LOGOUT:
      return {};
    default:
      return state;
  }
};

export default authentication;
