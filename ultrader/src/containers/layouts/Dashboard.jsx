import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/layouts/DashboardActions";
import * as RULES_ACTION_TYPES from "actions/Rules/RulesActions";
import * as STRATEGIES_ACTION_TYPES from "actions/Strategies/StrategiesActions";
import * as BACKTEST_ACTION_TYPES from "actions/Backtest/BacktestActions";
import * as OPTIMIZATION_TYPES from "actions/Optimization/OptimizationActions";
import * as HISTORY_MARKET_DATA_ACTION_TYPES from "actions/HistoryMarketData/HistoryMarketDataActions";

import DashboardComp from "layouts/Dashboard/Dashboard";

class Dashboard extends Component {
  render() {
    return (
      <DashboardComp
        user={this.props.user}
        systemStatus={this.props.systemStatus}
        monitorMessages={this.props.monitorMessages}
        ruleTypes={this.props.ruleTypes}
        ruleTypeSelectOptions={this.props.ruleTypeSelectOptions}
        indicatorTypes={this.props.indicatorTypes}
        indicatorCategories={this.props.indicatorCategories}
        categoryIndicatorMap={this.props.categoryIndicatorMap}
        strategyTemplates={this.props.strategyTemplates}
        strategyTemplateOptions={this.props.strategyTemplateOptions}
        socket={this.props.socket}
        stompClient={this.props.stompClient}
        notifications={this.props.notifications}
        onConnectedToMonitor={this.props.onConnectedToMonitor}
        onReceivedDataStatusMessage={this.props.onReceivedDataStatusMessage}
        onReceivedMarketStatusMessage={this.props.onReceivedMarketStatusMessage}
        onReceivedBotStatusMessage={this.props.onReceivedBotStatusMessage}
        onReceivedPortfolioMonitorMessage={
          this.props.onReceivedPortfolioMonitorMessage
        }
        onReceivedTradesMonitorMessage={
          this.props.onReceivedTradesMonitorMessage
        }
        onReceivedProfitMonitorMessage={
          this.props.onReceivedProfitMonitorMessage
        }
        onReceivedPositionMonitorMessage={
          this.props.onReceivedPositionMonitorMessage
        }
        onReceivedNotificationMessage={this.props.onReceivedNotificationMessage}
        onReceivedBacktestProgressMessage={
          this.props.onReceivedBacktestProgressMessage
        }
        onReceivedOptimizationProgressMessage={
          this.props.onReceivedOptimizationProgressMessage
        }
        onReceivedDownloadProgressMessage={
          this.props.onReceivedDownloadProgressMessage
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
        onRetrievedStrategyTemplate={this.props.onRetrievedStrategyTemplate}
        onGetStrategiesSuccess={this.props.onGetStrategiesSuccess}
        onGetRulesSuccess={this.props.onGetRulesSuccess}
        onReceivedDashboardNotifications={
          this.props.onReceivedDashboardNotifications
        }
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    user: state.authentication.user,
    systemStatus: state.global.systemStatus,
    monitorMessages: state.global.monitorMessages,
    ruleTypes: state.global.ruleTypes,
    ruleTypeSelectOptions: state.global.ruleTypeSelectOptions,
    indicatorTypes: state.global.indicatorTypes,
    indicatorCategories: state.global.indicatorCategories,
    categoryIndicatorMap: state.global.categoryIndicatorMap,
    strategyTemplateOptions: state.global.strategyTemplateOptions,
    strategyTemplates: state.global.strategyTemplates,
    socket: state.global.socket,
    stompClient: state.global.stompClient,

    notifications: state.global.notifications
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
    onReceivedBotStatusMessage: response =>
      dispatch({
        type: ACTION_TYPES.RECEIVED_BOT_STATUS_MESSAGE,
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
    onReceivedPositionMonitorMessage: response =>
      dispatch({
        type: ACTION_TYPES.RECEIVED_POSITION_MONITOR_MESSAGE,
        response: response
      }),
    onReceivedNotificationMessage: response =>
      dispatch({
        type: ACTION_TYPES.RETRIEVED_NOTIFICATION_MESSAGE,
        response: response
      }),

    // ---------------------- metadata ----------------------
    onRetrievedStrategyMetadata: response =>
      dispatch({
        type: ACTION_TYPES.RETRIEVED_STRATEGY_METADATA,
        response: response
      }),
    onRetrievedStrategyTemplate: response =>
      dispatch({
        type: ACTION_TYPES.RETRIEVED_STRATEGY_TEMPLATE,
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
      }),

    // ---------------------- progress ----------------------
    onReceivedBacktestProgressMessage: response =>
      dispatch({
        type: BACKTEST_ACTION_TYPES.RECEIVED_BACKTEST_PROGRESS_MESSAGE,
        response: response
      }),
    onReceivedOptimizationProgressMessage: response =>
      dispatch({
        type: OPTIMIZATION_TYPES.RECEIVED_OPTIMIZATION_PROGRESS_MESSAGE,
        response: response
      }),
    onReceivedDownloadProgressMessage: response =>
      dispatch({
        type: HISTORY_MARKET_DATA_ACTION_TYPES.RECEIVED_HISTORY_MARKET_DATA_DOWNLOAD_PROGRESS_MESSAGE,
        response: response
      }),
    // ---------------------- initialization ----------------------
    onReceivedDashboardNotifications: response =>
      dispatch({
        type: ACTION_TYPES.RETRIEVED_DASHBOARD_NOTIFICATIONS,
        response: response
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Dashboard);
