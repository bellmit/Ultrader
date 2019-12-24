import React, { Component } from "react";
import Button from "components/CustomButton/CustomButton.jsx";
import { checkRolePermission } from "helpers/AuthHelper";

class CustomPrivateButton extends Component {
  render() {
    const { user, requiredRoleId, ...rest } = this.props;
    return checkRolePermission(this.props.user, this.props.requiredRoleId) ? (
      <Button {...rest} />
    ) : (
      ""
    );
  }
}

export default CustomPrivateButton;
