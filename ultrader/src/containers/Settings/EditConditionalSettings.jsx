import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Settings/SettingsActions";

import EditConditionalSettingsComp from "views/Settings/EditConditionalSettings/EditConditionalSettings";

class EditConditionalSettings extends Component {
  render() {
    return (
      <EditConditionalSettingsComp

        conditionalSettings={this.props.conditionalSettings}
        strategies={this.props.strategies}
        strategyTemplates={this.props.strategyTemplates}
        strategyTemplateOptions={this.props.strategyTemplateOptions}
        onAddConditionalSetting={this.props.onAddConditionalSetting}
        onGetConditionalSettingsSuccess={this.props.onGetConditionalSettingsSuccess}
        onRetrievedStrategyTemplate={this.props.onRetrievedStrategyTemplate}

        {...this.props}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    conditionalSettings: state.settings.conditionalSettings,
    strategies: state.strategies.strategies,
    strategyTemplateOptions: state.global.strategyTemplateOptions,
    strategyTemplates: state.global.strategyTemplates
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onAddConditionalSetting: (trend, key, value) =>
      dispatch({
        type: ACTION_TYPES.ADD_CONDITIONAL_SETTING,
        trend: trend,
        key: key,
        value: value
      }),
      onGetConditionalSettingsSuccess: response =>
            dispatch({
              type: ACTION_TYPES.GET_CONDITIONAL_SETTINGS_SUCCESS,
              response: response
            })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EditConditionalSettings);
