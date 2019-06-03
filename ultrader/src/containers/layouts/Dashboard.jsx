import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/layouts/DashboardActions";
import * as RULES_ACTION_TYPES from "actions/Rules/RulesActions";
import * as STRATEGIES_ACTION_TYPES from "actions/Strategies/StrategiesActions";

import DashboardComp from "layouts/Dashboard/Dashboard";

class Dashboard extends Component {
  render() {
    return (
      <DashboardComp
        systemStatus={this.props.systemStatus}
        monitorMessages={this.props.monitorMessages}
        ruleTypes={this.props.ruleTypes}
        ruleTypeSelectOptions={this.props.ruleTypeSelectOptions}
        indicatorTypes={this.props.indicatorTypes}
        indicatorCategories={this.props.indicatorCategories}
        categoryIndicatorMap={this.props.categoryIndicatorMap}
        socket={this.props.socket}
        stompClient={this.props.stompClient}
        onConnectedToMonitor={this.props.onConnectedToMonitor}
        onReceivedDataStatusMessage={this.props.onReceivedDataStatusMessage}
        onReceivedMarketStatusMessage={this.props.onReceivedMarketStatusMessage}
        onReceivedPortfolioMonitorMessage={
          this.props.onReceivedPortfolioMonitorMessage
        }
        onReceivedTradesMonitorMessage={
          this.props.onReceivedTradesMonitorMessage
        }
        onReceivedProfitMonitorMessage={
          this.props.onReceivedProfitMonitorMessage
        }
        onRetrievedRuleTypes={this.props.onRetrievedRuleTypes}
        onRetrievedIndicatorTypes={this.props.onRetrievedIndicatorTypes}
        onRetrievedIndicatorCategories={
          this.props.onRetrievedIndicatorCategories
        }
        onRetrievedCategoryIndicatorMap={
          this.props.onRetrievedCategoryIndicatorMap
        }
        onRetrievedStrategyMetadata={this.props.onRetrievedStrategyMetadata}
        onGetStrategiesSuccess={this.props.onGetStrategiesSuccess}
        onGetRulesSuccess={this.props.onGetRulesSuccess}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    systemStatus: state.global.systemStatus,
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

    // ---------------------- monitors ----------------------
    onReceivedDataStatusMessage: response =>
      dispatch({
        type: ACTION_TYPES.RECEIVED_DATA_STATUS_MESSAGE,
        response: response
      }),
    onReceivedMarketStatusMessage: response =>
      dispatch({
        type: ACTION_TYPES.RECEIVED_MARKET_STATUS_MESSAGE,
        response: response
      }),
    onReceivedPortfolioMonitorMessage: response =>
      dispatch({
        type: ACTION_TYPES.RECEIVED_PORTFOLIO_MONITOR_MESSAGE,
        response: response
      }),
    onReceivedTradesMonitorMessage: response =>
      dispatch({
        type: ACTION_TYPES.RECEIVED_TRADES_MONITOR_MESSAGE,
        response: response
      }),
    onReceivedProfitMonitorMessage: response =>
      dispatch({
        type: ACTION_TYPES.RECEIVED_PROFIT_MONITOR_MESSAGE,
        response: response
      }),

    // ---------------------- metadata ----------------------
    onRetrievedStrategyMetadata: response =>
      dispatch({
        type: ACTION_TYPES.RETRIEVED_STRATEGY_METADATA,
        response: response
      }),
    onGetStrategiesSuccess: response =>
      dispatch({
        type: STRATEGIES_ACTION_TYPES.GET_STRATEGIES_SUCCESS,
        response: response
      }),
    onGetRulesSuccess: response =>
      dispatch({
        type: RULES_ACTION_TYPES.GET_RULES_SUCCESS,
        response: response
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Dashboard);
