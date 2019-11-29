import React, { Component } from "react";
import { Collapse } from "react-bootstrap";
import { NavLink } from "react-router-dom";
// this is used to create scrollbars on windows devices like the ones from apple devices
import PerfectScrollbar from "perfect-scrollbar";
import "perfect-scrollbar/css/perfect-scrollbar.css";

import { checkRolePermission } from "helpers/AuthHelper";

import HeaderLinks from "components/Header/HeaderLinks.jsx";

// backgroundImage for Sidebar
import image from "assets/img/blur-chart-data-159888.jpg";

// logo for sidebar
import logo from "logo.png";
import logoText from "assets/img/banner.png";

import dashboardRoutes from "routes/dashboard.jsx";

import TourBox from "components/TourBox/TourBox.jsx";

const bgImage = { backgroundImage: "url(" + image + ")" };

var ps;

class Sidebar extends Component {
  constructor(props) {
    super(props);
    this.state = {
      openAvatar: false,
      openComponents: this.activeRoute("/components") !== "" ? true : false,
      openForms: this.activeRoute("/forms") !== "" ? true : false,
      openTables: this.activeRoute("/tables") !== "" ? true : false,
      openMaps: this.activeRoute("/maps") !== "" ? true : false,
      openPages: this.activeRoute("/pages") !== "" ? true : false,
      isWindows: navigator.platform.indexOf("Win") > -1 ? true : false,
      width: window.innerWidth
    };
  }
  // verifies if routeName is the one active (in browser input)
  activeRoute(routeName) {
    return this.props.location.pathname.indexOf(routeName) > -1 ? "active" : "";
  }
  // if the windows width changes CSS has to make some changes
  // this functions tell react what width is the window
  updateDimensions() {
    this.setState({ width: window.innerWidth });
  }
  componentDidMount() {
    this.updateDimensions();
    // add event listener for windows resize
    window.addEventListener("resize", this.updateDimensions.bind(this));
    if (navigator.platform.indexOf("Win") > -1) {
      ps = new PerfectScrollbar(this.refs.sidebarWrapper, {
        suppressScrollX: true,
        suppressScrollY: false
      });
    }
  }
  componentDidUpdate() {
    if (navigator.platform.indexOf("Win") > -1) {
      setTimeout(() => {
        ps.update();
      }, 350);
    }
  }
  componentWillUnmount() {
    if (navigator.platform.indexOf("Win") > -1) {
      ps.destroy();
    }
  }

  render() {
    return (
      <div className="sidebar" data-color="black" data-image={image}>
        <div className="sidebar-background" style={bgImage} />
        <div className="logo">
          <a
            href="http://www.ultraderbot.com"
            className="simple-text logo-normal"
          >
            <img
              src={logoText}
              alt="Ultrader"
              style={{ height: 40, width: 244 }}
            />
          </a>
        </div>
        <div className="sidebar-wrapper" ref="sidebarWrapper">
          <ul className="nav">
            {/* If we are on responsive, we want both links from navbar and sidebar
                            to appear in sidebar, so we render here HeaderLinks */}
            {this.state.width <= 992 ? <HeaderLinks {...this.props} /> : null}
            {/*
                            here we render the links in the sidebar
                            if the link is simple, we make a simple link, if not,
                            we have to create a collapsible group,
                            with the specific parent button and with it's children which are the links
                        */}
            {dashboardRoutes.map((prop, key) => {
              var st = {};
              st[prop["state"]] = !this.state[prop.state];
              if (checkRolePermission(this.props.user, prop.requiredRoleId)) {
                if (prop.collapse) {
                  return (
                    <li className={this.activeRoute(prop.path)} key={key}>
                      <a onClick={() => this.setState(st)}>
                        <i className={prop.icon} />
                        <p>
                          {prop.name}
                          <b
                            className={
                              this.state[prop.state]
                                ? "caret rotate-180"
                                : "caret"
                            }
                          />
                        </p>
                      </a>
                      <Collapse in={this.state[prop.state]}>
                        <ul className="nav">
                          {prop.views.map((prop, key) => {
                            if (
                              checkRolePermission(
                                this.props.user,
                                prop.requiredRoleId
                              )
                            ) {
                              return (
                                <li
                                  className={this.activeRoute(prop.path)}
                                  key={key}
                                >
                                  <NavLink
                                    to={prop.path}
                                    className="nav-link"
                                    activeClassName="active"
                                  >
                                    <TourBox data-tour={prop.tour}>
                                      <span className="sidebar-mini">
                                        {prop.mini}
                                      </span>
                                      <span className="sidebar-normal">
                                        {prop.name}
                                      </span>
                                    </TourBox>
                                  </NavLink>
                                </li>
                              );
                            }
                          })}
                        </ul>
                      </Collapse>
                    </li>
                  );
                } else {
                  if (prop.redirect) {
                    return null;
                  } else {
                    return (
                      <li className={this.activeRoute(prop.path)} key={key}>
                        <NavLink
                          to={prop.path}
                          className="nav-link"
                          activeClassName="active"
                        >
                          <TourBox data-tour={prop.tour}>
                            <i className={prop.icon} />
                            <p>{prop.name}</p>
                          </TourBox>
                        </NavLink>
                      </li>
                    );
                  }
                }
              }
            })}
          </ul>
        </div>
      </div>
    );
  }
}

export default Sidebar;
