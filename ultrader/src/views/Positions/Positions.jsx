import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import { Grid, Row, Col } from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";
import axios from "axios";

import {
  axiosGetWithAuth,
  axiosPostWithAuth,
  handleResponse
} from "helpers/UrlHelper";

class PositionsComp extends Component {
  constructor(props) {
    super(props);
    axiosGetWithAuth("/api/position/getPositions")
      .then(handleResponse)
      .then(res => {
        console.log(res);
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

export default PositionsComp;
