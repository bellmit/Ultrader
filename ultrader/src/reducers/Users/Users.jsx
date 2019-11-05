import * as ACTION_TYPES from "actions/Users/UsersActions";

const initialState = {
  users: []
};

const users = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.ADD_USER_SUCCESS:
      var rule = action.response.data;
      return {
        ...state,
        users: [...state.users, rule]
      };
    case ACTION_TYPES.EDIT_USER_SUCCESS:
      var rule = action.response.data;
      var users = [...state.users];
      var index = users.map(function(e) { return e.id; }).indexOf(rule.id);
      users[index] = rule;
      return {
        ...state,
        users: users
      };
    case ACTION_TYPES.DELETE_USER_SUCCESS:
      var index = action.index;
      var users = [...state.users];
      users.splice(index, 1);
      return {
        ...state,
        users: users
      };
    case ACTION_TYPES.GET_USERS_SUCCESS:
      var users = action.response.data;
      return {
        ...state,
        users: users
      };
    default:
      return state;
  }
};

export default users;
