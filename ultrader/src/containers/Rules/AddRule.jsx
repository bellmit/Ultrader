import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Rules/RulesActions";

import AddRuleComp from "views/Rules/AddRule";

class AddRule extends Component {
  render() {
    return (
      <AddRuleComp
        ruleTypes={this.props.ruleTypes}
        ruleTypeSelectOptions={this.props.ruleTypeSelectOptions}
        indicatorTypes={this.props.indicatorTypes}
        indicatorTypesSelectOptions={this.props.indicatorTypesSelectOptions}
        indicatorCategories={this.props.indicatorCategories}
        categoryIndicatorMap={this.props.categoryIndicatorMap}
        indicatorSelectOptions={this.props.indicatorSelectOptions}
        onAddRuleSuccess={this.props.onAddRuleSuccess}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    ruleTypes: state.global.ruleTypes,
    ruleTypeSelectOptions: state.global.ruleTypeSelectOptions,
    indicatorTypes: state.global.indicatorTypes,
    indicatorTypesSelectOptions: state.global.indicatorTypesSelectOptions,
    indicatorCategories: state.global.indicatorCategories,
    categoryIndicatorMap: state.global.categoryIndicatorMap,
    indicatorSelectOptions: state.global.indicatorSelectOptions
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onAddRuleSuccess: response =>
      dispatch({
        type: ACTION_TYPES.ADD_RULE_SUCCESS,
        response: response
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AddRule);
