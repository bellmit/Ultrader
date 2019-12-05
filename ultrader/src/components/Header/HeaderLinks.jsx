import React, { Component } from "react";
import {
  MenuItem,
  Nav,
  NavItem,
  NavDropdown,
  OverlayTrigger,
  Tooltip
} from "react-bootstrap";

import TourBox from "components/TourBox/TourBox.jsx";
import { logout } from "helpers/AuthHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";
import { parseDate } from "helpers/ParseHelper";
import PerfectScrollbar from "perfect-scrollbar";
import "perfect-scrollbar/css/perfect-scrollbar.css";
import "assets/css/headerlinks.css";
import { axiosGetWithAuth } from "helpers/UrlHelper";
import { disableBodyScroll, enableBodyScroll } from "body-scroll-lock";
import Tour from "reactour";

var ps;
class HeaderLinks extends Component {
  constructor(props) {
    super(props);
    this.iconColor = this.iconColor.bind(this);
    this.reboot = this.reboot.bind(this);
    this.isNewNotification = this.isNewNotification.bind(this);
    this.readNotification = this.readNotification.bind(this);
    this.getNotification = this.getNotification.bind(this);
    this.getNotification();

    this.state = {
      isTourOpen: false
    };
  }

  getNotification() {
    axiosGetWithAuth("/api/notification/getNotifications?length=10")
      .then(res => {
        let notifications = res.data.reverse();
        for (let i in notifications) {
          let notification = {};
          let messageBody = notifications[i];
          let level = "info";
          let icon = "pe-7s-info";
          switch (messageBody.type) {
            case "BUY":
              level = "#28a745";
              icon = "pe-7s-plus";
              break;
            case "SELL":
              level = "#28a745";
              icon = "pe-7s-less";
              break;
            case "WARN":
              level = "#ffc107";
              icon = "pe-7s-speaker";
              break;
            case "ERROR":
              level = "#dc3545";
              icon = "pe-7s-speaker";
              break;
            default:
              break;
          }
          notification.level = level;
          notification.icon = icon;
          notification.message = messageBody;
          notification.new = false;
          this.props.notifications.push(notification);
        }
      })
      .catch(error => {
        console.log(error);
        alertError(error);
      });
  }

  isNewNotification(notification) {
    return notification.new;
  }

  iconColor(status) {
    switch (status) {
      case "error":
        return "text-danger";
      case "warning":
      case "closed":
        return "text-warning";
      case "success":
      case "normal":
        return "text-success";
      default:
        return;
    }
  }

  readNotification() {
    for (var i in this.props.notifications) {
      this.props.notifications[i].new = false;
    }
  }

  componentDidMount() {
    if (navigator.platform.indexOf("Win") > -1) {
      ps = new PerfectScrollbar(".dropdown-menu");
    }
  }

  reboot() {
    axiosGetWithAuth("/api/setting/restart")
      .then(() => {
        alertSuccess("Reboot Ultrader Successfully!");
      })
      .catch(error => {});
  }

  disableBody = target => disableBodyScroll(target);
  enableBody = target => enableBodyScroll(target);

  closeTour = () => {
    this.setState({ isTourOpen: false });
  };

  openTour = () => {
    this.setState({ isTourOpen: true });
  };

  render() {
    return (
      <div>
        <Nav id="statusNav" pullRight>
          <TourBox id="statusTourStep" data-tour="tour-statuses">
            <OverlayTrigger
              placement="bottom"
              overlay={
                <Tooltip id="rocket-tooltip">
                  {this.props.systemStatus.bot.detail}
                </Tooltip>
              }
            >
              <NavItem>
                <i
                  className={
                    "fa fa-rocket " +
                    this.iconColor(this.props.systemStatus.bot.status)
                  }
                />
                <p className="monitorIconText">System Status</p>
              </NavItem>
            </OverlayTrigger>
            <OverlayTrigger
              placement="bottom"
              overlay={
                <Tooltip id="circle-tooltip">
                  {this.props.systemStatus.data.detail}
                </Tooltip>
              }
            >
              <NavItem>
                <i
                  className={
                    "fa fa-database " +
                    this.iconColor(this.props.systemStatus.data.status)
                  }
                />
                <p className="monitorIconText">Data Status</p>
              </NavItem>
            </OverlayTrigger>
            <OverlayTrigger
              placement="bottom"
              overlay={
                <Tooltip id="globe-tooltip">
                  {this.props.systemStatus.account.detail}
                </Tooltip>
              }
            >
              <NavItem>
                <i
                  className={
                    "fa fa-user " +
                    this.iconColor(this.props.systemStatus.account.status)
                  }
                />
                <p className="monitorIconText">Account Status</p>
              </NavItem>
            </OverlayTrigger>
            <OverlayTrigger
              placement="bottom"
              overlay={
                <Tooltip id="university-tooltip">
                  {this.props.systemStatus.market.detail}
                </Tooltip>
              }
            >
              <NavItem>
                <i
                  className={
                    "fa fa-university " +
                    this.iconColor(this.props.systemStatus.market.status)
                  }
                />
                <p className="monitorIconText">Market Status</p>
              </NavItem>
            </OverlayTrigger>

            <NavDropdown
              eventKey={3}
              title={
                <div>
                  <TourBox data-tour="tour-notifications">
                    <i className="fa fa-bell-o" />
                  </TourBox>
                  <span className="notification">
                    {
                      this.props.notifications.filter(this.isNewNotification)
                        .length
                    }
                  </span>
                  <p className="hidden-md hidden-lg">
                    Notifications
                    <b className="caret" />
                  </p>
                </div>
              }
              noCaret
              onClick={this.readNotification}
              id="basic-nav-dropdown-2"
            >
              {this.props.notifications.reverse().map((notification, i) => (
                <MenuItem eventKey={"3." + i} key={"3." + i}>
                  <div
                    className="card"
                    style={{
                      color: "white",
                      backgroundColor: notification.level,
                      marginBottom: "5px"
                    }}
                  >
                    <div className="content">
                      <p>
                        <i className={notification.icon}></i>&nbsp;
                        {notification.message.content}
                      </p>
                    </div>
                    <div className="footer">
                      <hr />
                      <div className="stats" style={{ color: "white" }}>
                        <div>
                          <i className="fa fa-clock-o"></i>{" "}
                          {parseDate(notification.message.date)}{" "}
                          <span style={{ float: "right" }}>
                            {notification.new ? "New" : ""}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                </MenuItem>
              ))}
            </NavDropdown>
            <NavDropdown
              eventKey={4}
              title={
                <div>
                <TourBox data-tour="tour-operations">
                  <i className="fa fa-cog" />
                </TourBox>
                  <p className="hidden-md hidden-lg">
                    More
                    <b className="caret" />
                  </p>
                </div>
              }
              noCaret
              id="basic-nav-dropdown-3"
              bsClass="dropdown-with-icons dropdown"
            >
              <MenuItem eventKey={4.1} onClick={this.openTour}>
                <i className="pe-7s-help1" /> Tour Guide
              </MenuItem>

              <MenuItem divider />
              <MenuItem eventKey={4.2} onClick={this.reboot}>
                <div className="text-danger">
                  <i className="pe-7s-refresh" /> Reboot
                </div>
              </MenuItem>
              <MenuItem eventKey={4.3} onClick={logout}>
                <div className="text-danger">
                  <i className="pe-7s-door-lock" /> Log out
                </div>
              </MenuItem>
            </NavDropdown>
          </TourBox>
        </Nav>
        <Tour
            onRequestClose={this.closeTour}
            steps={tourConfig}
            isOpen={this.state.isTourOpen}
            maskClassName="mask"
            className="helper"
            rounded={5}
            accentColor="5cb7b7"
            onAfterOpen={this.disableBody}
            onBeforeClose={this.enableBody}
        />
      </div>
    );
  }
}

const tourConfig = [
  {
    selector: '[data-tour="tour-welcome"]',
    content: `Welcome to the Ultrader user guide.`
  },
  {
    selector: '[data-tour="tour-statuses"]',
    content: `Indicators to tell you if the Ultrader is working expectedly. Hover on the icons to see more details.`
  },
  {
    selector: '[data-tour="tour-notifications"]',
    content: `Click to see all kinds of notifications. It's good to check if there is any critical error.`
  },
  {
    selector: '[data-tour="tour-operations"]',
    content: `Operations you can do, such as log out and reboot the Ultrader.`
  },
  {
    selector: '[data-tour="tour-menu"]',
    content: `Use the menu to navigate to the functions. Hover the information icon on each page to see more details.`
  }
];

export default HeaderLinks;
