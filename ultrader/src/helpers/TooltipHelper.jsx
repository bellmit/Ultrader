import React from "react";
import { Tooltip, OverlayTrigger } from "react-bootstrap";
import Button from "components/CustomButton/CustomButton.jsx";

export function tooltip(message) {
  return (
    <OverlayTrigger placement="top" overlay={<Tooltip>{message}</Tooltip>}>
      <Button bsStyle="secondary" simple type="button" bsSize="xs">
        <i class="fa fa-info-circle" color="secondary"></i>
      </Button>
    </OverlayTrigger>
  );
}
