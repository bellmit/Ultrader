import React, { Component } from "react";

import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

import { HashRouter, Route, Switch } from "react-router-dom";

import indexRoutes from "../routes/index.jsx";

class AppComp extends Component {
  constructor(props) {
    super(props);

    this.processGreeting = this.processGreeting.bind(this);

            let socket = new SockJS('http://localhost:9191/gs-guide-websocket');
            let stompClient = Stomp.over(socket);

            stompClient.connect({}, frame => {

              this.props.onConnectedToMonitor(socket, stompClient);
              console.log(`connected, ${frame}!`);
              stompClient.subscribe('/topic/greetings', this.processGreeting);
            });
  }


  processGreeting(greeting) {
      this.props.onReceivedMonitorMessage(JSON.parse(greeting.body).content);
   }

  componentDidMount() {
  }

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
