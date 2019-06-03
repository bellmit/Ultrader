import React, { Component } from "react";
import { Grid, Col, Row } from "react-bootstrap";
// react component used to create charts
import ChartistGraph from "react-chartist";
// react components used to create a SVG / Vector map
import { VectorMap } from "react-jvectormap";

import Card from "components/Card/Card.jsx";
import StatsCard from "components/Card/StatsCard.jsx";
import Tasks from "components/Tasks/Tasks.jsx";

import { parseMoney, parsePercentage } from "helpers/ParseHelper";

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

var mapData = {
  AU: 760,
  BR: 550,
  CA: 120,
  DE: 1300,
  FR: 540,
  GB: 690,
  GE: 200,
  IN: 200,
  RO: 600,
  RU: 300,
  US: 2920
};

class DashboardComp extends Component {
  createTableData() {
    var tableRows = [];
    for (var i = 0; i < table_data.length; i++) {
      tableRows.push(
        <tr key={i}>
          <td>
            <div className="flag">
              <img src={table_data[i].flag} alt="us_flag" />
            </div>
          </td>
          <td>{table_data[i].country}</td>
          <td className="text-right">{table_data[i].count}</td>
          <td className="text-right">{table_data[i].percentage}</td>
        </tr>
      );
    }
    return tableRows;
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
            <Col md={6}>
              <Card
                title="Users Behavior"
                category="24 Hours performance"
                content={
                  <ChartistGraph
                    data={dataSales}
                    type="Line"
                    options={optionsSales}
                    responsiveOptions={responsiveSales}
                  />
                }
                legend={
                  <div>
                    <i className="fa fa-circle text-info" /> Open
                    <i className="fa fa-circle text-danger" /> Click
                    <i className="fa fa-circle text-warning" /> Click Second
                    Time
                  </div>
                }
                stats={
                  <div>
                    <i className="fa fa-history" /> Updated 3 minutes ago
                  </div>
                }
              />
            </Col>
            <Col md={6}>
              <Card
                title="Users Behavior"
                category="24 Hours performance"
                content={
                  <ChartistGraph
                    data={dataSales}
                    type="Line"
                    options={optionsSales}
                    responsiveOptions={responsiveSales}
                  />
                }
                legend={
                  <div>
                    <i className="fa fa-circle text-info" /> Open
                    <i className="fa fa-circle text-danger" /> Click
                    <i className="fa fa-circle text-warning" /> Click Second
                    Time
                  </div>
                }
                stats={
                  <div>
                    <i className="fa fa-history" /> Updated 3 minutes ago
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
