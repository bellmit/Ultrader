import React, { Component } from "react";
import { Grid, Col, Row, Carousel } from "react-bootstrap";
import ChartistGraph from "react-chartist";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";

import { parseMoney, parsePercentage, parseProfit } from "helpers/ParseHelper";
import { axiosGetWithAuth } from "helpers/UrlHelper";
import { alertError } from "helpers/AlertHelper";
import { responsiveSales } from "variables/Variables.jsx";
import newsImg from "assets/img/news.png";
import "./../../css/news.css";

class DashboardComp extends Component {
  constructor(props) {
    super(props);

    this.getTotalPortfolioChart = this.getTotalPortfolioChart.bind(this);
    this.getMonthlyTotalPortfolio = this.getMonthlyTotalPortfolio.bind(this);
    this.getCards = this.getCards.bind(this);
    this.getNews = this.getNews.bind(this);
    this.getMonthlyTotalPortfolio();
    this.getCards();
    this.getNews();
    this.state = {
      totalPortfolioChart: {},
      totalPortfolioUpdateDate: new Date().toLocaleString(),
      news: []
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
        console.log(res);
        this.props.portfolio.value = res.data.data.Portfolio;
        this.props.portfolio.buyingPower = res.data.data.BuyingPower;
        this.props.portfolio.change = res.data.data.Change;
        this.props.trades.buyCount = res.data.data.BuyCount;
        this.props.trades.sellCount = res.data.data.SellCount;
        this.props.trades.buyAmount = res.data.data.BuyAmount;
        this.props.trades.sellAmount = res.data.data.SellAmount;

        this.props.profit["1"].PeriodDays =  JSON.parse(res.data.data["1"]).PeriodDays;
        this.props.profit["1"].TotalTrades =  JSON.parse(res.data.data["1"]).TotalTrades;
        this.props.profit["1"].TotalProfit =  JSON.parse(res.data.data["1"]).TotalProfit;
        this.props.profit["1"].AverageProfit =  JSON.parse(res.data.data["1"]).AverageProfit;
        this.props.profit["1"].AverageProfitRatio =  JSON.parse(res.data.data["1"]).AverageProfitRatio;
        
        this.props.profit["7"].PeriodDays =  JSON.parse(res.data.data["7"]).PeriodDays;
        this.props.profit["7"].TotalTrades =  JSON.parse(res.data.data["7"]).TotalTrades;
        this.props.profit["7"].TotalProfit =  JSON.parse(res.data.data["7"]).TotalProfit;
        this.props.profit["7"].AverageProfit =  JSON.parse(res.data.data["7"]).AverageProfit;
        this.props.profit["7"].AverageProfitRatio =  JSON.parse(res.data.data["7"]).AverageProfitRatio;
        
        this.props.profit["30"].PeriodDays =  JSON.parse(res.data.data["30"]).PeriodDays;
        this.props.profit["30"].TotalTrades =  JSON.parse(res.data.data["30"]).TotalTrades;
        this.props.profit["30"].TotalProfit =  JSON.parse(res.data.data["30"]).TotalProfit;
        this.props.profit["30"].AverageProfit =  JSON.parse(res.data.data["30"]).AverageProfit;
        this.props.profit["30"].AverageProfitRatio =  JSON.parse(res.data.data["30"]).AverageProfitRatio;
        
        this.props.profit["365"].PeriodDays =  JSON.parse(res.data.data["365"]).PeriodDays;
        this.props.profit["365"].TotalTrades =  JSON.parse(res.data.data["365"]).TotalTrades;
        this.props.profit["365"].TotalProfit =  JSON.parse(res.data.data["365"]).TotalProfit;
        this.props.profit["365"].AverageProfit =  JSON.parse(res.data.data["365"]).AverageProfit;
        this.props.profit["365"].AverageProfitRatio =  JSON.parse(res.data.data["365"]).AverageProfitRatio;
        
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

  getNews() {
    axiosGetWithAuth("/api/news/getNewsList?num=3")
      .then(res => {
        this.setState({
          news: res.data
        });
      })
      .catch(error => {
        console.log(error);
      });
  }

  appendNews() {
    if (this.state.news.length > 0) {
      return (
        <Row>
          <Col sm={12}>
            <Carousel interval={5000} className="card card-stats">
              {this.state.news.map(newsItem => (
                <Carousel.Item
                  key={newsItem.newsId}
                  onClick={() => {
                    window.open(newsItem.url);
                  }}
                  style={{ cursor: "pointer" }}
                >
                  <img
                    style={{ margin: "auto", minHeight: 100, width: "100%" }}
                    src={newsItem.img}
                    alt="Test img"
                  />
                  <Carousel.Caption>
                    <h4 style={{ marginTop: 10 }}>{newsItem.title}</h4>
                  </Carousel.Caption>
                </Carousel.Item>
              ))}
            </Carousel>
          </Col>
        </Row>
      );
    } else {
      return <Row />;
    }
  }

  render() {
    return (
      <div className="main-content">
        <Grid fluid>
          {this.appendNews()}
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
                            {parseProfit(
                              this.props.portfolio.change,
                              this.props.portfolio.value
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
                          <p>Positions : {this.props.positions.holds}</p>
                          <p>
                            Gain/Loss :{" "}
                            {this.props.positions.profitStocks +
                              "/" +
                              (this.props.positions.holds -
                                this.props.positions.profitStocks)}
                          </p>
                          <p>
                            Position Profit :{" "}
                            {parseProfit(
                              this.props.positions.profit,
                              this.props.portfolio.value
                            )}
                          </p>
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
                            {parseProfit(
                              this.props.profit["1"].totalProfit,
                              this.props.portfolio.value
                            )}
                          </p>
                          <p>
                            Daily Total Trades :{" "}
                            {this.props.profit["1"].TotalTrades}
                          </p>
                          <p>
                            Daily Average Profit % :{" "}
                            {parsePercentage(
                              this.props.profit["1"].averageProfitRatio
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
                        <i className="pe-7s-graph1 text-success" />
                      </div>
                    </div>
                    <div className="col-xs-10">
                      <div className="numbers">
                        <div>
                          <p>
                            Weekly Total Profit :{" "}
                            {parseProfit(
                              this.props.profit["7"].totalProfit,
                              this.props.portfolio.value
                            )}
                          </p>
                          <p>
                            Weekly Total Trades :{" "}
                            {this.props.profit["7"].TotalTrades}
                          </p>
                          <p>
                            Weekly Average Profit % :{" "}
                            {parsePercentage(
                              this.props.profit["7"].averageProfitRatio
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
                        <i className="pe-7s-graph1 text-warning" />
                      </div>
                    </div>
                    <div className="col-xs-10">
                      <div className="numbers">
                        <div>
                          <p>
                            Monthly Total Profit :{" "}
                            {parseProfit(
                              this.props.profit["30"].totalProfit,
                              this.props.portfolio.value
                            )}
                          </p>
                          <p>
                            Monthly Total Trades :{" "}
                            {this.props.profit["30"].TotalTrades}
                          </p>
                          <p>
                            Monthly Average Profit % :{" "}
                            {parsePercentage(
                              this.props.profit["30"].averageProfitRatio
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
                        <i className="pe-7s-graph1 text-danger" />
                      </div>
                    </div>
                    <div className="col-xs-10">
                      <div className="numbers">
                        <div>
                          <p>
                            Yearly Total Profit :{" "}
                            {parseProfit(
                              this.props.profit["365"].totalProfit,
                              this.props.portfolio.value
                            )}
                          </p>
                          <p>
                            Yearly Total Trades :{" "}
                            {this.props.profit["365"].TotalTrades}
                          </p>
                          <p>
                            Yearly Average Profit % :{" "}
                            {parsePercentage(
                              this.props.profit["365"].averageProfitRatio
                            )}
                          </p>{" "}
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
