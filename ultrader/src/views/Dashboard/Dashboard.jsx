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

import { parseMoney, parsePercentage } from "helpers/ParseHelper";
import { axiosGetWithAuth} from "helpers/UrlHelper";

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
    this.getMonthlyTotalPortfolio();
    this.state = {
      totalPortfolioChart : {},
      totalPortfolioUpdateDate : new Date().toLocaleString()
    };
  }
  getTotalPortfolioChart(length, period) {
      axiosGetWithAuth("/api/chart/getPortfolio?length=" + length + "&period=" + period)
        .then(res => {
          this.setState({totalPortfolioChart : res.data, totalPortfolioUpdateDate : new Date().toLocaleString()});
        })
        .catch(error => {
          console.log(error);
          alert(error);
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
                        <i className="pe-7s-note2 text-primary" />
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
                            Withdrawable :{" "}
                            {parseMoney(this.props.portfolio.withdrawableCash)}
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
                        <i className="pe-7s-wallet text-success" />
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
                            Daily Profit :{" "}
                            {parseMoney(this.props.daily.netIncome)}
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
                            Performance :{" "}
                            {parsePercentage(this.props.performance.portfolio)}
                          </p>
                          <p>
                            Market :{" "}
                            {parsePercentage(this.props.performance.market)}
                          </p>
                          <p>
                            Comparison :{" "}
                            {parsePercentage(this.props.performance.comparison)}
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
                    options={ {
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
                              }
                            }
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
                    <Button bsSize="xs" onClick={this.getMonthlyTotalPortfolio}><i className="fa fa-history" /></Button> Updated at {this.state.totalPortfolioUpdateDate}
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
