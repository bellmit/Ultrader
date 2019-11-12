import React, { Component } from "react";
import {
  Grid,
  Row,
  Col,
  FormGroup,
  FormControl,
  ControlLabel
} from "react-bootstrap";
import Select from "react-select";

import Card from "components/Card/Card.jsx";
import { tooltip } from "helpers/TooltipHelper";
import Button from "components/CustomButton/CustomButton.jsx";
var booleanOptions = [
  { value: "true", label: "Enable" },
  { value: "false", label: "Disable" }
];
class AlpacaStep extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  isValidated() {
    return true;
  }

  render() {
    const { exchangeInputValue, exchangeMenuOpen } = this.state;
    return (
      <Card
        content={
          <form>
            <FormGroup>
              <ControlLabel>
                Day Trading Margin Check {tooltip("DayTradingMarginCheck")}
              </ControlLabel>
              <Select
                placeholder="Day Trading Margin Check"
                name="marginCheck"
                options={this.props.marginCheckOptions}
                value={this.props.selectedOptions["ALPACA_DTMC"]}
                id="ALPACA_DTMC"
                onChange={option =>
                  this.props.selectOption("ALPACA_DTMC", option)
                }
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                No Shorting Trade {tooltip("NoShortingTrade")} <a href="https://docs.alpaca.markets/trading-on-alpaca/margin-and-shorting/">Read Before Enable</a>
              </ControlLabel>
              <Select
                placeholder="No Shorting Trade"
                name="marginCheck"
                options={this.props.booleanOptions}
                value={this.props.selectedOptions["ALPACA_NO_SHORTING"]}
                id="ALPACA_NO_SHORTING"
                onChange={option =>
                  this.props.selectOption("ALPACA_NO_SHORTING", option)
                }
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Suspend Trade {tooltip("SuspendTrade")}
              </ControlLabel>
              <Select
                placeholder="Suspend Trade"
                name="marginCheck"
                options={this.props.booleanOptions}
                value={this.props.selectedOptions["ALPACA_SUSPEND_TRADE"]}
                id="ALPACA_SUSPEND_TRADE"
                onChange={option =>
                  this.props.selectOption("ALPACA_SUSPEND_TRADE", option)
                }
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Email Notification {tooltip("EmailNotification")}
              </ControlLabel>
              <Select
                placeholder="Email Notification"
                name="emailNotification"
                options={this.props.emailNotificationOptions}
                value={this.props.selectedOptions["ALPACA_TRADE_CONFIRM_EMAIL"]}
                id="ALPACA_TRADE_CONFIRM_EMAIL"
                onChange={option =>
                  this.props.selectOption("ALPACA_TRADE_CONFIRM_EMAIL", option)
                }
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>Use Margin {tooltip("UseMargin")}  <a href="https://docs.alpaca.markets/trading-on-alpaca/margin-and-shorting/">Read Before Enable</a></ControlLabel>
              <Select
                placeholder="Use Margin"
                name="useMargin"
                options={this.props.booleanOptions}
                value={this.props.selectedOptions["ALPACA_USE_MARGIN"]}
                id="ALPACA_USE_MARGIN"
                onChange={option =>
                  this.props.selectOption("ALPACA_USE_MARGIN", option)
                }
              />
            </FormGroup>
          </form>
        }
      />
    );
  }
}

export default AlpacaStep;
