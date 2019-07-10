import React, { Component } from "react";
import { Switch, Route } from "react-router-dom";

// dinamically create pages routes
import setupRoutes from "routes/setup.jsx";

import { axiosGetWithAuth } from "helpers/UrlHelper";

import bgImage from "assets/img/Bulle_und_Bär_Frankfurt.jpg";

class SetupComp extends Component {
  constructor(props) {
    super(props);
    this.initMetadata = this.initMetadata.bind(this);
  }

  componentDidMount() {
    this.initMetadata();
  }

  initMetadata() {
    axiosGetWithAuth("/api/metadata/getStrategyTemplate")
      .then(res => {
        console.log(res);
        this.props.onRetrievedStrategyTemplate(res);
      })
      .catch(error => {});
  }

  render() {
    return (
      <div>
        <div className="wrapper wrapper-full-page">
          <div className={"full-page"} data-color="black" data-image={bgImage}>
            <div className="content" style={{ paddingTop: "5vh" }}>
              <Switch>
                {setupRoutes.map((prop, key) => {
                  return (
                    <Route
                      path={prop.path}
                      component={prop.component}
                      key={key}
                    />
                  );
                })}
              </Switch>
            </div>
            <div
              className="full-page-background"
              style={{ backgroundImage: "url(" + bgImage + ")" }}
            />
          </div>
        </div>
      </div>
    );
  }
}

export default SetupComp;
