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

var operatorMap = { "&": " and ", "|": " or ", "^": " xor " };
var operators = ["&", "|", "^"];

class StrategiesComp extends Component {
  constructor(props) {
    super(props);
    axiosGetWithAuth("/api/strategy/getStrategies")
      .then(handleResponse)
      .then(res => {
        this.props.onGetStrategiesSuccess(res);
      })
      .catch(error => {
        console.log(error);
        alert(error);
      });

    axiosGetWithAuth("/api/rule/getRules")
      .then(handleResponse)
      .then(res => {
        this.props.onGetRulesSuccess(res);
      })
      .catch(error => {
        console.log(error);
        alert(error);
      });
  }

  parseFormula(cell) {
    console.log(cell);
    let formula = cell.value;
    let tokens = formula
      .replace(",", "")
      .split(new RegExp("([" + operators.join("") + "])", "g"));
    let parsed = tokens.map(token => {
      if (operatorMap[token]) {
        return operatorMap[token];
      } else {
        let foundRule = this.props.rules.filter(rule => {
          return rule.id === parseInt(token);
        });

        return foundRule.length > 0
          ? "(" + foundRule[0].name + ")"
          : "(Unknown Rule)";
      }
    });
    let resultString = parsed.join(" ");
    return <span>{resultString}</span>;
  }

  render() {
    return (
      <div className="main-content">
        <Grid fluid>
          <Row>
            <Col md={12}>
              <Card
                title="Strategies"
                content={
                  <ReactTable
                    data={this.props.strategies}
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
                        accessor: "formula",
                        Cell: cell => this.parseFormula(cell)
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

export default StrategiesComp;
