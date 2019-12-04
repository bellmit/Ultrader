import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import {
  Grid,
  Row,
  Col,
  ControlLabel,
  FormControl,
  FormGroup
} from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";

import { axiosGetWithAuth } from "helpers/UrlHelper";
import { tooltip } from "helpers/TooltipHelper";
import { parseMoney, parsePercentage, parseDate, parseProfit } from "helpers/ParseHelper";

class HistoryOrdersComp extends Component {
  constructor(props) {
    super(props);

    this.getHistoryOrders = this.getHistoryOrders.bind(this);
    this.validate = this.validate.bind(this);
    this.search = this.search.bind(this);
    this.getHoldDays = this.getHoldDays.bind(this);
    this.state = { days: 14 };
    this.getHistoryOrders(14);
  }

  getHistoryOrders(days) {
    axiosGetWithAuth("/api/order/getClosedOrders/" + days)
      .then(res => {
        this.props.onGetHistoryOrdersSuccess(res);
      })
      .catch(error => {
        console.log(error);
        alert(error);
      });
  }

  validate() {
    if (this.state.days) {
      return true;
    } else {
      return false;
    }
  }

  search() {
    if (this.validate()) {
      this.getHistoryOrders(this.state.days);
    }
  }

  getHoldDays(row) {
    return Math.round(
      (Date.parse(row.original.sellDate) - Date.parse(row.original.buyDate)) /
        (1000 * 60 * 60 * 24)
    );
  }

  render() {
    return (
      <div className="main-content">
        <Grid fluid>
          <Row style={{ padding: 20 }}>
            <Col md={10}>
              <Col componentClass={ControlLabel} sm={2}>
                Days:
              </Col>
              <Col sm={10}>
                <FormControl
                  value={this.state.days}
                  onChange={e => {
                    this.setState({ days: e.target.value });
                  }}
                  id="days"
                />
              </Col>
            </Col>
            <Col md={2}>
              <Button onClick={this.search.bind(this)} color="primary">
                Search
              </Button>
            </Col>
          </Row>
          {this.props.historyOrders && this.props.historyOrders.length > 0 && (
            <Row>
              <Col md={12}>
                <Card
                  title={<div>History Orders{tooltip("HistoryOrdersPage")}</div>}
                  content={
                    <ReactTable
                      data={this.props.historyOrders}
                      filterable
                      columns={[
                        {
                          Header: "Symbol",
                          accessor: "symbol"
                        },
                        {
                          Header: "Quantity",
                          accessor: "qty"
                        },
                        {
                          Header: "Buy Price",
                          accessor: "buyPrice",
                          Cell: row => parseMoney(row.value)
                        },
                        {
                          Header: "Sell Price",
                          accessor: "sellPrice",
                          Cell: row => parseMoney(row.value)
                        },
                        {
                          Header: "Buy Date",
                          accessor: "buyDate",
                          Cell: row => parseDate(row.original.buyDate)
                        },
                        {
                          Header: "Sell Date",
                          accessor: "sellDate",
                          Cell: row => parseDate(row.original.sellDate)
                        },
                        {
                          Header: "Hold Days",
                          Cell: row => this.getHoldDays(row)
                        },
                        {
                          Header: "Profit",
                          Cell: row => parseProfit(row.original.profit, row.original.buyPrice * row.original.qty)
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
          )}
        </Grid>
      </div>
    );
  }
}

export default HistoryOrdersComp;
