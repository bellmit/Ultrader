import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import { Grid, Row, Col } from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";
import axios from "axios";

import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { parseDate } from "helpers/ParseHelper";

var sampleResponse = {
  data: [
    {
      symbol: "NVDA",
      quantity: 28,
      averageCost: 171.09,
      buyDate: "2019-07-22T13:37:20.395+0000",
      currentPrice: 178.49,
      exchange: "NASDAQ",
      marketValue: 4997.72,
      changeToday: 0.0418515059537707
    },
    {
      symbol: "ULTA",
      quantity: 13,
      averageCost: 356.15,
      buyDate: "2019-07-22T17:14:03.122+0000",
      currentPrice: 352.99,
      exchange: "NASDAQ",
      marketValue: 4588.87,
      changeToday: 1.133305000708e-4
    },
    {
      symbol: "AVGO",
      quantity: 16,
      averageCost: 294.01,
      buyDate: "2019-07-22T13:32:40.491+0000",
      currentPrice: 301.62,
      exchange: "NASDAQ",
      marketValue: 4825.92,
      changeToday: 0.0184703697450616
    },
    {
      symbol: "PDFS",
      quantity: 369,
      averageCost: 13.51,
      buyDate: "2019-07-03T14:12:03.020+0000",
      currentPrice: 13.16,
      exchange: "NASDAQ",
      marketValue: 4856.04,
      changeToday: 0.0076569678407351
    },
    {
      symbol: "MYRG",
      quantity: 133,
      averageCost: 36.97,
      buyDate: "2019-07-10T16:15:23.798+0000",
      currentPrice: 36.6,
      exchange: "NASDAQ",
      marketValue: 4867.8,
      changeToday: 0.0090984284532672
    },
    {
      symbol: "TILE",
      quantity: 316,
      averageCost: 15.31,
      buyDate: "2019-06-27T19:59:16.909+0000",
      currentPrice: 15.47,
      exchange: "NASDAQ",
      marketValue: 4888.52,
      changeToday: 0.0238252812706817
    },
    {
      symbol: "SCZ",
      quantity: 84,
      averageCost: 57.34,
      buyDate: "2019-06-28T13:32:53.775+0000",
      currentPrice: 57.77,
      exchange: "NASDAQ",
      marketValue: 4852.68,
      changeToday: 0.0050452331245651
    },
    {
      symbol: "AMAG",
      quantity: 461,
      averageCost: 10.47,
      buyDate: "2019-05-17T16:25:55.478+0000",
      currentPrice: 8.64,
      exchange: "NASDAQ",
      marketValue: 3983.04,
      changeToday: -0.0181818181818182
    },
    {
      symbol: "FNKO",
      quantity: 228,
      averageCost: 21.62,
      buyDate: "2019-07-11T13:40:06.114+0000",
      currentPrice: 24.0,
      exchange: "NASDAQ",
      marketValue: 5472.0,
      changeToday: -0.0033222591362126
    },
    {
      symbol: "AXSM",
      quantity: 187,
      averageCost: 26.25,
      buyDate: "2019-07-22T15:22:40.599+0000",
      currentPrice: 25.8,
      exchange: "NASDAQ",
      marketValue: 4824.6,
      changeToday: -0.0193842645381984
    },
    {
      symbol: "MELI",
      quantity: 7,
      averageCost: 630.78,
      buyDate: "2019-07-23T18:41:13.324+0000",
      currentPrice: 631.92,
      exchange: "NASDAQ",
      marketValue: 4423.44,
      changeToday: -0.0072111985671867
    },
    {
      symbol: "ETSY",
      quantity: 73,
      averageCost: 67.53,
      buyDate: "2019-07-22T16:02:40.973+0000",
      currentPrice: 67.6,
      exchange: "NASDAQ",
      marketValue: 4934.8,
      changeToday: -0.0135706989639574
    },
    {
      symbol: "STRA",
      quantity: 26,
      averageCost: 188.27,
      buyDate: "2019-07-23T17:01:14.046+0000",
      currentPrice: 186.67,
      exchange: "NASDAQ",
      marketValue: 4853.42,
      changeToday: 0.0030089731878996
    },
    {
      symbol: "LRCX",
      quantity: 23,
      averageCost: 208.78,
      buyDate: "2019-07-23T16:41:31.902+0000",
      currentPrice: 211.96,
      exchange: "NASDAQ",
      marketValue: 4875.08,
      changeToday: 0.0229236040731625
    },
    {
      symbol: "CDNS",
      quantity: 67,
      averageCost: 72.84,
      buyDate: "2019-07-23T13:40:11.340+0000",
      currentPrice: 74.27,
      exchange: "NASDAQ",
      marketValue: 4976.09,
      changeToday: 0.0221579961464355
    },
    {
      symbol: "TEAM",
      quantity: 36,
      averageCost: 136.09,
      buyDate: "2019-07-22T15:08:00.564+0000",
      currentPrice: 134.0,
      exchange: "NASDAQ",
      marketValue: 4824.0,
      changeToday: -0.0094618568894145
    },
    {
      symbol: "NOVT",
      quantity: 56,
      averageCost: 86.58,
      buyDate: "2019-07-12T13:56:54.300+0000",
      currentPrice: 86.64,
      exchange: "NASDAQ",
      marketValue: 4851.84,
      changeToday: -0.0064220183486239
    },
    {
      symbol: "ICPT",
      quantity: 73,
      averageCost: 67.57,
      buyDate: "2019-07-23T15:35:31.753+0000",
      currentPrice: 67.04,
      exchange: "NASDAQ",
      marketValue: 4893.92,
      changeToday: -1.491424310216e-4
    },
    {
      symbol: "ISRG",
      quantity: 9,
      averageCost: 533.33,
      buyDate: "2019-07-22T15:25:20.592+0000",
      currentPrice: 537.79,
      exchange: "NASDAQ",
      marketValue: 4840.11,
      changeToday: 0.0042763772175537
    },
    {
      symbol: "SWAV",
      quantity: 96,
      averageCost: 51.49,
      buyDate: "2019-07-23T18:55:14.981+0000",
      currentPrice: 51.8,
      exchange: "NASDAQ",
      marketValue: 4972.8,
      changeToday: 0.0339321357285429
    }
  ]
};

class PositionsComp extends Component {
  constructor(props) {
    super(props);
    this.getProfitDisplay = this.getProfitDisplay.bind(this);
  }

  componentDidMount() {
    axiosGetWithAuth("/api/position/getPositions")
      .then(res => {
        this.props.onGetPositionsSuccess(res);
      })
      .catch(error => {
        console.log(error);
        alert(error);
      });
  }

  getProfitDisplay(cell) {
    let row = cell.original;
    if (row) {
      var color = row.profit > 0 ? "green" : "red";
      var profitPercentText = (row.profitPercent * 100).toFixed(2) + "%";
      return (
        <span style={{ color: color }}>
          {row.profit + " (" + profitPercentText + ")"}
        </span>
      );
    }
  }

  render() {
    return (
      <div className="main-content">
        <Grid fluid>
          <Row>
            <Col md={12}>
              <Card
                title="Positions"
                content={
                  <ReactTable
                    data={this.props.positions}
                    filterable
                    columns={[
                      {
                        Header: "Symbol",
                        accessor: "symbol"
                      },
                      {
                        Header: "Quantity",
                        accessor: "quantity"
                      },
                      {
                        Header: "Average Cost",
                        accessor: "averageCost"
                      },
                      {
                        Header: "Current Price",
                        accessor: "currentPrice"
                      },
                      {
                        Header: "Profit",
                        accessor: "profitPercent",
                        Cell: cell => this.getProfitDisplay(cell)
                      },
                      {
                        Header: "Buy Date",
                        accessor: "buyDate",
                        Cell: cell => parseDate(cell.value)
                      },
                      {
                        Header: "Hold Days",
                        accessor: "holdDays",
                        width: 100
                      },
                      {
                        Header: "Exchange",
                        accessor: "exchange",
                        width: 100
                      }
                    ]}
                    defaultPageSize={10}
                    showPaginationTop
                    showPaginationBottom={false}
                    className="-striped -highlight"
                  />
                }
              />
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}

export default PositionsComp;
