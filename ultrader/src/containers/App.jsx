import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "../actions/AppActions";
import * as RULES_ACTION_TYPES from "actions/Rules/RulesActions";
import * as STRATEGIES_ACTION_TYPES from "actions/Strategies/StrategiesActions";

import AppComp from "../views/App";

class App extends Component {
  render() {
    return (
      <AppComp
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
)(App);
