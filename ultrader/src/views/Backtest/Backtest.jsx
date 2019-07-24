import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import {
  Grid,
  Row,
  Col,
  ControlLabel,
  FormControl,
  FormGroup,
  Collapse
} from "react-bootstrap";

import Select from "react-select";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";

import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";
import { parseDate } from "helpers/ParseHelper";
import Datetime from 'react-datetime';
import 'react-datetime/css/react-datetime.css';

var intervalOptions = [
  { value: "60", label: "1 Minute" },
  { value: "300", label: "5 Minutes" },
  { value: "900", label: "15 Minutes" },
  { value: "86400", label: "1 Day" }
];

class BacktestComp extends Component {
  constructor(props) {
    super(props);

    this.getBacktest = this.getBacktest.bind(this);
    this.validate = this.validate.bind(this);
    this.search = this.search.bind(this);
    this.selectBuyStrategyOption = this.selectBuyStrategyOption.bind(this);
    this.selectSellStrategyOption = this.selectSellStrategyOption.bind(this);
    this.selectSellStrategyOption = this.selectSellStrategyOption.bind(this);
    this.initData = this.initData.bind(this);
    this.toggleInputs = this.toggleInputs.bind(this);

    this.state = {
      showInputs: true,
      toggleText: "Hide Inputs",
      length: 300,
      interval: 300,
      stocks: "AAPL",
      buyStrategyOptions: [],
      sellStrategyOptions: [],
      selectedBuyStrategyOption: {},
      selectedSellStrategyOption: {}
    };
  }

  componentDidMount() {
    this.initData();
  }

  initData() {
    axiosGetWithAuth("/api/strategy/getStrategies")
      .then(response => {
        var strategies = response.data;
        var buyStrategyOptions = strategies
          .filter(strategy => {
            return strategy.type === "Buy";
          })
          .map(strategy => {
            return { label: strategy.name, value: strategy.id };
          });
        this.setState({
          buyStrategyOptions: buyStrategyOptions
        });
        var sellStrategyOptions = strategies
          .filter(strategy => {
            return strategy.type === "Sell";
          })
          .map(strategy => {
            return { label: strategy.name, value: strategy.id };
          });
        this.setState({
          sellStrategyOptions: sellStrategyOptions
        });
      })
      .catch(error => {
        alertError(error);
      });
  }

  selectBuyStrategyOption(option) {
    let selectedBuyStrategyOption = option ? option : {};
    this.setState({
      selectedBuyStrategyOption: selectedBuyStrategyOption
    });
  }

  selectIntervalOption(option) {
    let selectedIntervalOption = option ? option : {};
    this.setState({
      selectedIntervalOption: selectedIntervalOption
    });
  }

  selectSellStrategyOption(option) {
    let selectedSellStrategyOption = option ? option : {};
    this.setState({
      selectedSellStrategyOption: selectedSellStrategyOption
    });
  }

  getBacktest() {
    console.log(this.state);
    axiosGetWithAuth(
      "/api/strategy/backtestByDate?" +
        "startDate=" +
        this.state.startDate +
        "&endDate=" +
        this.state.endDate+
        "&interval=" +
        this.state.selectedIntervalOption.value +
        "&stocks=" +
        this.state.stocks +
        "&buyStrategyId=" +
        this.state.selectedBuyStrategyOption.value +
        "&sellStrategyId=" +
        this.state.selectedSellStrategyOption.value
    )
      .then(res => {
        console.log(res);
        this.props.onBacktestSuccess(res);
        this.toggleInputs(false);
      })
      .catch(error => {
        console.log(error);
        alertError(error);
      });
  }

  validate() {
    if (
      this.state.startDate &&
      this.state.endDate &&
      this.state.selectedIntervalOption &&
      this.state.stocks &&
      this.state.selectedBuyStrategyOption &&
      this.state.selectedSellStrategyOption
    ) {
      return true;
    } else {
      return false;
    }
  }

  toggleInputs(showInputs) {
    var showInputs = !this.state.showInputs;
    this.setState({
      showInputs: showInputs,
      toggleText: showInputs ? "Hide Inputs" : "Show Inputs"
    });
  }

  search() {
    if (this.validate()) {
      this.getBacktest();
    }
  }

  render() {
    return (
      <div className="main-content">
        <Grid fluid>
          <Card
            content={
              <div>
                <Collapse in={this.state.showInputs}>
                  <form>
                    <FormGroup>
                        <ControlLabel>Start Date</ControlLabel>
                        <Datetime
                            id="startDate"
                            inputProps={{placeholder:"Test Start Date"}}
                            onChange={e => {
                                this.setState({ startDate: e.format()});
                            }}
                        />
                    </FormGroup>
                    <FormGroup>
                        <ControlLabel>End Date</ControlLabel>
                        <Datetime
                            id="endDate"
                            inputProps={{placeholder:"Test End Date"}}
                            onChange={e => {
                                this.setState({ endDate: e.format()});
                            }}
                        />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Interval</ControlLabel>
                      <Select
                        placeholder="One bar represent how long"
                        name="intervalInput"
                        options={intervalOptions}
                        value={this.state.selectedIntervalOption}
                        id="intervalInput1"
                        onChange={option => this.selectIntervalOption(option)
                        }
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Stocks</ControlLabel>
                      <FormControl
                        id="stocks"
                        value={this.state.stocks}
                        onChange={e => {
                          this.setState({ stocks: e.target.value });
                        }}
                        type="text"
                        placeholder="AAPL,AMZN,etc."
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trade Buy Strategy</ControlLabel>
                      <Select
                        placeholder="Trade Buy Strategy"
                        name="buyStrategy"
                        options={this.state.buyStrategyOptions}
                        value={this.state.selectedBuyStrategyOption}
                        id="TRADE_BUY_STRATEGY"
                        onChange={option =>
                          this.selectBuyStrategyOption(option)
                        }
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trade Sell Strategy</ControlLabel>
                      <Select
                        placeholder="Trade Sell Strategy"
                        name="sellStrategy"
                        options={this.state.sellStrategyOptions}
                        value={this.state.selectedSellStrategyOption}
                        id="TRADE_SELL_STRATEGY"
                        onChange={option =>
                          this.selectSellStrategyOption(option)
                        }
                      />
                    </FormGroup>
                    <Button
                      onClick={this.search.bind(this)}
                      color="primary"
                      style={{ textAlign: "center" }}
                    >
                      Search
                    </Button>
                  </form>
                </Collapse>
                <div style={{ textAlign: "center" }}>
                  <Button
                    color="primary"
                    onClick={() => {
                      this.toggleInputs(!this.state.showInputs);
                    }}
                  >
                    {this.state.toggleText}
                  </Button>
                </div>
              </div>
            }
          />
          {this.props.results && this.props.results.length > 0 && (
            <Row>
              <Col md={12}>
                <Card
                  title="Backtest Results"
                  content={
                    <ReactTable
                      data={this.props.results}
                      filterable
                      columns={[
                        {
                          Header: "Stock",
                          accessor: "stock"
                        },
                        {
                          Header: "Trades",
                          accessor: "tradingCount"
                        },
                        {
                          Header: "Profit/Trades",
                          accessor: "profitTradesRatio"
                        },
                        {
                          Header: "Reward/Risk",
                          accessor: "rewardRiskRatio",
                          Cell: cell => parseFloat(cell.value).toFixed(6)
                        },
                        {
                          Header: "Buy vs Hold",
                          accessor: "vsBuyAndHold",
                          Cell: cell => parseFloat(cell.value).toFixed(6)
                        },
                        {
                          Header: "Total Profit",
                          accessor: "totalProfit",
                          Cell: cell => parseFloat(cell.value).toFixed(6)
                        },
                        {
                          Header: "Start Date",
                          accessor: "startDate",
                          Cell: cell =>
                            cell.value ? parseDate(cell.value) : ""
                        },
                        {
                          Header: "End Date",
                          accessor: "endDate",
                          Cell: cell =>
                            cell.value ? parseDate(cell.value) : ""
                        }
                      ]}
                      defaultPageSize={20}
                      showPaginationTop
                      showPaginationBottom={false}
                      className="-striped -highlight"
                    />
                  }
                />
              </Col>
            </Row>
          )}
        </Grid>
      </div>
    );
  }
}

export default BacktestComp;
