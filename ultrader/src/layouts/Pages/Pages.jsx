import React, { Component } from "react";
import { Switch, Route } from "react-router-dom";

import PagesHeader from "components/Header/PagesHeader.jsx";
import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";

// dinamically create pages routes
import pagesRoutes from "routes/pages.jsx";

import bgImage from "assets/img/Bulle_und_Bär_Frankfurt.jpg";

class Pages extends Component {
  constructor(props) {
    super(props);

    this.state = {
      hasUsers: true
    };
  }

  getPageClass() {
    var pageClass = "";
    switch (this.props.location.pathname) {
      case "/pages/login-page":
        pageClass = " login-page";
        break;
      case "/pages/register-page":
        pageClass = " register-page";
        break;
      case "/pages/lock-screen-page":
        pageClass = " lock-page";
        break;
      default:
        pageClass = "";
        break;
    }
    return pageClass;
  }

  componentWillMount() {
    if (document.documentElement.className.indexOf("nav-open") !== -1) {
      document.documentElement.classList.toggle("nav-open");
    }
  }

  componentDidMount() {
    axiosGetWithAuth("/api/user/hasUsers")
      .then(res => {
        if (res.data) {
          this.setState({ hasUsers: true });
        } else {
          this.setState({ hasUsers: false });
        }
      })
      .catch(error => {
        console.log(error);
        alertError(error);
      });
  }

  render() {
    return (
      <div>
        <PagesHeader hasUsers={this.state.hasUsers} />
        <div className="wrapper wrapper-full-page">
          <div
            className={"full-page" + this.getPageClass()}
            data-color="black"
            data-image={bgImage}
          >
            <div className="content">
              <Switch>
                {pagesRoutes.map((prop, key) => {
                  const Comp = prop.component;
                  return (
                    <Route
                      path={prop.path}
                      key={key}
                      render={props => (
                        <Comp hasUsers={this.state.hasUsers} {...props} />
                      )}
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

export default Pages;
