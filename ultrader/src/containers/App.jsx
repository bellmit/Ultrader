import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "../actions/AppActions";

import AppComp from "../views/App";

class App extends Component {
  render() {
    return (
      <AppComp
        onConnectedToMonitor={this.props.onConnectedToMonitor}
        onReceivedMonitorMessage={this.props.onReceivedMonitorMessage}

        monitorMessages={this.props.monitorMessages}
        socket={this.props.socket}
        stompClient={this.props.stompClient}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    monitorMessages: state.global.monitorMessages,

          socket: state.global.socket,
          stompClient: state.global.stompClient,


  };
};

const mapDispatchToProps = dispatch => {
  return {
    onConnectedToMonitor: (socket, stompClient) => dispatch({
        type:ACTION_TYPES.CONNECTED_TO_MONITOR,
          socket: socket,
          stompClient: stompClient
    }),
       onReceivedMonitorMessage: (monitorMessage) => dispatch({
           type:ACTION_TYPES.RECEIVED_MONITOR_MESSAGE,
             monitorMessage: monitorMessage
       })

  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(App);
