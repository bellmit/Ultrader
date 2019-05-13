import React from "react";
import ReactDOM from "react-dom";
import { createStore } from 'redux';
import appReducer from './reducers';
import Root from './containers/Root';

import "bootstrap/dist/css/bootstrap.min.css";
import "./assets/sass/light-bootstrap-dashboard.css?v=1.1.1";
import "./assets/css/demo.css";
import "./assets/css/pe-icon-7-stroke.css";
import "css/style.css";

const store = createStore(appReducer);

ReactDOM.render(
  <Root store={store} />,
  document.getElementById("root")
);
