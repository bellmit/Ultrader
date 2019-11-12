import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Orders/OrdersActions";

import HistoryOrdersComp from "views/Orders/HistoryOrders";

class HistoryOrders extends Component {
  render() {
    return (
      <HistoryOrdersComp
        historyOrders={this.props.historyOrders}
        onGetHistoryOrdersSuccess={this.props.onGetHistoryOrdersSuccess}

        {...this.props}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    historyOrders: state.orders.historyOrders
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onGetHistoryOrdersSuccess: response =>
      dispatch({
        type: ACTION_TYPES.GET_HISTORY_ORDERS_SUCCESS,
        response: response
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(HistoryOrders);
