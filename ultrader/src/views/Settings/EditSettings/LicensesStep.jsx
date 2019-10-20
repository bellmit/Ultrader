import React from "react";
import {
  Grid,
  Button,
  Row,
  Col,
  FormGroup,
  FormControl,
  ControlLabel
} from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import { tooltip } from "helpers/TooltipHelper";

class LicensesStep extends React.Component {
  constructor(props) {
    super(props);
    this.textOnChange = this.textOnChange.bind(this);
  }

  textOnChange(e) {
    this.props.onAddSetting(e.target.id, e.target.value);
  }

  isValidated() {
    var valid =
      this.props.settings["KEY_ULTRADER_KEY"] &&
      this.props.settings["KEY_ULTRADER_SECRET"] &&
      this.props.settings["KEY_ALPACA_PAPER_KEY"] &&
      this.props.settings["KEY_ALPACA_PAPER_SECRET"];
    if (!valid) {
      alert("Please fill in all required fields.");
    }
    return valid;
  }

  render() {
    return (
      <Card
        content={
          <form>
            <FormGroup>
              <ControlLabel>
                Ultrader Bot Key <span className="star">*</span>{" "}
                {tooltip("UltraderKey")}
              </ControlLabel>
              <FormControl
                id="KEY_ULTRADER_KEY"
                value={this.props.settings["KEY_ULTRADER_KEY"]}
                onChange={this.textOnChange}
                type="text"
                placeholder="Ultrader Bot Key"
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Ultrader Bot Secret <span className="star">*</span>{" "}
                {tooltip("UltraderSecret")}
              </ControlLabel>
              <FormControl
                id="KEY_ULTRADER_SECRET"
                value={this.props.settings["KEY_ULTRADER_SECRET"]}
                onChange={this.textOnChange}
                type="text"
                placeholder="Ultrader Bot Secret"
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Alpaca Paper Trading Key <span className="star">*</span>{" "}
                {tooltip("AlpacaPaperKey")}
              </ControlLabel>
              <FormControl
                id="KEY_ALPACA_PAPER_KEY"
                value={this.props.settings["KEY_ALPACA_PAPER_KEY"]}
                onChange={this.textOnChange}
                type="text"
                placeholder="Alpaca Paper Trading Key"
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Alpaca Paper Trading Secret <span className="star">*</span>{" "}
                {tooltip("AlpacaPaperSecret")}
              </ControlLabel>
              <FormControl
                id="KEY_ALPACA_PAPER_SECRET"
                value={this.props.settings["KEY_ALPACA_PAPER_SECRET"]}
                onChange={this.textOnChange}
                type="text"
                placeholder="Alpaca Paper Trading Secret"
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Alpaca Live Trading Key {tooltip("AlpacaLiveKey")}
              </ControlLabel>
              <FormControl
                id="KEY_ALPACA_KEY"
                value={this.props.settings["KEY_ALPACA_KEY"]}
                onChange={this.textOnChange}
                type="text"
                placeholder="Alpaca Live Trading Key"
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Alpaca Live Trading Secret {tooltip("AlpacaLiveSecret")}
              </ControlLabel>
              <FormControl
                id="KEY_ALPACA_SECRET"
                value={this.props.settings["KEY_ALPACA_SECRET"]}
                onChange={this.textOnChange}
                type="text"
                placeholder="Alpaca Live Trading Secret"
              />
            </FormGroup>
            <div className="category" style={{ color: "red" }}>
              <span className="star">*</span> Required fields
            </div>
          </form>
        }
      />
    );
  }
}

export default LicensesStep;
