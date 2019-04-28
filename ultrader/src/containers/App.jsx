import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "../actions/AppActions";
import * as RULES_ACTION_TYPES from "actions/Rules/RulesActions";
import * as STRATEGIES_ACTION_TYPES from "actions/Strategies/StrategiesActions";

import AppComp from "../views/App";

class App extends Component {
  render() {
    return (
      <AppComp
        onConnectedToMonitor={this.props.onConnectedToMonitor}
        onReceivedMonitorMessage={this.props.onReceivedMonitorMessage}
        onRetrievedRuleTypes={this.props.onRetrievedRuleTypes}
        onRetrievedIndicatorTypes={this.props.onRetrievedIndicatorTypes}
        onRetrievedIndicatorCategories={
          this.props.onRetrievedIndicatorCategories
        }
        onRetrievedCategoryIndicatorMap={
          this.props.onRetrievedCategoryIndicatorMap
        }
        monitorMessages={this.props.monitorMessages}
        ruleTypes={this.props.ruleTypes}
        ruleTypeSelectOptions={this.props.ruleTypeSelectOptions}
        indicatorTypes={this.props.indicatorTypes}
        indicatorCategories={this.props.indicatorCategories}
        categoryIndicatorMap={this.props.categoryIndicatorMap}
        socket={this.props.socket}
        stompClient={this.props.stompClient}


        onGetStrategiesSuccess={this.props.onGetStrategiesSuccess}
        onGetRulesSuccess={this.props.onGetRulesSuccess}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    monitorMessages: state.global.monitorMessages,
    ruleTypes: state.global.ruleTypes,
    ruleTypeSelectOptions: state.global.ruleTypeSelectOptions,
    indicatorTypes: state.global.indicatorTypes,
    indicatorCategories: state.global.indicatorCategories,
    categoryIndicatorMap: state.global.categoryIndicatorMap,

    socket: state.global.socket,
    stompClient: state.global.stompClient
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onConnectedToMonitor: (socket, stompClient) =>
      dispatch({
        type: ACTION_TYPES.CONNECTED_TO_MONITOR,
        socket: socket,
        stompClient: stompClient
      }),
    onReceivedMonitorMessage: monitorMessage =>
      dispatch({
        type: ACTION_TYPES.RECEIVED_MONITOR_MESSAGE,
        monitorMessage: monitorMessage
      }),
    onRetrievedRuleTypes: response =>
      dispatch({
        type: ACTION_TYPES.RETRIEVED_RULE_TYPES,
        response: response
      }),
    onRetrievedIndicatorTypes: response =>
      dispatch({
        type: ACTION_TYPES.RETRIEVED_INDICATOR_TYPES,
        response: response
      }),
    onRetrievedIndicatorCategories: response =>
      dispatch({
        type: ACTION_TYPES.RETRIEVED_INDICATOR_CATEGORIES,
        response: response
      }),
    onRetrievedCategoryIndicatorMap: response =>
      dispatch({
        type: ACTION_TYPES.RETRIEVED_CATEGORY_INDICATOR_MAP,
        response: response
      }),

      onGetStrategiesSuccess: (response) =>
        dispatch({
          type: STRATEGIES_ACTION_TYPES.GET_STRATEGIES_SUCCESS,
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
)(App);
