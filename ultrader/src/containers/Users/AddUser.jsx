import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Users/UsersActions";

import AddUserComp from "views/Users/AddUser";

class AddUser extends Component {
  render() {
    return (
      <AddUserComp
        onAddUserSuccess={this.props.onAddUserSuccess}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onAddUserSuccess: response =>
      dispatch({
        type: ACTION_TYPES.ADD_USER_SUCCESS,
        response: response
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AddUser);
