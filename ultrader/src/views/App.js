import React, { Component } from "react";
import PrivateRoute from "components/Routes/PrivateRoute";
import LoginPage from "containers/Pages/LoginPage";
import RegisterPage from "containers/Pages/RegisterPage";

import SockJS from "sockjs-client";
import Stomp from "stompjs";
import axios from "axios";

import { HashRouter, Route, Switch } from "react-router-dom";

import indexRoutes from "../routes/index.jsx";

import {
  axiosGetWithAuth,
  axiosPostWithAuth,
  handleResponse
} from "helpers/UrlHelper";

class AppComp extends Component {
  constructor(props) {
    super(props);

    this.processGreeting = this.processGreeting.bind(this);
    /*
    let socket = new SockJS("/api/gs-guide-websocket");
    let stompClient = Stomp.over(socket);

    stompClient.connect({}, frame => {
      this.props.onConnectedToMonitor(socket, stompClient);
      console.log(`connected, ${frame}!`);
      stompClient.subscribe("/topic/greetings", this.processGreeting);
    });
*/
    axiosGetWithAuth("/api/rule/getRuleType")
      .then(handleResponse)
      .then(res => {
        console.log(res);
        this.props.onRetrievedRuleTypes(res);
      })
      .catch(error => {});

    axiosGetWithAuth("/api/rule/getIndicatorType")
      .then(handleResponse)
      .then(res => {
        console.log(res);
        this.props.onRetrievedIndicatorTypes(res);
      })
      .catch(error => {});
    axiosGetWithAuth("/api/rule/getIndicatorCategory")
      .then(handleResponse)
      .then(res => {
        console.log(res);
        this.props.onRetrievedIndicatorCategories(res);
      })
      .catch(error => {});

    axiosGetWithAuth("/api/rule/getCategoryIndicatorMap")
      .then(handleResponse)
      .then(res => {
        console.log(res);
        this.props.onRetrievedCategoryIndicatorMap(res);
      })
      .catch(error => {});

    axiosGetWithAuth("/api/strategy/getStrategies")
      .then(handleResponse)
      .then(res => {
        console.log(res);
        this.props.onGetStrategiesSuccess(res);
      })
      .catch(error => {
        console.log(error);
      });

    axiosGetWithAuth("/api/rule/getRules")
      .then(handleResponse)
      .then(res => {
        console.log(res);
        this.props.onGetRulesSuccess(res);
      })
      .catch(error => {
        console.log(error);
      });
  }

  processGreeting(greeting) {
    this.props.onReceivedMonitorMessage(JSON.parse(greeting.body).content);
  }

  componentDidMount() {}

  render() {
    return (
      <div>
        <HashRouter>
          <Switch>
            {indexRoutes.map((prop, key) => {
              return (
                <Route path={prop.path} component={prop.component} key={key} />
              );
            })}
          </Switch>
        </HashRouter>
      </div>
    );
  }

  render() {
    const mainRouteComponents = indexRoutes.map((prop, key) =>
      prop.private ? (
        <PrivateRoute path={prop.path} component={prop.component} key={key} />
      ) : (
        <Route path={prop.path} component={prop.component} key={key} />
      )
    );
    console.log(localStorage.getItem("user"));

    return (
      <div>
        <HashRouter>
          <Switch>{mainRouteComponents}</Switch>
        </HashRouter>
      </div>
    );
  }
}
export default AppComp;
