import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/HistoryMarketData/HistoryMarketDataActions";

import AddHistoryMarketDataComp from "views/HistoryMarketData/AddHistoryMarketData";

class AddHistoryMarketData extends Component {
  render() {
    return (
      <AddHistoryMarketDataComp
        onAddHistoryMarketDataSuccess={this.props.onAddHistoryMarketDataSuccess}

        {...this.props}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    historyMarketDataTypes: state.global.historyMarketDataTypes,
    historyMarketDataTypeSelectOptions: state.global.historyMarketDataTypeSelectOptions,
    indicatorTypes: state.global.indicatorTypes,
    indicatorTypesSelectOptions: state.global.indicatorTypesSelectOptions,
    indicatorCategories: state.global.indicatorCategories,
    categoryIndicatorMap: state.global.categoryIndicatorMap,
    indicatorSelectOptions: state.global.indicatorSelectOptions
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onAddHistoryMarketDataSuccess: response =>
      dispatch({
        type: ACTION_TYPES.ADD_HISTORY_MARKET_DATA_SUCCESS,
        response: response
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AddHistoryMarketData);
