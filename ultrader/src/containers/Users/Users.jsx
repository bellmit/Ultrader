import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Users/UsersActions";

import UsersComp from "views/Users/Users";

class Users extends Component {
  render() {
    return (
      <UsersComp
        users={this.props.users}
        onGetUsersSuccess={this.props.onGetUsersSuccess}
        onDeleteUserSuccess={this.props.onDeleteUserSuccess}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    users: state.users.users
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onGetUsersSuccess: response =>
      dispatch({
        type: ACTION_TYPES.GET_USERS_SUCCESS,
        response: response
      }),

    onDeleteUserSuccess: index =>
      dispatch({
        type: ACTION_TYPES.DELETE_USER_SUCCESS,
        index: index
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Users);
