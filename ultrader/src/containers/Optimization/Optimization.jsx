import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Optimization/OptimizationActions";

import OptimizationComp from "views/Optimization/Optimization";

class Optimization extends Component {
  render() {
    return (
      <OptimizationComp
        optimization={this.props.optimization}
        progress={this.props.progress}
        onOptimizationSuccess={this.props.onOptimizationSuccess}
        onOptimizationStarted={this.props.onOptimizationStarted}

        {...this.props}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    optimization: state.optimization.optimization,
    progress: state.optimization.progress
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
