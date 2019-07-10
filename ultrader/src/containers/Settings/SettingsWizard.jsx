import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Settings/SettingsWizardActions";

import SettingsWizardComp from "views/Settings/Wizard/SettingsWizard";

class SettingsWizard extends Component {
  render() {
    return (
      <SettingsWizardComp
        settings={this.props.settings}
        strategyTemplates={this.props.strategyTemplates}
        strategyTemplateOptions={this.props.strategyTemplateOptions}
        onAddSetting={this.props.onAddSetting}
        onRetrievedStrategyTemplate={this.props.onRetrievedStrategyTemplate}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    settings: state.settingsWizard.settings,
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
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SettingsWizard);
