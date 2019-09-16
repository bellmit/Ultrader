import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Backtest/BacktestActions";

import BacktestComp from "views/Backtest/Backtest";

class Backtest extends Component {
  render() {
    return (
      <BacktestComp
        results={this.props.results}
        progress={this.props.progress}
        onBacktestSuccess={this.props.onBacktestSuccess}
        onBacktestStarted={this.props.onBacktestStarted}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    results: state.backtest.results,
    progress: state.backtest.progress
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onBacktestSuccess: response =>
      dispatch({
        type: ACTION_TYPES.BACKTEST_SUCCESS,
        response: response
      }),
    onBacktestStarted: response =>
      dispatch({
        type: ACTION_TYPES.BACKTEST_STARTED,
        response: response
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Backtest);
