import React, { Component } from "react";
import Button from "components/CustomButton/CustomButton.jsx";
import { checkRolePermission } from "helpers/AuthHelper";


class CustomPrivateButton extends Component {
  render() {
    return checkRolePermission(this.props.user, this.props.requiredRoleId) ? <Button {...this.props} /> : "";
  }
}

export default CustomPrivateButton;
