import React, { Component } from "react";
import PrivateRoute from "components/Routes/PrivateRoute";
import LoginPage from "containers/Pages/LoginPage";
import RegisterPage from "containers/Pages/RegisterPage";

import { HashRouter, Route, Switch } from "react-router-dom";

import indexRoutes from "../routes/index.jsx";



class AppComp extends Component {
  constructor(props) {
    super(props);
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
