import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Optimization/OptimizationActions";

import OptimizationComp from "views/Optimization/Optimization";

class Optimization extends Component {
  render() {
    return (
      <OptimizationComp
        results={this.props.results}
        progress={this.props.progress}
        iteration={this.props.iteration}
        parameters={this.props.parameters}
        bestResults={this.props.bestResults}
        bestParameters={this.props.bestParameters}
        onOptimizationSuccess={this.props.onOptimizationSuccess}
        onOptimizationStarted={this.props.onOptimizationStarted}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    results: state.optimization.results,
    progress: state.optimization.progress,
    iteration: state.optimization.iteration,
    parameters: state.optimization.parameters,
    bestResults: state.optimization.bestResults,
    bestParameters: state.optimization.bestParameters
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onOptimizationSuccess: response =>
      dispatch({
        type: ACTION_TYPES.OPTIMIZATION_SUCCESS,
        response: response
      }),
    onOptimizationStarted: response =>
      dispatch({
        type: ACTION_TYPES.OPTIMIZATION_STARTED,
        response: response
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Optimization);
