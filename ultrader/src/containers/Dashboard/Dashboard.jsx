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
        daily={this.props.daily}
        positions={this.props.positions}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
      portfolio: state.global.portfolio,
      trades: state.global.trades,
      daily: state.global.daily,
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
