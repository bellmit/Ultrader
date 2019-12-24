import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/HistoryMarketData/HistoryMarketDataActions";

import HistoryMarketDataComp from "views/HistoryMarketData/HistoryMarketData";

class HistoryMarketData extends Component {
  render() {
    return (
      <HistoryMarketDataComp
        historyMarketDatas={this.props.historyMarketDatas}
        isDownloading={this.props.isDownloading}
        progress={this.props.progress}

        onGetHistoryMarketDataSuccess={this.props.onGetHistoryMarketDataSuccess}
        onDeleteHistoryMarketDataSuccess={this.props.onDeleteHistoryMarketDataSuccess}
        onEditHistoryMarketDataSuccess={this.props.onEditHistoryMarketDataSuccess}

        {...this.props}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    historyMarketDatas: state.historyMarketData.historyMarketDatas,
    isDownloading: state.historyMarketData.isDownloading,
    progress: state.historyMarketData.progress,
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onGetHistoryMarketDataSuccess: response =>
      dispatch({
        type: ACTION_TYPES.GET_HISTORY_MARKET_DATA_SUCCESS,
        response: response
      }),
    onEditHistoryMarketDataSuccess: (response,index) =>
      dispatch({
        type: ACTION_TYPES.EDIT_HISTORY_MARKET_DATA_SUCCESS,
        response: response,
        index: index
      }),
    onDeleteHistoryMarketDataSuccess: index =>
      dispatch({
        type: ACTION_TYPES.DELETE_HISTORY_MARKET_DATA_SUCCESS,
        index: index
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(HistoryMarketData);
