import React, { Component } from "react";
import {
  Navbar,
  Nav,
  NavItem,
  NavDropdown,
  MenuItem,
  FormGroup,
  FormControl,
  InputGroup,
  OverlayTrigger,
  Tooltip
} from "react-bootstrap";
import Card from "components/Card/Card.jsx";
import { logout } from "helpers/AuthHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";
import { parseDate, parseProfit } from "helpers/ParseHelper";
import PerfectScrollbar from "perfect-scrollbar";
import "perfect-scrollbar/css/perfect-scrollbar.css";
import "assets/css/headerlinks.css";
import {
  axiosGetWithAuth,
  handleResponse,
  getAuthHeader
} from "helpers/UrlHelper";
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
  }

  getNotification() {
    axiosGetWithAuth("/api/notification/getNotifications?length=10")
      .then(res => {
        var notifications = res.data.reverse();
        for (var i in notifications) {
          var notification = {};
          var messageBody = notifications[i];
          var level = "info";
          var icon = "pe-7s-info";
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
      .then(res => {
        alertSuccess("Reboot Ultrader Successfully!");
      })
      .catch(error => {});
  }
  render() {
    return (
      <div>
        <Nav pullRight>
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
                  this.iconColor(this.props.systemStatus.account.status)
                }
              />
              <p className="monitorIconText">Market Status</p>
            </NavItem>
          </OverlayTrigger>

          <NavDropdown
            eventKey={3}
            title={
              <div>
                <i className="fa fa-bell-o" />
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
                <i className="fa fa-cog" />
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
            <MenuItem eventKey={4.1}>
              <i className="pe-7s-mail" /> Messages
            </MenuItem>

            <MenuItem divider />
            <MenuItem eventKey={4.3} onClick={this.reboot}>
              <div className="text-danger">
                <i className="pe-7s-refresh" /> Reboot
              </div>
            </MenuItem>
            <MenuItem eventKey={4.4} onClick={logout}>
              <div className="text-danger">
                <i className="pe-7s-door-lock" /> Log out
              </div>
            </MenuItem>
          </NavDropdown>
        </Nav>
      </div>
    );
  }
}
export default HeaderLinks;
