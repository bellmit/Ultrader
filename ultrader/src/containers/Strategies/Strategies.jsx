import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Strategies/StrategiesActions";
import * as RULES_ACTION_TYPES from "actions/Rules/RulesActions";

import StrategiesComp from "views/Strategies/Strategies";

class Strategies extends Component {
  render() {
    return (
      <StrategiesComp
        strategies={this.props.strategies}
        rules={this.props.rules}

        onGetStrategiesSuccess={this.props.onGetStrategiesSuccess}
        onGetRulesSuccess={this.props.onGetRulesSuccess}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    strategies: state.strategies.strategies,
    rules: state.rules.rules
  };
};

const mapDispatchToProps = dispatch => {
  return {
      onGetStrategiesSuccess: (response) =>
        dispatch({
          type: ACTION_TYPES.GET_STRATEGIES_SUCCESS,
          response: response
        }),
                        onGetRulesSuccess: (response) =>
                          dispatch({
                            type: RULES_ACTION_TYPES.GET_RULES_SUCCESS,
                            response: response
                          })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Strategies);
