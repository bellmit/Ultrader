import React, { Component } from "react";
import { Switch, Route, Redirect } from "react-router-dom";
// this is used to create scrollbars on windows devices like the ones from apple devices
import PerfectScrollbar from "perfect-scrollbar";
import "perfect-scrollbar/css/perfect-scrollbar.css";
// react component that creates notifications (like some alerts with messages)
import NotificationSystem from "react-notification-system";

import Sidebar from "components/Sidebar/Sidebar.jsx";
import Header from "components/Header/Header.jsx";

// dinamically create dashboard routes
import dashboardRoutes from "routes/dashboard.jsx";

// style for notifications
import { style } from "variables/Variables.jsx";

import { withRouter } from "react-router";

import SockJS from "sockjs-client";
import Stomp from "stompjs";
import axios from "axios";

import {
  axiosGetWithAuth,
  axiosPostWithAuth,
  handleResponse,
  getAuthHeader
} from "helpers/UrlHelper";

var ps;

class DashboardComp extends Component {
  constructor(props) {
    super(props);
    this.handleNotification = this.handleNotification.bind(this);
    this.processMarketStatusMessage = this.processMarketStatusMessage.bind(
      this
    );
    this.processDataStatusMessage = this.processDataStatusMessage.bind(this);
    this.processPortfolioMessage = this.processPortfolioMessage.bind(this);
    this.processTradesMessage = this.processTradesMessage.bind(this);
    this.processProfitMessage = this.processProfitMessage.bind(this);

    this.processNotification = this.processNotification.bind(this);

    this.connectToSockets = this.connectToSockets.bind(this);
    this.initMetadata = this.initMetadata.bind(this);
    this.initData = this.initData.bind(this);
    this.checkBotStatus = this.checkBotStatus.bind(this);

    this.state = {
      _notificationSystem: null
    };
  }

  processMarketStatusMessage(message) {
    this.props.onReceivedMarketStatusMessage(message);
  }

  processDataStatusMessage(message) {
    this.props.onReceivedDataStatusMessage(message);
  }

  processPortfolioMessage(message) {
    this.props.onReceivedPortfolioMonitorMessage(message);
  }

  processTradesMessage(message) {
    this.props.onReceivedTradesMonitorMessage(message);
  }

  processProfitMessage(message) {
    this.props.onReceivedProfitMonitorMessage(message);
  }

  processNotification(message) {
    this.handleNotification(message, "success", "tr");
    this.props.onReceivedNotificationMessage(message);
  }

  connectToSockets() {
    let socket = new SockJS("/ws");
    let stompClient = Stomp.over(socket);
    stompClient.debug = () => {};

    let authHeader = getAuthHeader();
    if(!this.props.stompClient||!this.props.socket){
        stompClient.connect(authHeader, frame => {
          this.props.onConnectedToMonitor(socket, stompClient);
          stompClient.subscribe(
            "/topic/status/data",
            this.processDataStatusMessage
          );
          stompClient.subscribe(
            "/topic/status/market",
            this.processMarketStatusMessage
          );
          stompClient.subscribe(
            "/topic/dashboard/account",
            this.processPortfolioMessage
          );
          stompClient.subscribe(
            "/topic/dashboard/trades",
            this.processTradesMessage
          );
          stompClient.subscribe(
            "/topic/dashboard/profit",
            this.processProfitMessage
          );
          stompClient.subscribe("/topic/order", this.processNotification);
          this.initData();
        });
    }

  }

  initData() {
    axiosGetWithAuth("/api/notification/dashboard")
      .then(handleResponse)
      .then(res => {
        this.props.onReceivedDashboardNotifications(res);
      })
      .catch(error => {});
  }

  initMetadata() {
    axiosGetWithAuth("/api/metadata/getStrategyMetadata")
      .then(handleResponse)
      .then(res => {
        this.props.onRetrievedStrategyMetadata(res);
      })
      .catch(error => {});

        axiosGetWithAuth("/api/metadata/getStrategyTemplate")
          .then(res => {
            this.props.onRetrievedStrategyTemplate(res);
          })
          .catch(error => {});

    axiosGetWithAuth("/api/strategy/getStrategies")
      .then(handleResponse)
      .then(res => {
        this.props.onGetStrategiesSuccess(res);
      })
      .catch(error => {
        console.log(error);
      });

    axiosGetWithAuth("/api/rule/getRules")
      .then(handleResponse)
      .then(res => {
        this.props.onGetRulesSuccess(res);
      })
      .catch(error => {
        console.log(error);
      });
  }

  checkBotStatus() {
    axiosGetWithAuth("/api/metadata/getStrategyMetadata")
      .then(handleResponse)
      .then(res => {
        this.props.onReceivedBotStatusMessage(true);
      })
      .catch(error => {
        this.props.onReceivedBotStatusMessage(false);
      });
  }

  componentDidMount() {
    this.setState({ _notificationSystem: this.refs.notificationSystem });
    if (navigator.platform.indexOf("Win") > -1) {
      ps = new PerfectScrollbar(this.refs.mainPanel);
    }
    this.connectToSockets();
    this.initMetadata();
    this.initData();
    this.checkBotStatus();
    this.interval = setInterval(() => {
      this.checkBotStatus();
    }, 5 * 60 * 1000);
    setTimeout(() => {
      this.initData();
    }, 15 * 1000);
  }

  componentWillUnmount() {
    if (navigator.platform.indexOf("Win") > -1) {
      ps.destroy();
    }

    clearInterval(this.interval);
  }

  componentDidUpdate(e) {
    if (navigator.platform.indexOf("Win") > -1) {
      setTimeout(() => {
        ps.update();
      }, 350);
    }
    if (e.history.action === "PUSH") {
      document.documentElement.scrollTop = 0;
      document.scrollingElement.scrollTop = 0;
      this.refs.mainPanel.scrollTop = 0;
    }
    if (
      window.innerWidth < 993 &&
      e.history.action === "PUSH" &&
      document.documentElement.className.indexOf("nav-open") !== -1
    ) {
      document.documentElement.classList.toggle("nav-open");
    }
  }

  componentWillMount() {
    if (document.documentElement.className.indexOf("nav-open") !== -1) {
      document.documentElement.classList.toggle("nav-open");
    }
  }

  // function that shows/hides notifications - it was put here, because the wrapper div has to be outside the main-panel class div
  handleNotification(message, level, position) {
    var messageBody = JSON.parse(message.body).content;
    if (this.state._notificationSystem) {
      this.state._notificationSystem.addNotification({
        title: <span data-notify="icon" className="pe-7s-gift" />,
        message: <div>{messageBody}</div>,
        level: level,
        position: position,
        autoDismiss: 15
      });
    }
  }

  render() {
    return (
      <div className="wrapper">
        <NotificationSystem ref="notificationSystem" style={style} />
        <Sidebar {...this.props} />
        <div
          className={
            "main-panel" +
            (this.props.location.pathname === "/maps/full-screen-maps"
              ? " main-panel-maps"
              : "")
          }
          ref="mainPanel"
        >
          <Header {...this.props} />
          <Switch>
            {dashboardRoutes.map((prop, key) => {
              if (prop.collapse) {
                return prop.views.map((prop, key) => {
                  return (
                    <Route
                      path={prop.path}
                      component={prop.component}
                      key={key}
                    />
                  );
                });
              } else {
                if (prop.redirect)
                  return (
                    <Redirect from={prop.path} to={prop.pathTo} key={key} />
                  );
                else
                  return (
                    <Route
                      path={prop.path}
                      component={prop.component}
                      key={key}
                    />
                  );
              }
            })}
          </Switch>
        </div>
      </div>
    );
  }
}

export default withRouter(DashboardComp);
