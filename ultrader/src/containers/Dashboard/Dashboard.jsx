import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Dashboard/DashboardActions";

import DashboardComp from "views/Dashboard/Dashboard";

class Dashboard extends Component {
  render() {
    return (
      <DashboardComp
        portfolio={this.props.portfolio}
        trades={this.props.trades}
        profit={this.props.profit}
        positions={this.props.positions}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
      portfolio: state.global.portfolio,
      trades: state.global.trades,
      profit: state.global.profit,
      positions: state.global.positions
  };
};

const mapDispatchToProps = dispatch => {
  return {
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Dashboard);
