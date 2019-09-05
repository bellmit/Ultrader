import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import { Grid, Row, Col } from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";
import axios from "axios";

import { confirmAlert } from "react-confirm-alert"; // Import
import "react-confirm-alert/src/react-confirm-alert.css"; // Import css

import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { parseDate, parseProfit } from "helpers/ParseHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";
class PositionsComp extends Component {
  constructor(props) {
    super(props);

    this.state = { selected: {}, selectAll: 0 };

    this.toggleRow = this.toggleRow.bind(this);
    this.manualSell = this.manualSell.bind(this);
    this.loadData = this.loadData.bind(this);
  }

  loadData() {
    axiosGetWithAuth("/api/position/getPositions")
      .then(res => {
        this.props.onGetPositionsSuccess(res);
      })
      .catch(error => {
        console.log(error);
        alert(error);
      });
  }

  componentDidMount() {
    this.loadData();
  }

  manualSell() {
    var selectedPositions = this.props.positions.filter(p => {
      return this.state.selected[p.symbol];
    });
    if (selectedPositions.length > 0) {
      var symbols = selectedPositions
        .map(p => {
          return p.symbol;
        })
        .join(",");
      confirmAlert({
        title: "",
        message: "Confirm to sell: " + symbols,
        buttons: [
          {
            label: "Yes",
            onClick: () => {
              axiosPostWithAuth("/api/order/liquid?assets=" + symbols)
                .then(res => {
                      alertSuccess("Successfully sent the request to sell the selected assets: " + symbols);
                      this.loadData();
                       })
                .catch(error => { alertError(error);});

            }
          },
          {
            label: "No"
          }
        ]
      });
    }
  }

  toggleRow(symbol) {
    const newSelected = Object.assign({}, this.state.selected);
    newSelected[symbol] = !this.state.selected[symbol];
    this.setState({
      selected: newSelected,
      selectAll: 2
    });
  }

  toggleSelectAll() {
    let newSelected = {};

    if (this.state.selectAll === 0) {
      this.props.positions.forEach(x => {
        newSelected[x.symbol] = true;
      });
    }

    this.setState({
      selected: newSelected,
      selectAll: this.state.selectAll === 0 ? 1 : 0
    });
  }

  render() {
    return (
      <div className="main-content">
        <Grid fluid>
          <Row>
            <Col md={12}>
              <Card
                title={
                  <div>
                    Positions
                    <Button
                      className="add_button"
                      variant="primary"
                      onClick={this.manualSell}
                    >
                      Liquid Selected Assets
                    </Button>
                  </div>
                }
                content={
                  <ReactTable
                    data={this.props.positions}
                    filterable
                    columns={[
                      {
                        id: "checkbox",
                        accessor: "",
                        Cell: cell => {
                          return (
                            <input
                              type="checkbox"
                              className="checkbox"
                              checked={
                                this.state.selected[cell.original.symbol] ===
                                true
                              }
                              onChange={() =>
                                this.toggleRow(cell.original.symbol)
                              }
                            />
                          );
                        },
                        Header: x => {
                          return (
                            <input
                              type="checkbox"
                              className="checkbox"
                              checked={this.state.selectAll === 1}
                              ref={input => {
                                if (input) {
                                  input.indeterminate =
                                    this.state.selectAll === 2;
                                }
                              }}
                              onChange={() => this.toggleSelectAll()}
                            />
                          );
                        },
                        sortable: false,
                        filterable: false,
                        width: 45
                      },
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
                        Cell: cell =>
                          parseProfit(
                            (cell.original.currentPrice -
                              cell.original.averageCost) *
                              cell.original.quantity,
                            cell.original.averageCost * cell.original.quantity
                          )
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
