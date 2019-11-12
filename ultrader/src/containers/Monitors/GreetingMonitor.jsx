import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Monitors/GreetingMonitorActions";

import GreetingMonitorComp from "views/Monitors/GreetingMonitor";

class GreetingMonitor extends Component {
  render() {
    return (
      <GreetingMonitorComp
        monitorMessages={this.props.monitorMessages}

        {...this.props}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    monitorMessages: state.global.monitorMessages
  };
};

const mapDispatchToProps = dispatch => {
  return {
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(GreetingMonitor);
