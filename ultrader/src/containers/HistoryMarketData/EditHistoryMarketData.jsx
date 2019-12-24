import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/HistoryMarketData/HistoryMarketDataActions";

import EditHistoryMarketDataComp from "views/HistoryMarketData/EditHistoryMarketData";

class EditHistoryMarketData extends Component {
  render() {
    return (
      <EditHistoryMarketDataComp
        historyMarketData={this.props.historyMarketData}

        onEditHistoryMarketDataSuccess={this.props.onEditHistoryMarketDataSuccess}

        {...this.props}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onEditHistoryMarketDataSuccess: (response,index) =>
      dispatch({
        type: ACTION_TYPES.EDIT_HISTORY_MARKET_DATA_SUCCESS,
        response: response,
        index: index
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EditHistoryMarketData);
