import React, { Component } from "react";
import { Grid, Col, Row, Carousel, Nav, NavItem, Tab} from "react-bootstrap";
import ChartistGraph from "react-chartist";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";

import { parseMoney, parsePercentage, parseProfit } from "helpers/ParseHelper";
import { axiosGetWithAuth } from "helpers/UrlHelper";
import { alertError } from "helpers/AlertHelper";
import { responsiveSales } from "variables/Variables.jsx";
import newsImg from "assets/img/news.png";
import "./../../css/news.css";

var portfolioChartOptions = {
    showArea: false,
    height: "245px",
    axisX: {showGrid: false},
    lineSmooth: true,
    showLine: true,
    showPoint: true,
    fullWidth: true,
    chartPadding: {right: 5}
    };
var profitChartOptions = {
    showArea: false,
    height: "245px",
    axisX: {showGrid: false},
    lineSmooth: true,
    showLine: true,
    showPoint: true,
    fullWidth: true,
    chartPadding: {right: 5}
    };
var tradeChartOptions = {
    showArea: false,
    height: "245px",
    axisX: {showGrid: false},
    lineSmooth: true,
    showLine: true,
    showPoint: true,
    fullWidth: true,
    chartPadding: {right: 5}
    };
class DashboardComp extends Component {
  constructor(props) {
    super(props);

    this.getTotalPortfolioChart = this.getTotalPortfolioChart.bind(this);
    this.refreshPortfolioChart = this.refreshPortfolioChart.bind(this);
    this.drawPortfolioChart = this.drawPortfolioChart.bind(this);
    this.getTotalProfitChart = this.getTotalProfitChart.bind(this);
    this.refreshProfitChart = this.refreshProfitChart.bind(this);
    this.drawProfitChart = this.drawProfitChart.bind(this);
    this.getTotalTradeChart = this.getTotalTradeChart.bind(this);
    this.refreshTradeChart = this.refreshTradeChart.bind(this);
    this.drawTradeChart = this.drawTradeChart.bind(this);
    this.getPortfolioPieChart = this.getPortfolioPieChart.bind(this);
    this.refreshPortfolioPieChart = this.refreshPortfolioPieChart.bind(this);
    this.drawPortfolioPieChart = this.drawPortfolioPieChart.bind(this);
    this.getCards = this.getCards.bind(this);
    this.getNews = this.getNews.bind(this);

    this.getCards();
    this.getNews();
    this.state = {
      totalPortfolioChart: {
         daily:{}, weekly:{}, monthly:{}, yearly:{}
      },
      totalPortfolioUpdateDate: new Date().toLocaleString(),
      portfolioPieChart: {
         Margin:{}, GainLoss:{}
      },
      portfolioPieUpdateDate: new Date().toLocaleString(),
      totalProfitChart: {
         daily:{}, weekly:{}, monthly:{}, yearly:{}
      },
      totalProfitUpdateDate: new Date().toLocaleString(),
      totalTradeChart: {
         daily:{}, weekly:{}, monthly:{}, yearly:{}
      },
      totalTradeUpdateDate: new Date().toLocaleString(),
      news: []
    };
    this.refreshPortfolioChart();
    this.refreshProfitChart();
    this.refreshTradeChart();
    this.refreshPortfolioPieChart();
  }

  getTotalPortfolioChart(length, step, period, key) {
    axiosGetWithAuth(
      "/api/chart/getPortfolio?length=" + length + "&period=" + period + "&step=" + step
    )
      .then(res => {
        this.state.totalPortfolioChart[key] = res.data;
        this.setState({
          totalPortfolioChart: this.state.totalPortfolioChart,
          totalPortfolioUpdateDate: new Date().toLocaleString()
        });
      })
      .catch(error => {
        console.log(error);
        alertError(error);
      });
  }
  getPortfolioPieChart(type) {
    axiosGetWithAuth(
      "/api/chart/getPortfolioPie?type=" + type
    )
      .then(res => {
        this.state.portfolioPieChart[type]['labels'] = res.data.labels;
        this.state.portfolioPieChart[type]['series'] = res.data.series[0];
        this.setState({
          portfolioPieChart: this.state.portfolioPieChart,
          portfolioPieUpdateDate: new Date().toLocaleString()
        });
      })
      .catch(error => {
        console.log(error);
        alertError(error);
      });
  }
  getTotalProfitChart(length, step, period, key) {
    axiosGetWithAuth(
      "/api/chart/getProfit?length=" + length + "&period=" + period + "&step=" + step
    )
      .then(res => {
        this.state.totalProfitChart[key] = res.data;
        this.setState({
          totalProfitChart: this.state.totalProfitChart,
          totalProfitUpdateDate: new Date().toLocaleString()
        });
      })
      .catch(error => {
        console.log(error);
        alertError(error);
      });
  }

  getTotalTradeChart(length, step, period, key) {
    axiosGetWithAuth(
      "/api/chart/getTrades?length=" + length + "&period=" + period + "&step=" + step
    )
      .then(res => {
        this.state.totalTradeChart[key] = res.data;
        this.setState({
          totalTradeChart: this.state.totalTradeChart,
          totalTradeUpdateDate: new Date().toLocaleString()
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
                    style={{ margin: "auto", maxHeight: 400, maxWidth: window.innerWidth < 1200 ? '100%' : 1200, backgroundRepeat: 'no-repeat',backgroundPosition: 'center', backgroundSize: 'cover',backgroundOrigin:'content' }}
                    src={newsItem.img}
                    alt="Test img"
                  />
                </Carousel.Item>
            ))}
         </Carousel>
      );
    }
  }

  refreshPortfolioChart() {
     this.getTotalPortfolioChart(180, 10, 172800, 'yearly');
     this.getTotalPortfolioChart(90, 6, 28800, 'monthly');
     this.getTotalPortfolioChart(84, 6, 7200, 'weekly');
     this.getTotalPortfolioChart(96, 8, 900, 'daily');
  }

  refreshProfitChart() {
     this.getTotalProfitChart(24, 2, 1296000, 'yearly');
     this.getTotalProfitChart(30, 2, 86400, 'monthly');
     this.getTotalProfitChart(28, 4, 21600, 'weekly');
     this.getTotalProfitChart(48, 2, 1800, 'daily');
  }

  refreshTradeChart() {
     this.getTotalTradeChart(24, 2, 1296000, 'yearly');
     this.getTotalTradeChart(30, 2, 86400, 'monthly');
     this.getTotalTradeChart(28, 4, 21600, 'weekly');
     this.getTotalTradeChart(48, 2, 1800, 'daily');
  }
  refreshPortfolioPieChart() {
     this.getPortfolioPieChart('Margin');
     this.getPortfolioPieChart('GainLoss');
  }
  drawPortfolioChart() {
    this.setState({
             totalPortfolioChart: this.state.totalPortfolioChart
            });
  }
  drawPortfolioPieChart() {
    this.setState({
             portfolioPieChart: this.state.portfolioPieChart
            });
  }

  drawProfitChart() {
    this.setState({
             totalProfitChart: this.state.totalProfitChart
            });
  }
  drawTradeChart() {
    this.setState({
             totalTradeChart: this.state.totalTradeChart
            });
  }
  render() {
    return (
      <div className="main-content">
        <Grid fluid>
          <Row>
              <Col xl={12} lg={12} md={12} sm={12}>
                  {this.appendNews()}
              </Col>
          </Row>
          <Row>
          <Col xl={4} lg={4} md={6} sm={12}>
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
            <Col xl={4} lg={4} md={6} sm={12}>
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

            <Col xl={4} lg={4} md={6} sm={12}>
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
            <Col xl={4} lg={4} md={6} sm={12}>
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
            <Col xl={4} lg={4} md={6} sm={12}>
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
            <Col xl={4} lg={4} md={6} sm={12}>
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
            <Col xl={6} lg={6} md={12} sm={12}>
              <Card
                title="Portfolio Change"
                category=""
                content={
                    <Tab.Container id="portfolioChart" defaultActiveKey="daily" ref="portfolioChart">
                        <Row className="clearfix">
                            <Col sm={12}>
                                <Nav bsStyle="tabs">
                                    <NavItem eventKey="daily">Daily</NavItem>
                                    <NavItem eventKey="weekly">Weekly</NavItem>
                                    <NavItem eventKey="monthly">Monthly</NavItem>
                                    <NavItem eventKey="yearly">Yearly</NavItem>
                                </Nav>
                            </Col>
                            <Col sm={12}>
                                <Tab.Content animation>
                                    <Tab.Pane eventKey="daily" onEnter={this.drawPortfolioChart}>
                                    <ChartistGraph
                                        data={this.state.totalPortfolioChart['daily']}
                                        type="Line"
                                        options={portfolioChartOptions}
                                        responsiveOptions={responsiveSales}
                                        />
                                    </Tab.Pane>
                                    <Tab.Pane eventKey="weekly" onEnter={this.drawPortfolioChart}>
                                    <ChartistGraph
                                        data={this.state.totalPortfolioChart['weekly']}
                                        type="Line"
                                        options={portfolioChartOptions}
                                        responsiveOptions={responsiveSales}
                                        />
                                    </Tab.Pane>
                                    <Tab.Pane eventKey="monthly" onEnter={this.drawPortfolioChart}>
                                    <ChartistGraph
                                        data={this.state.totalPortfolioChart['monthly']}
                                        type="Line"
                                        options={portfolioChartOptions}
                                        responsiveOptions={responsiveSales}
                                        />
                                    </Tab.Pane>
                                    <Tab.Pane eventKey="yearly" onEnter={this.drawPortfolioChart}>
                                    <ChartistGraph
                                        data={this.state.totalPortfolioChart['yearly']}
                                        type="Line"
                                        options={portfolioChartOptions}
                                        responsiveOptions={responsiveSales}
                                        />
                                    </Tab.Pane>
                                </Tab.Content>
                            </Col>
                        </Row>
                    </Tab.Container>
                }
                stats={
                  <div>
                    <Button bsSize="xs" onClick={this.refreshPortfolioChart}>
                      <i className="fa fa-history" />
                    </Button>{" "}
                    Updated at {this.state.totalPortfolioUpdateDate}
                  </div>
                }
              />
            </Col>
            <Col xl={6} lg={6} md={12} sm={12}>
              <Card
                title="Portfolio Pie Chart"
                category=""
                content={
                    <Tab.Container id="portfolioPieChart" defaultActiveKey="GainLoss" ref="portfolioPieChart">
                        <Row className="clearfix">
                            <Col sm={12}>
                                <Nav bsStyle="tabs">
                                    <NavItem eventKey="GainLoss">Gain / Loss</NavItem>
                                    <NavItem eventKey="Margin">Deposit / Loan</NavItem>
                                </Nav>
                            </Col>
                            <Col sm={12}>
                                <Tab.Content animation>
                                    <Tab.Pane eventKey="GainLoss" onEnter={this.drawPortfolioPieChart}>
                                    <ChartistGraph
                                        data={this.state.portfolioPieChart['GainLoss']}
                                        type="Pie"
                                        />
                                    </Tab.Pane>
                                    <Tab.Pane eventKey="Margin" onEnter={this.drawPortfolioPieChart}>
                                    <ChartistGraph
                                        data={this.state.portfolioPieChart['Margin']}
                                        type="Pie"
                                        />
                                    </Tab.Pane>

                                </Tab.Content>
                            </Col>
                        </Row>
                    </Tab.Container>
                }
                stats={
                  <div>
                    <Button bsSize="xs" onClick={this.refreshPortfolioPieChart}>
                      <i className="fa fa-history" />
                    </Button>{" "}
                    Updated at {this.state.portfolioPieUpdateDate}
                  </div>
                }
              />
            </Col>
            <Col xl={6} lg={6} md={12} sm={12}>
              <Card
                title="Aggregated Profit"
                category=""
                content={
                    <Tab.Container id="profitChart" defaultActiveKey="weekly" ref="profitChart">
                        <Row className="clearfix">
                            <Col sm={12}>
                                <Nav bsStyle="tabs">
                                    <NavItem eventKey="daily">Daily</NavItem>
                                    <NavItem eventKey="weekly">Weekly</NavItem>
                                    <NavItem eventKey="monthly">Monthly</NavItem>
                                    <NavItem eventKey="yearly">Yearly</NavItem>
                                </Nav>
                            </Col>
                            <Col sm={12}>
                                <Tab.Content animation>
                                    <Tab.Pane eventKey="daily" onEnter={this.drawProfitChart}>
                                    <ChartistGraph
                                        data={this.state.totalProfitChart['daily']}
                                        type="Bar"
                                        options={profitChartOptions}
                                        responsiveOptions={responsiveSales}
                                        />
                                    </Tab.Pane>
                                    <Tab.Pane eventKey="weekly" onEnter={this.drawProfitChart}>
                                    <ChartistGraph
                                        data={this.state.totalProfitChart['weekly']}
                                        type="Bar"
                                        options={profitChartOptions}
                                        responsiveOptions={responsiveSales}
                                        />
                                    </Tab.Pane>
                                    <Tab.Pane eventKey="monthly" onEnter={this.drawProfitChart}>
                                    <ChartistGraph
                                        data={this.state.totalProfitChart['monthly']}
                                        type="Bar"
                                        options={profitChartOptions}
                                        responsiveOptions={responsiveSales}
                                        />
                                    </Tab.Pane>
                                    <Tab.Pane eventKey="yearly" onEnter={this.drawProfitChart}>
                                    <ChartistGraph
                                        data={this.state.totalProfitChart['yearly']}
                                        type="Bar"
                                        options={profitChartOptions}
                                        responsiveOptions={responsiveSales}
                                        />
                                    </Tab.Pane>
                                </Tab.Content>
                            </Col>
                        </Row>
                    </Tab.Container>
                }
                stats={
                  <div>
                    <Button bsSize="xs" onClick={this.refreshProfitChart}>
                      <i className="fa fa-history" />
                    </Button>{" "}
                    Updated at {this.state.totalProfitUpdateDate}
                  </div>
                }
              />
            </Col>
            <Col xl={6} lg={6} md={12} sm={12}>
              <Card
                title="Aggregated Trades"
                category=""
                content={
                    <Tab.Container id="tradeChart" defaultActiveKey="weekly" ref="tradeChart">
                        <Row className="clearfix">
                            <Col sm={12}>
                                <Nav bsStyle="tabs">
                                    <NavItem eventKey="daily">Daily</NavItem>
                                    <NavItem eventKey="weekly">Weekly</NavItem>
                                    <NavItem eventKey="monthly">Monthly</NavItem>
                                    <NavItem eventKey="yearly">Yearly</NavItem>
                                </Nav>
                            </Col>
                            <Col sm={12}>
                                <Tab.Content animation>
                                    <Tab.Pane eventKey="daily" onEnter={this.drawTradeChart}>
                                    <ChartistGraph
                                        data={this.state.totalTradeChart['daily']}
                                        type="Bar"
                                        options={tradeChartOptions}
                                        responsiveOptions={responsiveSales}
                                        />
                                    </Tab.Pane>
                                    <Tab.Pane eventKey="weekly" onEnter={this.drawTradeChart}>
                                    <ChartistGraph
                                        data={this.state.totalTradeChart['weekly']}
                                        type="Bar"
                                        options={tradeChartOptions}
                                        responsiveOptions={responsiveSales}
                                        />
                                    </Tab.Pane>
                                    <Tab.Pane eventKey="monthly" onEnter={this.drawTradeChart}>
                                    <ChartistGraph
                                        data={this.state.totalTradeChart['monthly']}
                                        type="Bar"
                                        options={tradeChartOptions}
                                        responsiveOptions={responsiveSales}
                                        />
                                    </Tab.Pane>
                                    <Tab.Pane eventKey="yearly" onEnter={this.drawTradeChart}>
                                    <ChartistGraph
                                        data={this.state.totalTradeChart['yearly']}
                                        type="Bar"
                                        options={tradeChartOptions}
                                        responsiveOptions={responsiveSales}
                                        />
                                    </Tab.Pane>
                                </Tab.Content>
                            </Col>
                        </Row>
                    </Tab.Container>
                }
                stats={
                  <div>
                    <Button bsSize="xs" onClick={this.refreshTradeChart}>
                      <i className="fa fa-history" />
                    </Button>{" "}
                    Updated at {this.state.totalTradeUpdateDate}
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
