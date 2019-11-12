import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Authentication/AuthenticationActions";

import LoginPageComp from "views/Pages/LoginPage";

class LoginPage extends Component {
  render() {
    return (
      <LoginPageComp
        onLoginSuccess={this.props.onLoginSuccess}

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
      onLoginSuccess: (user) =>
        dispatch({
          type: ACTION_TYPES.LOGIN_SUCCESS,
          user: user
        })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginPage);
