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

import { logout } from "helpers/AuthHelper";

class HeaderLinks extends Component {
  constructor(props) {
    super(props);
    this.iconColor = this.iconColor.bind(this);
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
            </NavItem>
          </OverlayTrigger>
          <NavDropdown
            eventKey={3}
            title={
              <div>
                <i className="fa fa-bell-o" />
                <span className="notification">
                  {this.props.notifications.length}
                </span>
                <p className="hidden-md hidden-lg">
                  Notifications
                  <b className="caret" />
                </p>
              </div>
            }
            noCaret
            id="basic-nav-dropdown-2"
          >
            {this.props.notifications.map((notification, i) => (
              <MenuItem eventKey={"3." + i} key={"3." + i}>
                {notification.message}
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
            <MenuItem eventKey={4.2}>
              <i className="pe-7s-help1" /> Help Center
            </MenuItem>
            <MenuItem eventKey={4.3}>
              <i className="pe-7s-tools" /> Settings
            </MenuItem>
            <MenuItem divider />
            <MenuItem eventKey={4.5} onClick={logout}>
              <div className="text-danger">
                <i className="pe-7s-close-circle" /> Log out
              </div>
            </MenuItem>
          </NavDropdown>
        </Nav>
      </div>
    );
  }
}
export default HeaderLinks;
