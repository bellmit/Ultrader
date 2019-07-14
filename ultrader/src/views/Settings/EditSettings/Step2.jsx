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
import Select from "react-select";

import Card from "components/Card/Card.jsx";

var tradingPlatformOptions = [
  { value: "Alpaca", label: "Alpaca" },
  { value: "AlpacaPaper", label: "Alpaca Paper" }
];

var marketDataPlatformOptions = [
  { value: "IEX", label: "The Investor Exchange" },
  { value: "Polygon", label: "Polygon API" }
];

var booleanOptions = [
  { value: "true", label: "true" },
  { value: "false", label: "false" }
];

class Step2 extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      exchangeMenuOpen: null,
      exchangeInputValue: null
    };
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
                      <ControlLabel>Trading Platform</ControlLabel>
                      <Select
                        placeholder="Trading Platform"
                        name="tradingPlatform"
                        options={tradingPlatformOptions}
                        value={this.props.selectedTradingPlatformOption}
                        id="GLOBAL_TRADING_PLATFORM"
                        onChange={option =>
                          this.props.selectTradingPlatformOption(option)
                        }
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Market Data Platform</ControlLabel>
                      <Select
                        placeholder="Market Data Platform"
                        name="marketDataPlatform"
                        options={marketDataPlatformOptions}
                        value={this.props.selectedMarketDataPlatformOption}
                        id="GLOBAL_MARKETDATA_PLATFORM"
                        onChange={option =>
                          this.props.selectMarketDataPlatformOption(option)
                        }
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>
                        Auto Trading Enabled (Default True)
                      </ControlLabel>
                      <Select
                        placeholder="Market Data Platform"
                        name="marketDataPlatform"
                        options={booleanOptions}
                        value={this.props.selectedAutoTradingOption}
                        id="GLOBAL_AUTO_TRADING_ENABLE"
                        onChange={option =>
                          this.props.selectAutoTradingOption(option)
                        }
                      />
                    </FormGroup>
                  </form>
                }
              />
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}

export default Step2;
