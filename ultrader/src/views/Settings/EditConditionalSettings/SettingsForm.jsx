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

var exchangeOptions = [
  { value: "NASDAQ", label: "NASDAQ" },
  { value: "AMEX", label: "AMEX" },
  { value: "ARCA", label: "ARCA" },
  { value: "BATS", label: "BATS" },
  { value: "NYSE", label: "NYSE" },
  { value: "NYSEARCA", label: "NYSEARCA" }
];
var booleanOptions = [
  { value: "true", label: "Enable" },
  { value: "false", label: "Disable" }
];
var intervalOptions = [
  { value: "60", label: "1 Minute" },
  { value: "300", label: "5 Minutes" },
  { value: "900", label: "15 Minutes" },
  { value: "86400", label: "1 Day" }
];

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
    console.log(this.props);
    const { exchangeInputValue, exchangeMenuOpen } = this.state;
    return (
      <Card
        content={
          <form>
            <FormGroup>
              <ControlLabel>
                Trade Exchange List {tooltip("TradeExchangeList")}
              </ControlLabel>
              <Select
                isMulti
                value={
                  this.props.selectedConditionalSettingOptions[
                    this.props.selectedMarketTrend
                  ]["TRADE_EXCHANGE_LIST"]
                }
                isClearable
                isSearchable
                inputValue={exchangeInputValue}
                onChange={option =>
                  this.props.onExchangeInputChange(
                    this.props.selectedMarketTrend,
                    option
                  )
                }
                name="exchange"
                id="TRADE_EXCHANGE_LIST"
                options={exchangeOptions}
                menuIsOpen={exchangeMenuOpen}
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Trading Period in Seconds {tooltip("TradePeriod")}
              </ControlLabel>
              <Select
                placeholder="Trading Period in Seconds"
                name="tradeperiod"
                options={intervalOptions}
                value={
                  this.props.selectedConditionalSettingOptions[
                    this.props.selectedMarketTrend
                  ]["TRADE_PERIOD_SECOND"]
                }
                id="TRADE_PERIOD_SECOND"
                onChange={option =>
                  this.props.selectConditionalOption(
                    this.props.selectedMarketTrend,
                    "TRADE_PERIOD_SECOND",
                    option
                  )
                }
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Is Trading White List Enabled {tooltip("WhiteListEnabled")}
              </ControlLabel>
              <FormControl
                id="TRADE_WHITE_LIST_ENABLE"
                value={
                  this.props.conditionalSettings[
                    this.props.selectedMarketTrend
                  ]["TRADE_WHITE_LIST_ENABLE"]
                }
                onChange={this.textOnChange}
                type="text"
                placeholder="Is Trading White List Enabled"
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Trading Stock List {tooltip("StockList")}
              </ControlLabel>
              <Select
                placeholder="Choose a created Asset List"
                name="tradingStockList"
                options={this.props.assetListOptions}
                value={
                  this.props.selectedConditionalSettingOptions[
                    this.props.selectedMarketTrend
                  ]["TRADE_STOCK_LIST"]
                }
                id="TRADE_STOCK_LIST"
                onChange={option =>
                  this.props.selectConditionalOption(
                    this.props.selectedMarketTrend,
                    "TRADE_STOCK_LIST",
                    option
                  )
                }
              />
            </FormGroup>
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
            <FormGroup>
              <ControlLabel>
                Trade Buy Order Type {tooltip("BuyOrderType")}
              </ControlLabel>
              <Select
                placeholder="Trade Buy Order Type"
                name="marketDataPlatform"
                options={this.props.orderTypeOptions}
                value={
                  this.props.selectedConditionalSettingOptions[
                    this.props.selectedMarketTrend
                  ]["TRADE_BUY_ORDER_TYPE"]
                }
                id="TRADE_BUY_ORDER_TYPE"
                onChange={option =>
                  this.props.selectConditionalOption(
                    this.props.selectedMarketTrend,
                    "TRADE_BUY_ORDER_TYPE",
                    option
                  )
                }
              />
            </FormGroup>
            <FormGroup>
              <ControlLabel>
                Trade Sell Order Type {tooltip("SellOrderType")}
              </ControlLabel>
              <Select
                placeholder="Trade Sell Order Type"
                name="marketDataPlatform"
                options={this.props.orderTypeOptions}
                value={
                  this.props.selectedConditionalSettingOptions[
                    this.props.selectedMarketTrend
                  ]["TRADE_SELL_ORDER_TYPE"]
                }
                id="TRADE_SELL_ORDER_TYPE"
                onChange={option =>
                  this.props.selectConditionalOption(
                    this.props.selectedMarketTrend,
                    "TRADE_SELL_ORDER_TYPE",
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
