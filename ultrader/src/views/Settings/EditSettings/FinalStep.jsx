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

import Button from "components/CustomButton/CustomButton.jsx";
var exchangeOptions = [
  { value: "NASDAQ", label: "NASDAQ" },
  { value: "AMEX", label: "AMEX" },
  { value: "ARCA", label: "ARCA" },
  { value: "BATS", label: "BATS" },
  { value: "NYSE", label: "NYSE" },
  { value: "NYSEARCA", label: "NYSEARCA" }
];
class FinalStep extends Component {
  constructor(props) {
    super(props);
    this.textOnChange = this.textOnChange.bind(this);
    this.state = {};
  }

  isValidated() {
    return true;
  }

  textOnChange(e) {
    this.props.onAddSetting(e.target.id, e.target.value);
  }

  render() {
    const { exchangeInputValue, exchangeMenuOpen } = this.state;
    return (
      <div className="wizard-step">
        <Grid fluid>
          <Row>
            <Col md={12}>
              <Card
                content={
                  <form>
                    <FormGroup>
                      <ControlLabel>Trade Exchange List</ControlLabel>
                      <Select
                        isMulti
                        value={this.props.selectedExchangeOptions}
                        isClearable
                        isSearchable
                        inputValue={exchangeInputValue}
                        onChange={this.props.onExchangeInputChange}
                        name="exchange"
                        id="TRADE_EXCHANGE_LIST"
                        options={exchangeOptions}
                        menuIsOpen={exchangeMenuOpen}
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trading Period in Seconds</ControlLabel>
                      <FormControl
                        id="TRADE_PERIOD_SECOND"
                        value={this.props.settings["TRADE_PERIOD_SECOND"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Trading Period in Seconds"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Is Trading White List Enabled</ControlLabel>
                      <FormControl
                        id="TRADE_WHITE_LIST_ENABLE"
                        value={this.props.settings["TRADE_WHITE_LIST_ENABLE"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Is Trading White List Enabled"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trading Stock List</ControlLabel>
                      <FormControl
                        id="TRADE_STOCK_LIST"
                        value={this.props.settings["TRADE_STOCK_LIST"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Trading Stock List"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trade Price Limit Max</ControlLabel>
                      <FormControl
                        id="TRADE_PRICE_LIMIT_MAX"
                        value={this.props.settings["TRADE_PRICE_LIMIT_MAX"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Trade Price Limit Max"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trade Price Limit Min</ControlLabel>
                      <FormControl
                        id="TRADE_PRICE_LIMIT_MIN"
                        value={this.props.settings["TRADE_PRICE_LIMIT_MIN"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Trade Price Limit Min"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trade Volume Limit Max</ControlLabel>
                      <FormControl
                        id="TRADE_VOLUME_LIMIT_MAX"
                        value={this.props.settings["TRADE_VOLUME_LIMIT_MAX"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Trade Volume Limit Max"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trade Volume Limit Min</ControlLabel>
                      <FormControl
                        id="TRADE_VOLUME_LIMIT_MIN"
                        value={this.props.settings["TRADE_VOLUME_LIMIT_MIN"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Trade Volume Limit Min"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trade Buy Max Limit</ControlLabel>
                      <FormControl
                        id="TRADE_BUY_MAX_LIMIT"
                        value={this.props.settings["TRADE_BUY_MAX_LIMIT"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Trade Buy Max Limit"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trade Buy Holding Limit</ControlLabel>
                      <FormControl
                        id="TRADE_BUY_HOLDING_LIMIT"
                        value={this.props.settings["TRADE_BUY_HOLDING_LIMIT"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Trade Buy Holding Limit"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trade Buy Strategy</ControlLabel>
                      <Select
                        placeholder="Trade Buy Strategy"
                        name="buyStrategy"
                        options={this.props.buyStrategyOptions}
                        value={this.props.selectedBuyStrategyOption}
                        id="TRADE_BUY_STRATEGY"
                        onChange={option =>
                          this.props.selectBuyStrategyOption(option)
                        }
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trade Sell Strategy</ControlLabel>
                      <Select
                        placeholder="Trade Sell Strategy"
                        name="sellStrategy"
                        options={this.props.sellStrategyOptions}
                        value={this.props.selectedSellStrategyOption}
                        id="TRADE_SELL_STRATEGY"
                        onChange={option =>
                          this.props.selectSellStrategyOption(option)
                        }
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trade Buy Order Type</ControlLabel>
                      <Select
                        placeholder="Trade Buy Order Type"
                        name="marketDataPlatform"
                        options={this.props.orderTypeOptions}
                        value={this.props.selectedBuyOrderTypeOption}
                        id="TRADE_BUY_ORDER_TYPE"
                        onChange={option =>
                          this.props.selectBuyOrderTypeOption(option)
                        }
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trade Sell Order Type</ControlLabel>
                      <Select
                        placeholder="Trade Sell Order Type"
                        name="marketDataPlatform"
                        options={this.props.orderTypeOptions}
                        value={this.props.selectedSellOrderTypeOption}
                        id="TRADE_SELL_ORDER_TYPE"
                        onChange={option =>
                          this.props.selectSellOrderTypeOption(option)
                        }
                      />
                    </FormGroup>
                  </form>
                }
              />
            </Col>
          </Row>
        </Grid>
        <div className="wizard-finish-button">
          <Button
            bsStyle="info"
            fill
            wd
            onClick={this.props.saveSettings}
            pullRight
          >
            Finish
          </Button>
        </div>
      </div>
    );
  }
}

export default FinalStep;
