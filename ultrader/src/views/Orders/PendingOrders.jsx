import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import { Grid, Row, Col } from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";
import axios from "axios";
import { tooltip } from "helpers/TooltipHelper";
import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { parseSymbolGraphModal } from "helpers/ParseHelper";

class PendingOrdersComp extends Component {
  constructor(props) {
    super(props);
    axiosGetWithAuth("/api/order/getOpenOrders")
      .then(res => {
        this.props.onGetPendingOrdersSuccess(res);
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
                title={<div>Pending Orders{tooltip("PendingOrdersPage")}</div>}
                content={
                  <ReactTable
                    data={this.props.pendingOrders}
                    filterable
                    columns={[
                      {
                        Header: "Symbol",
                        accessor: "symbol",
                        Cell: cell => parseSymbolGraphModal(cell.value)
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
                        Header: "Buy Date",
                        accessor: "buyDate"
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

export default PendingOrdersComp;
