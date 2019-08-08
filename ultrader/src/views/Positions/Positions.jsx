import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import { Grid, Row, Col } from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";
import axios from "axios";

import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { parseDate, parseProfit } from "helpers/ParseHelper";

class PositionsComp extends Component {
  constructor(props) {
    super(props);
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
                        Cell: cell => parseProfit((cell.original.currentPrice - cell.original.averageCost) * cell.original.quantity, cell.original.averageCost * cell.original.quantity)
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
