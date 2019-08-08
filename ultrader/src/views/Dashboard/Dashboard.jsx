import React, { Component } from "react";
import { Grid, Col, Row } from "react-bootstrap";
// react component used to create charts
import ChartistGraph from "react-chartist";
// react components used to create a SVG / Vector map
import { VectorMap } from "react-jvectormap";

import Card from "components/Card/Card.jsx";
import StatsCard from "components/Card/StatsCard.jsx";
import Tasks from "components/Tasks/Tasks.jsx";
import Button from "components/CustomButton/CustomButton.jsx";

import { parseMoney, parsePercentage, parseProfit } from "helpers/ParseHelper";
import { axiosGetWithAuth } from "helpers/UrlHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";

import {
  dataPie,
  dataSales,
  optionsSales,
  responsiveSales,
  dataBar,
  optionsBar,
  responsiveBar,
  table_data
} from "variables/Variables.jsx";

class DashboardComp extends Component {
  constructor(props) {
    super(props);

    this.getTotalPortfolioChart = this.getTotalPortfolioChart.bind(this);
    this.getMonthlyTotalPortfolio = this.getMonthlyTotalPortfolio.bind(this);
    this.getCards = this.getCards.bind(this);
    this.getMonthlyTotalPortfolio();
    this.getCards();
    this.state = {
      totalPortfolioChart: {},
      totalPortfolioUpdateDate: new Date().toLocaleString()
    };
  }
  getTotalPortfolioChart(length, period) {
    axiosGetWithAuth(
      "/api/chart/getPortfolio?length=" + length + "&period=" + period
    )
      .then(res => {
        this.setState({
          totalPortfolioChart: res.data,
          totalPortfolioUpdateDate: new Date().toLocaleString()
        });
      })
      .catch(error => {
        console.log(error);
        alertError(error);
      });
  }
  getCards() {
    axiosGetWithAuth("/api/notification/dashboard")
      .then(res => {
        this.props.portfolio.value = res.data.data.Portfolio;
        this.props.portfolio.buyingPower = res.data.data.BuyingPower;
        this.props.portfolio.change = res.data.data.Change;
        this.props.trades.buyCount = res.data.data.BuyCount;
        this.props.trades.sellCount = res.data.data.SellCount;
        this.props.trades.buyAmount = res.data.data.BuyAmount;
        this.props.trades.sellAmount = res.data.data.SellAmount;
        this.props.daily.netIncome = res.data.data.TotalProfit;
        this.props.daily.averageProfit = res.data.data.AverageProfit;
        this.props.daily.averageProfitRate = res.data.data.AverageProfitRatio;
        this.props.positions.holds = res.data.data.Holds;
        this.props.positions.profitStocks = res.data.data.ProfitableStock;
        this.props.positions.profit = res.data.data.Profit;
      })
      .catch(error => {
        console.log(error);
        alertError(error);
      });
  }
  getMonthlyTotalPortfolio() {
    this.getTotalPortfolioChart(30, 86400);
  }
  render() {
    return (
      <div className="main-content">
        <Grid fluid>
          <Row>
            <Col xl={3} lg={6} sm={6}>
              <div className="card card-stats">
                <div className="content">
                  <div
                    className="row"
                    style={{
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center"
                    }}
                  >
                    <div className="col-xs-2">
                      <div className="icon-big text-center icon-warning">
                        <i className="pe-7s-wallet text-primary" />
                      </div>
                    </div>
                    <div className="col-xs-10">
                      <div className="numbers">
                        <div>
                          <p>
                            Portfolio : {parseMoney(this.props.portfolio.value)}
                          </p>
                          <p>
                            Buying Power :{" "}
                            {parseMoney(this.props.portfolio.buyingPower)}
                          </p>
                          <p>
                            24H Change :{" "}
                            {parseProfit(this.props.portfolio.change, this.props.portfolio.value)}
                          </p>{" "}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </Col>
            <Col xl={3} lg={6} sm={6}>
              <div className="card card-stats">
                <div className="content">
                  <div
                    className="row"
                    style={{
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center"
                    }}
                  >
                    <div className="col-xs-2">
                      <div className="icon-big text-center icon-warning">
                        <i className="pe-7s-note2 text-success" />
                      </div>
                    </div>
                    <div className="col-xs-10">
                      <div className="numbers">
                        <div>
                          <p>
                            Buy/Sell Count :{" "}
                            {this.props.trades.buyCount +
                              "/" +
                              this.props.trades.sellCount}
                          </p>
                          <p>
                            Buy Amount :{" "}
                            {parseMoney(this.props.trades.buyAmount)}
                          </p>
                          <p>
                            Sell Amount :{" "}
                            {parseMoney(this.props.trades.sellAmount)}
                          </p>{" "}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </Col>
            <Col xl={3} lg={6} sm={6}>
              <div className="card card-stats">
                <div className="content">
                  <div
                    className="row"
                    style={{
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center"
                    }}
                  >
                    <div className="col-xs-2">
                      <div className="icon-big text-center icon-warning">
                        <i className="pe-7s-graph1 text-info" />
                      </div>
                    </div>
                    <div className="col-xs-10">
                      <div className="numbers">
                        <div>
                          <p>
                            Daily Total Profit :{" "}
                            {parseProfit(this.props.daily.netIncome, this.props.portfolio.value)}
                          </p>
                          <p>
                            Average Profit :{" "}
                            {parseMoney(this.props.daily.averageProfit)}
                          </p>
                          <p>
                            Average Profit % :{" "}
                            {parsePercentage(
                              this.props.daily.averageProfitRate
                            )}
                          </p>{" "}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </Col>
            <Col xl={3} lg={6} sm={6}>
              <div className="card card-stats">
                <div className="content">
                  <div
                    className="row"
                    style={{
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center"
                    }}
                  >
                    <div className="col-xs-2">
                      <div className="icon-big text-center icon-warning">
                        <i className="pe-7s-graph3 text-danger" />
                      </div>
                    </div>
                    <div className="col-xs-10">
                      <div className="numbers">
                        <div>
                          <p>
                            Positions :{" "}
                            {this.props.positions.holds}
                          </p>
                          <p>
                            Gain/Loss :{" "}
                            {this.props.positions.profitStocks + "/" + (this.props.positions.holds - this.props.positions.profitStocks)}
                          </p>
                          <p>
                            Position Profit :{" "}
                            {parseProfit(this.props.positions.profit, this.props.portfolio.value - this.props.portfolio.buyingPower)}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </Col>
          </Row>
          <Row>
            <Col md={12}>
              <Card
                title="Total Portfolio"
                category="30 Days"
                content={
                  <ChartistGraph
                    data={this.state.totalPortfolioChart}
                    type="Line"
                    options={{
                      showArea: false,
                      height: "245px",
                      axisX: {
                        showGrid: false
                      },
                      lineSmooth: true,
                      showLine: true,
                      showPoint: true,
                      fullWidth: true,
                      chartPadding: {
                        right: 50
                      }
                    }}
                    responsiveOptions={responsiveSales}
                  />
                }
                legend={
                  <div>
                    <i className="fa fa-circle text-info" /> Total Portfolio
                    Time
                  </div>
                }
                stats={
                  <div>
                    <Button bsSize="xs" onClick={this.getMonthlyTotalPortfolio}>
                      <i className="fa fa-history" />
                    </Button>{" "}
                    Updated at {this.state.totalPortfolioUpdateDate}
                  </div>
                }
              />
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}

export default DashboardComp;
