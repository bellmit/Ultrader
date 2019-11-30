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

class SettingsForm extends Component {
  constructor(props) {
    super(props);
    this.textOnChange = this.textOnChange.bind(this);
    this.state = {};
  }

  isValidated() {
    return true;
  }

  textOnChange(e) {
    this.props.onAddConditionalSetting(
      this.props.selectedMarketTrend,
      e.target.id,
      e.target.value
    );
  }

  render() {
    return (
      <Card
        content={
          <form>
            <FormGroup>
              <ControlLabel>
                Trade Price Limit Max {tooltip("TradePriceLimitMax")}
              </ControlLabel>
              <FormControl
                id="TRADE_PRICE_LIMIT_MAX"
                value={
                  this.props.conditionalSettings[
                    this.props.selectedMarketTrend
                  ]["TRADE_PRICE_LIMIT_MAX"]
                }
                onChange={this.textOnChange}
                type="text"
                placeholder="Trade Price Limit Max"
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Trade Price Limit Min {tooltip("TradePriceLimitMin")}
              </ControlLabel>
              <FormControl
                id="TRADE_PRICE_LIMIT_MIN"
                value={
                  this.props.conditionalSettings[
                    this.props.selectedMarketTrend
                  ]["TRADE_PRICE_LIMIT_MIN"]
                }
                onChange={this.textOnChange}
                type="text"
                placeholder="Trade Price Limit Min"
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Trade Volume Limit Max {tooltip("TradeVolumeLimitMax")}
              </ControlLabel>
              <FormControl
                id="TRADE_VOLUME_LIMIT_MAX"
                value={
                  this.props.conditionalSettings[
                    this.props.selectedMarketTrend
                  ]["TRADE_VOLUME_LIMIT_MAX"]
                }
                onChange={this.textOnChange}
                type="text"
                placeholder="Trade Volume Limit Max"
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Trade Volume Limit Min {tooltip("TradeVolumeLimitMin")}
              </ControlLabel>
              <FormControl
                id="TRADE_VOLUME_LIMIT_MIN"
                value={
                  this.props.conditionalSettings[
                    this.props.selectedMarketTrend
                  ]["TRADE_VOLUME_LIMIT_MIN"]
                }
                onChange={this.textOnChange}
                type="text"
                placeholder="Trade Volume Limit Min"
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Trade Buy Max Limit {tooltip("TradeBuyLimitMax")}
              </ControlLabel>
              <FormControl
                id="TRADE_BUY_MAX_LIMIT"
                value={
                  this.props.conditionalSettings[
                    this.props.selectedMarketTrend
                  ]["TRADE_BUY_MAX_LIMIT"]
                }
                onChange={this.textOnChange}
                type="text"
                placeholder="Trade Buy Max Limit"
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Trade Buy Holding Limit {tooltip("TradeHoldLimitMax")}
              </ControlLabel>
              <FormControl
                id="TRADE_BUY_HOLDING_LIMIT"
                value={
                  this.props.conditionalSettings[
                    this.props.selectedMarketTrend
                  ]["TRADE_BUY_HOLDING_LIMIT"]
                }
                onChange={this.textOnChange}
                type="text"
                placeholder="Trade Buy Holding Limit"
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Trade Buy Strategy {tooltip("BuyStrategy")}
              </ControlLabel>
              <Select
                placeholder="Trade Buy Strategy"
                name="buyStrategy"
                options={this.props.buyStrategyOptions}
                value={
                  this.props.selectedConditionalSettingOptions[
                    this.props.selectedMarketTrend
                  ]["TRADE_BUY_STRATEGY"]
                }
                id="TRADE_BUY_STRATEGY"
                onChange={option =>
                  this.props.selectConditionalOption(
                    this.props.selectedMarketTrend,
                    "TRADE_BUY_STRATEGY",
                    option
                  )
                }
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Trade Sell Strategy {tooltip("SellStrategy")}
              </ControlLabel>
              <Select
                placeholder="Trade Sell Strategy"
                name="sellStrategy"
                options={this.props.sellStrategyOptions}
                value={
                  this.props.selectedConditionalSettingOptions[
                    this.props.selectedMarketTrend
                  ]["TRADE_SELL_STRATEGY"]
                }
                id="TRADE_SELL_STRATEGY"
                onChange={option =>
                  this.props.selectConditionalOption(
                    this.props.selectedMarketTrend,
                    "TRADE_SELL_STRATEGY",
                    option
                  )
                }
              />
            </FormGroup>
          </form>
        }
      />
    );
  }
}

export default SettingsForm;
