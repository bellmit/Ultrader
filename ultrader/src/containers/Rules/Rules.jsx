import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Rules/RulesActions";

import RulesComp from "views/Rules/Rules";

class Rules extends Component {
  render() {
    return (
      <RulesComp
        rules={this.props.rules}

        onGetRulesSuccess={this.props.onGetRulesSuccess}
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
      onGetRulesSuccess: (response) =>
        dispatch({
          type: ACTION_TYPES.GET_RULES_SUCCESS,
          response: response
        })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Rules);
