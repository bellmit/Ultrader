import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Backtest/BacktestActions";

import BacktestComp from "views/Backtest/Backtest";

class Backtest extends Component {
  render() {
    return (
      <BacktestComp
        results={this.props.results}
        onBacktestSuccess={this.props.onBacktestSuccess}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
      results: state.backtest.results
  };
};

const mapDispatchToProps = dispatch => {
  return {
      onBacktestSuccess: response =>
            dispatch({
              type: ACTION_TYPES.BACKTEST_SUCCESS,
              response: response
            })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Backtest);
