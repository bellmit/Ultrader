import React, { Component } from "react";

import SockJS from "sockjs-client";
import Stomp from "stompjs";
import axios from "axios";

import { HashRouter, Route, Switch } from "react-router-dom";

import indexRoutes from "../routes/index.jsx";

class AppComp extends Component {
  constructor(props) {
    super(props);

    this.processGreeting = this.processGreeting.bind(this);

    let socket = new SockJS("http://localhost:9191/gs-guide-websocket");
    let stompClient = Stomp.over(socket);

    stompClient.connect({}, frame => {
      this.props.onConnectedToMonitor(socket, stompClient);
      console.log(`connected, ${frame}!`);
      stompClient.subscribe("/topic/greetings", this.processGreeting);
    });

    axios
      .get("http://localhost:9191/rule/getRuleType")
      .then(res => {
        console.log(res);
        this.props.onRetrievedRuleTypes(res);
      })
      .catch(error => {
        alert(error);
      });
    axios
      .get("http://localhost:9191/rule/getIndicatorType")
      .then(res => {
        console.log(res);
        this.props.onRetrievedIndicatorTypes(res);
      })
      .catch(error => {
        alert(error);
      });
    axios
      .get("http://localhost:9191/rule/getIndicatorCategory")
      .then(res => {
        console.log(res);
        this.props.onRetrievedIndicatorCategories(res);
      })
      .catch(error => {
        alert(error);
      });

    axios
      .get("http://localhost:9191/rule/getCategoryIndicatorMap")
      .then(res => {
        console.log(res);
        this.props.onRetrievedCategoryIndicatorMap(res);
      })
      .catch(error => {
        alert(error);
      });

          axios
            .get("http://localhost:9191/strategy/getStrategies")
            .then(res => {
              console.log(res);
              this.props.onGetStrategiesSuccess(res);
            })
            .catch(error => {
              console.log(error);
              alert(error);
            });

          axios
            .get("http://localhost:9191/rule/getRules")
            .then(res => {
              console.log(res);
              this.props.onGetRulesSuccess(res);
            })
            .catch(error => {
              console.log(error);
              alert(error);
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
}
export default AppComp;
