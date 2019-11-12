import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Settings/SettingsActions";

import EditSettingsComp from "views/Settings/EditSettings/EditSettings";

class EditSettings extends Component {
  render() {
    return (
      <EditSettingsComp
        settings={this.props.settings}
        strategies={this.props.strategies}
        strategyTemplates={this.props.strategyTemplates}
        strategyTemplateOptions={this.props.strategyTemplateOptions}
        onAddSetting={this.props.onAddSetting}
        onGetSettingsSuccess={this.props.onGetSettingsSuccess}
        onRetrievedStrategyTemplate={this.props.onRetrievedStrategyTemplate}

        {...this.props}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    settings: state.settings.settings,
    strategies: state.strategies.strategies,
    strategyTemplateOptions: state.global.strategyTemplateOptions,
    strategyTemplates: state.global.strategyTemplates
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onAddSetting: (key, value) =>
      dispatch({
        type: ACTION_TYPES.ADD_SETTING,
        key: key,
        value: value
      }),
      onGetSettingsSuccess: response =>
            dispatch({
              type: ACTION_TYPES.GET_SETTINGS_SUCCESS,
              response: response
            })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EditSettings);
