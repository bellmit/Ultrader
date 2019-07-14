import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Rules/RulesActions";

import EditRuleComp from "views/Rules/EditRule";

class EditRule extends Component {
  render() {
    return (
      <EditRuleComp
        rule={this.props.rule}

        ruleTypes={this.props.ruleTypes}
        ruleTypeSelectOptions={this.props.ruleTypeSelectOptions}
        indicatorTypes={this.props.indicatorTypes}
        indicatorTypesSelectOptions={this.props.indicatorTypesSelectOptions}
        indicatorCategories={this.props.indicatorCategories}
        categoryIndicatorMap={this.props.categoryIndicatorMap}
        indicatorSelectOptions={this.props.indicatorSelectOptions}
        onEditRuleSuccess={this.props.onEditRuleSuccess}
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
    onEditRuleSuccess: (response,index) =>
      dispatch({
        type: ACTION_TYPES.EDIT_RULE_SUCCESS,
        response: response,
        index: index
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EditRule);
