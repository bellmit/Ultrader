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
  Collapse,
  Modal,
  ProgressBar
} from "react-bootstrap";

import Select from "react-select";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";

import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";
import { tooltip } from "helpers/TooltipHelper";
import { parseDate } from "helpers/ParseHelper";
import Datetime from "react-datetime";
import "react-datetime/css/react-datetime.css";

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
    this.generateSummary = this.generateSummary.bind(this);

    this.state = {
      showInputs: true,
      toggleText: "Hide Inputs",
      inTesting: false,
      length: 300,
      interval: 300,
      stocks: "AAPL",
      assetListOptions: [],
      buyStrategyOptions: [],
      sellStrategyOptions: [],
      selectedBuyStrategyOption: {},
      selectedSellStrategyOption: {},
      totalTrades: 0,
      totalStocks: 0,
      profitTradesRatio: 0.0,
      profitStockRatio: 0.0,
      holdingDays: 0.0,
      avgHoldingDays: 0.0,
      profitPerStock: 0.0,
      profitPerTrade: 0.0,
      totalProfitStrategy: 0.0,
      totalProfitHold: 0.0,
      amountPerTrade: 0.0,
      holdLimit: 0
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
    axiosGetWithAuth("/api/setting/getSettings")
      .then(response => {
        for (var i in response.data) {
          if (response.data[i].name === "TRADE_BUY_MAX_LIMIT") {
            this.state.amountPerTrade = response.data[i].value;
          }
          if (response.data[i].name === "TRADE_BUY_HOLDING_LIMIT") {
            this.state.holdLimit = response.data[i].value;
          }
        }
      })
      .catch(error => {
        alertError(error);
      });

    axiosGetWithAuth("/api/asset/getAssetLists")
      .then(response => {
        var assetLists = response.data;
        var assetListOptions = assetLists.map(assetList => {
          return { label: assetList.name, value: assetList.symbols };
        });

        this.setState({
          assetListOptions: assetListOptions
        });
      })
      .catch(error => {
        alertError(error);
      });
  }

  selectAssetListOption(option) {
    let selectedAssetListOption = option ? option : {};
    this.setState({
      selectedAssetListOption: selectedAssetListOption,
      stocks: option.value
    });
  }

  generateSummary(res) {
    var totalStocks = 0;
    var totalTrades = 0;
    var profitTradesRatio = 0.0;
    var profitStockRatio = 0.0;
    var holdingDays = 0.0;
    var avgHoldingDays = 0.0;
    var profitPerStock = 0.0;
    var profitPerTrade = 0.0;
    var totalProfitStrategy = 0.0;
    var totalProfitHold = 0.0;
    var hasTrade = 0;
    for (var i in res.data) {
      totalStocks += 1;
      if (res.data[i].tradingCount > 0) {
        hasTrade += 1;
        totalTrades += res.data[i].tradingCount;
        profitTradesRatio +=
          res.data[i].tradingCount * res.data[i].profitTradesRatio;
        if (res.data[i].averageHoldingDays > 0) {
          avgHoldingDays += res.data[i].averageHoldingDays;
        }
        profitPerTrade += res.data[i].totalProfit;
      }

      if (res.data[i].buyAndHold > 0) {
        profitStockRatio += 1;
      }
      var startDate = new Date(res.data[i].startDate);
      var endDate = new Date(res.data[i].endDate);
      if (endDate.getTime() - startDate.getTime() > holdingDays) {
        holdingDays = endDate.getTime() - startDate.getTime();
      }
      profitPerStock += res.data[i].buyAndHold;
    }

    this.state.totalStocks = totalStocks;
    this.state.totalTrades = totalTrades;
    this.state.profitTradesRatio =
      ((profitTradesRatio / totalTrades) * 100).toFixed(4) + "%";
    this.state.profitStockRatio =
      ((profitStockRatio / totalStocks) * 100).toFixed(4) + "%";
    this.state.holdingDays = Math.round(holdingDays / 24 / 3600 / 1000);
    this.state.avgHoldingDays = (avgHoldingDays / hasTrade).toFixed(1);
    this.state.profitPerTrade =
      ((profitPerTrade / totalTrades) * 100).toFixed(4) + "%";
    this.state.profitPerStock =
      ((profitPerStock / totalStocks) * 100).toFixed(4) + "%";
    this.state.totalProfitHold =
      ((profitPerStock / totalStocks) * 100).toFixed(4) + "%";

    var isPercentage = false;
    var amount = this.state.amountPerTrade + "";
    if (amount.indexOf("%") > 0) {
      amount = parseFloat(amount.substring(0, amount.length - 1));
      isPercentage = true;
    } else {
      amount = parseFloat(parseFloat(amount) / this.props.portfolio.value);
    }
    var holds = parseInt(this.state.holdLimit);
    this.state.totalProfitStrategy =
      (
        (Math.pow(
          ((profitPerTrade / totalTrades) * amount) / 100 + 1,
          Math.round(
            (this.state.holdingDays / this.state.avgHoldingDays) * holds
          )
        ) -
          1) *
        100
      ).toFixed(4) + "%";
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
    this.props.onBacktestStarted();

    axiosGetWithAuth(
      "/api/strategy/backtestByDate?" +
        "startDate=" +
        this.state.startDate +
        "&endDate=" +
        this.state.endDate +
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
        this.setState({ inTesting: false });
        this.generateSummary(res);
        this.props.onBacktestSuccess(res);
        this.toggleInputs(false);
      })
      .catch(error => {
        console.log(error);
        this.setState({ inTesting: false });
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

  search(e) {
    e.preventDefault();
    if (this.validate() && !this.state.inTesting) {
      this.setState({ inTesting: true });
      this.getBacktest();
    }
  }

  render() {
    return (
      <div className="main-content">
        <Modal show={this.state.inTesting} dialogClassName="modal-90w">
          <Modal.Header>
            <Modal.Title>Backtest: {this.props.progress.status}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <p>{this.props.progress.message}</p>
            <ProgressBar now={this.props.progress.progress} />
          </Modal.Body>
        </Modal>
        <Grid fluid>
          <Card
            content={
              <div>
                  <div  className={'alert alert-warning'}>
                  <strong>Warning!</strong>
                  <p>
                    Test longer date range or more assets will cost more system resource and impact the Ultrader functions if the memory is not sufficient.
                  </p>
                  <p>
                  Please choose appropriate date range and asset list and test on hosts which have bigger memory.
                  It's recommended to run the back test after the market is closed.
                  </p>
                  </div>

                <Collapse in={this.state.showInputs}>
                  <form onSubmit={this.search}>
                    <FormGroup>
                      <ControlLabel>Start Date {tooltip("Start date of the testing data")}</ControlLabel>
                      <Datetime
                        id="startDate"
                        inputProps={{ placeholder: "Test Start Date" }}
                        onChange={e => {
                          this.setState({ startDate: e.format() });
                        }}
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>End Date {tooltip("End date of the testing data")}</ControlLabel>
                      <Datetime
                        id="endDate"
                        inputProps={{ placeholder: "Test End Date" }}
                        onChange={e => {
                          this.setState({ endDate: e.format() });
                        }}
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trading Period {tooltip("Trading period used in the testing")}</ControlLabel>
                      <Select
                        placeholder="One bar represent how long"
                        name="intervalInput"
                        options={intervalOptions}
                        value={this.state.selectedIntervalOption}
                        id="intervalInput1"
                        onChange={option => this.selectIntervalOption(option)}
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Test Asset List {tooltip("Asset List will be used in the testing")}</ControlLabel>
                      <Select
                        placeholder="Choose a created Asset List"
                        name="tradingStockList"
                        options={this.state.assetListOptions}
                        value={this.state.selectedAssetListOption}
                        id="stocks"
                        onChange={option => this.selectAssetListOption(option)}
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Test Buy Strategy {tooltip("Buy strategy will be used in the testing")}</ControlLabel>
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
                      <ControlLabel>Test Sell Strategy {tooltip("Sell strategy will be used in the testing")}</ControlLabel>
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
                      fill
                      disabled={this.state.inTesting}
                      onClick={this.search.bind(this)}
                      color="info"
                      style={{ textAlign: "center" }}
                      type="submit">
                      Test
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
            <div>
              <Row>
                <Col md={6} xs={12}>
                  <Card
                    title="Trading Strategy Summary"
                    content={
                      <div>
                        <p>Total Trades{tooltip("Sum of trades during the testing")}: {this.state.totalTrades}</p>
                        <p>
                          Avg. Profitable Trades % {tooltip("Averagely, possibility of one trade can make profit")}:{" "}
                          {this.state.profitTradesRatio}
                        </p>
                        <p>
                          Avg. Holding Days/ Asset {tooltip("Average holding days for each asset")}: {this.state.avgHoldingDays}
                        </p>
                        <p>
                          Avg. Profit % / Trade {tooltip("Averagely, profit that one trade can gain")}: {this.state.profitPerTrade}
                        </p>
                        <p>
                          Expected Total Profit % {tooltip("Considering user current settings, such as how much spend on each trader and max position, the expect total profit can gain during the testing date range")}:{" "}
                          {this.state.totalProfitStrategy}
                        </p>
                      </div>
                    }
                  />
                </Col>
                <Col md={6} xs={12}>
                  <Card
                    title="Buy and Hold Summary"
                    content={
                      <div>
                        <p>Total Assets {tooltip("How many asset used in the testing")}: {this.state.totalStocks}</p>
                        <p>
                          Profitable Asset % {tooltip("If holding the asset, how many percentages asset will be profitable.")}: {this.state.profitStockRatio}
                        </p>
                        <p>Holding Days {tooltip("Holding days = testing data days")}: {this.state.holdingDays}</p>
                        <p>
                          Avg. Profit % / Asset {tooltip("The profit that one asset can gain, averagely")}: {this.state.profitPerStock}
                        </p>
                        <p>
                          Expected Total Profit % {tooltip("If spend same amount of money on each asset, the expected profit can gain.")}: {this.state.totalProfitHold}
                        </p>
                      </div>
                    }
                  />
                </Col>
              </Row>
              <Row>
                <Col md={12}>
                  <Card
                    title="Back Test Details"
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
                            Header: "Profitable Trades %",
                            accessor: "profitTradesRatio"
                          },
                          {
                            Header: "Reward/Risk %",
                            accessor: "rewardRiskRatio",
                            Cell: cell => parseFloat(cell.value).toFixed(6)
                          },
                          {
                            Header: "Buy and Hold %",
                            accessor: "buyAndHold",
                            Cell: cell => parseFloat(cell.value).toFixed(6)
                          },
                          {
                            Header: "Total Profit %",
                            accessor: "totalProfit",
                            Cell: cell => parseFloat(cell.value).toFixed(6)
                          },
                          {
                            Header: "Average Holding Days",
                            accessor: "averageHoldingDays",
                            Cell: cell => parseFloat(cell.value).toFixed(1)
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
            </div>
          )}
        </Grid>
      </div>
    );
  }
}

export default BacktestComp;
