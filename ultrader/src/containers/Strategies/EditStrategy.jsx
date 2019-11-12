import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Strategies/StrategiesActions";
import * as RULES_ACTION_TYPES from "actions/Rules/RulesActions";

import EditStrategyComp from "views/Strategies/EditStrategy";

class EditStrategy extends Component {
  render() {
    return (
      <EditStrategyComp
        strategy={this.props.strategy}
        rules={this.props.rules}
        onGetRulesSuccess={this.props.onGetRulesSuccess}
        onEditStrategySuccess={this.props.onEditStrategySuccess}

        {...this.props}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    rules: state.rules.rules
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onGetRulesSuccess: response =>
      dispatch({
        type: RULES_ACTION_TYPES.GET_RULES_SUCCESS,
        response: response
      }),
    onEditStrategySuccess: response =>
      dispatch({
        type: ACTION_TYPES.EDIT_STRATEGY_SUCCESS,
        response: response
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EditStrategy);
