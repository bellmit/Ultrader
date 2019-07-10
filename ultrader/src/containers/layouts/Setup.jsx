import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/layouts/SetupActions";

import SetupComp from "layouts/Setup/Setup";

class Setup extends Component {
  render() {
    return (
      <SetupComp
        strategyTemplates={this.props.strategyTemplates}
        strategyTemplateOptions={this.props.strategyTemplateOptions}

        onRetrievedStrategyTemplate={this.props.onRetrievedStrategyTemplate}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    strategyTemplateOptions: state.global.strategyTemplateOptions,
    strategyTemplates: state.global.strategyTemplates
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onRetrievedStrategyTemplate: response =>
      dispatch({
        type: ACTION_TYPES.RETRIEVED_STRATEGY_TEMPLATE,
        response: response
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Setup);
