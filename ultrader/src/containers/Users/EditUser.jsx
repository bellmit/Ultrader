import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Users/UsersActions";

import EditUserComp from "views/Users/EditUser";

class EditUser extends Component {
  render() {
    return (
      <EditUserComp
        editUser={this.props.editUser}
        onEditUserSuccess={this.props.onEditUserSuccess}

        {...this.props}
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
    onEditUserSuccess: (response,index) =>
      dispatch({
        type: ACTION_TYPES.EDIT_USER_SUCCESS,
        response: response,
        index: index
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EditUser);
