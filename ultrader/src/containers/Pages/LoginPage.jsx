import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Authentication/AuthenticationActions";

import LoginPageComp from "views/Pages/LoginPage";

class LoginPage extends Component {
  render() {
    return (
      <LoginPageComp
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
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginPage);
