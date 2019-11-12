import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Rules/RulesActions";

import RulesComp from "views/Rules/Rules";

class Rules extends Component {
  render() {
    return (
      <RulesComp
        user={this.props.user}
        rules={this.props.rules}
        onGetRulesSuccess={this.props.onGetRulesSuccess}
        onDeleteRuleSuccess={this.props.onDeleteRuleSuccess}

        {...this.props}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    rules: state.rules.rules,
    user: state.authentication.user
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onGetRulesSuccess: response =>
      dispatch({
        type: ACTION_TYPES.GET_RULES_SUCCESS,
        response: response
      }),

    onDeleteRuleSuccess: index =>
      dispatch({
        type: ACTION_TYPES.DELETE_RULE_SUCCESS,
        index: index
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Rules);
