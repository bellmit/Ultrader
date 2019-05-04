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

class RulesComp extends Component {
  constructor(props) {
    super(props);

    axiosGetWithAuth("/api/rule/getRules")
      .then(handleResponse)
      .then(res => {
        console.log(res);
        this.props.onGetRulesSuccess(res);
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
                title="Rules"
                content={
                  <ReactTable
                    data={this.props.rules}
                    filterable
                    columns={[
                      {
                        Header: "Name",
                        accessor: "name"
                      },
                      {
                        Header: "Description",
                        accessor: "description"
                      },
                      {
                        Header: "Type",
                        accessor: "type"
                      },
                      {
                        Header: "Formula",
                        accessor: "formula"
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

export default RulesComp;
