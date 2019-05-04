import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Authentication/AuthenticationActions";

import RegisterPageComp from "views/Pages/RegisterPage";

class RegisterPage extends Component {
  render() {
    return (
      <RegisterPageComp
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
)(RegisterPage);
