import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Orders/OrdersActions";

import PendingOrdersComp from "views/Orders/PendingOrders";

class PendingOrders extends Component {
  render() {
    return (
      <PendingOrdersComp
        pendingOrders={this.props.pendingOrders}
        onGetPendingOrdersSuccess={this.props.onGetPendingOrdersSuccess}

        {...this.props}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    pendingOrders: state.orders.pendingOrders
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onGetPendingOrdersSuccess: response =>
      dispatch({
        type: ACTION_TYPES.GET_PENDING_ORDERS_SUCCESS,
        response: response
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(PendingOrders);
