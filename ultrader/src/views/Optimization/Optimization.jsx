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
import { parseDate,parseProfit } from "helpers/ParseHelper";
import Datetime from "react-datetime";
import "react-datetime/css/react-datetime.css";

var intervalOptions = [
  { value: "60", label: "1 Minute" },
  { value: "300", label: "5 Minutes" },
  { value: "900", label: "15 Minutes" },
  { value: "86400", label: "1 Day" }
];
var optimizationGoalOption = [
  { value: "AVG_PROFIT", label: "Average Profit Per Trade" },
  { value: "TOTAL_PROFIT", label: "Total Profit" }
];
class OptimizationComp extends Component {
  constructor(props) {
    super(props);

    this.getOptimization = this.getOptimization.bind(this);
    this.validate = this.validate.bind(this);
    this.search = this.search.bind(this);
    this.selectBuyStrategyOption = this.selectBuyStrategyOption.bind(this);
    this.selectSellStrategyOption = this.selectSellStrategyOption.bind(this);
    this.selectSellStrategyOption = this.selectSellStrategyOption.bind(this);
    this.initData = this.initData.bind(this);
    this.toggleInputs = this.toggleInputs.bind(this);
    this.showBestParameters = this.showBestParameters.bind(this);

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
      holdLimit: 0,
      optimizationGoal: "AVG_PROFIT"
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

  selectOptimizationGoalOption(option) {
    let selectedOptimizationGoalOption = option ? option : {};
    this.setState({
      selectedOptimizationGoalOption: selectedOptimizationGoalOption
    });
  }

  selectSellStrategyOption(option) {
    let selectedSellStrategyOption = option ? option : {};
    this.setState({
      selectedSellStrategyOption: selectedSellStrategyOption
    });
  }

  showBestParameters() {
    var table = []
    var parameters = this.props.optimization.bestParameters.parameters.split("|");
    var names = this.props.optimization.parameterNames.split("|");
    for (var i in names) {
        table.push(<p>{names[i]} : {parameters[i]}</p>);
    }
    return table;
  }

  getOptimization() {
    this.props.onOptimizationStarted();

    axiosGetWithAuth(
      "/api/strategy/optimizeStrategyByDate?" +
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
        this.state.selectedSellStrategyOption.value +
        "&maxIteration=" +
        this.state.iteration +
        "&optimizeGoal=" +
        this.state.selectedOptimizationGoalOption.value
    )
      .then(res => {
        this.setState({ inTesting: false });
        this.props.onOptimizationSuccess(res);
        this.toggleInputs(false);
      })
      .catch(error => {
        this.setState({ inTesting: false });
        if (error.indexOf("411")) {
          alertError("Insufficient training data, please add more assets or increase the data range.");
        } else {
          alertError(error);
        }

      });
  }

  validate() {
    if (
      this.state.startDate &&
      this.state.endDate &&
      this.state.selectedIntervalOption &&
      this.state.stocks &&
      this.state.selectedBuyStrategyOption &&
      this.state.selectedSellStrategyOption &&
      this.state.iteration &&
      this.state.selectedOptimizationGoalOption
    ) {
      return true;
    } else {
      alertError("Please fill all fields.");
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
      this.getOptimization();
    }
  }

  render() {
    return (
      <div className="main-content">
        <Modal show={this.state.inTesting} dialogClassName="modal-90w">
          <Modal.Header>
            <Modal.Title>
              Optimization: {this.props.progress.status}
            </Modal.Title>
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
                  <div className={'alert alert-warning'}>
                  <strong>Warning!</strong>
                  <p>
                    Use longer date range or more assets will cost more system resource and impact the Ultrader functions if the memory is not sufficient.
                  </p>
                  <p>
                  Please choose appropriate date range and asset list and optimize on hosts which have bigger memory.
                  It's recommended to run the optimization after the market is closed.
                  </p>
                  </div>
                <Collapse in={this.state.showInputs}>
                  <form onSubmit={this.search}>
                    <FormGroup>
                      <ControlLabel>Start Date {tooltip("Start date of the training data")}</ControlLabel>
                      <Datetime
                        id="startDate"
                        inputProps={{ placeholder: "Test Start Date" }}
                        onChange={e => {
                          this.setState({ startDate: e.format() });
                        }}
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>End Date {tooltip("End date of the training data")}</ControlLabel>
                      <Datetime
                        id="endDate"
                        inputProps={{ placeholder: "Test End Date" }}
                        onChange={e => {
                          this.setState({ endDate: e.format() });
                        }}
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Trading Period {tooltip("Trading period used in the training")}</ControlLabel>
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
                      <ControlLabel>Asset List {tooltip("Assets in the list will be regarded as the training data")}</ControlLabel>
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
                      <ControlLabel>Trade Buy Strategy {tooltip("Buy strategy will be used in the training")}</ControlLabel>
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
                      <ControlLabel>Trade Sell Strategy {tooltip("Sell strategy will be used in the training")}</ControlLabel>
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
                    <FormGroup>
                      <ControlLabel>Optimization Iteration {tooltip("The maximum iterations you want to optimize the parameter. Bigger number will cost more time to training and provide better result. Recommend value 10 - 20.")}</ControlLabel>
                      <FormControl
                        id="iteration"
                        value={this.state.iteration}
                        onChange={e => {
                           this.setState({ iteration: e.target.value });
                        }}
                        type="text"
                        placeholder="e.g. 10"
                    />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Optimization Goal{tooltip("The criterion of the optimization trying to maximize.")}</ControlLabel>
                      <Select
                        placeholder="Choose one optimization goal"
                        name="optimizationGoal"
                        options={optimizationGoalOption}
                        value={this.state.selectedOptimizationGoalOption}
                        id="optimizationGoalSelect"
                        onChange={option => this.selectOptimizationGoalOption(option)}

                      />
                    </FormGroup>
                    <Button
                      fill
                      disabled={this.state.inTesting}
                      onClick={this.search.bind(this)}
                      color="info"
                      style={{ textAlign: "center" }}
                      type="submit">
                      Optimize
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
          {this.props.optimization.results && this.props.optimization.results.length > 0 && (
            <div>
              <Row>
                <Col md={12} xs={12}>
                  <Card
                    title="Best Parameter Combination"
                    content={
                      <div>
                        <ReactTable
                        data={this.props.optimization.bestParameters}
                        filterable
                        columns={[
                          {
                            Header: "Strategy Name",
                            accessor: "strategyName"
                          },
                          {
                            Header: "Rule Name",
                            accessor: "ruleName"
                          },
                          {
                            Header: "Parameter Type",
                            accessor: "parameterType"
                          },
                          {
                            Header: "Value",
                            accessor: "value"
                          }
                        ]}
                        defaultPageSize={5}
                        showPaginationTop
                        showPaginationBottom={false}
                        className="-striped -highlight"
                      />
                      </div>
                    }
                  />
                </Col>
              </Row>
              <Row>
                <Col md={12}>
                  <Card
                    title="Optimization Details"
                    content={
                      <ReactTable
                        data={this.props.optimization.results}
                        filterable
                        columns={[
                          {
                            Header: "Iteration",
                            accessor: "iteration"
                          },
                          {
                            Header: this.state.selectedOptimizationGoalOption.label,
                            accessor: "optimizationGoal",
                            Cell: cell => parseFloat(cell.value).toFixed(6)
                          },
                          {
                            Header: this.props.optimization.parameterNames,
                            accessor: "parameters"
                          },
                          {
                            Header: "Trades",
                            accessor: "backtest.tradingCount"
                          },
                          {
                            Header: "Profitable Trades %",
                            accessor: "backtest.profitTradesRatio",
                            Cell: cell => parseFloat(cell.value).toFixed(6)
                          },
                          {
                            Header: "Reward/Risk %",
                            accessor: "backtest.rewardRiskRatio",
                            Cell: cell => parseFloat(cell.value).toFixed(6)
                          },
                          {
                            Header: "Buy and Hold %",
                            accessor: "backtest.buyAndHold",
                            Cell: cell => parseFloat(cell.value).toFixed(6)
                          },
                          {
                            Header: "Total Profit %",
                            accessor: "backtest.totalProfit",
                            Cell: cell => parseFloat(cell.value).toFixed(6)
                          },
                          {
                            Header: "Average Holding Days",
                            accessor: "backtest.averageHoldingDays",
                            Cell: cell => parseFloat(cell.value).toFixed(1)
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

export default OptimizationComp;
