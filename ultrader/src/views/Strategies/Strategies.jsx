import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import { Grid, Row, Col, Modal } from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";
import axios from "axios";

import AddStrategy from "containers/Strategies/AddStrategy.jsx";

import { axiosGetWithAuth, axiosDeleteWithAuth } from "helpers/UrlHelper";

var operatorMap = { "&": " and ", "|": " or ", "^": " xor " };
var operators = ["&", "|", "^"];

class StrategiesComp extends Component {
  constructor(props) {
    super(props);
    axiosGetWithAuth("/api/strategy/getStrategies")
      .then(res => {
        this.props.onGetStrategiesSuccess(res);
      })
      .catch(error => {
        console.log(error);
        alert(error);
      });

    axiosGetWithAuth("/api/rule/getRules")
      .then(res => {
        this.props.onGetRulesSuccess(res);
      })
      .catch(error => {
        console.log(error);
        alert(error);
      });
    this.handleShow = this.handleShow.bind(this);
    this.handleClose = this.handleClose.bind(this);
    this.deleteStrategy = this.deleteStrategy.bind(this);

    this.state = {
      show: false
    };
  }

  parseFormula(cell) {
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

  handleClose() {
    this.setState({ show: false });
  }

  handleShow() {
    this.setState({ show: true });
  }

  deleteStrategy(row) {
    let id = row.original.id;
    let index = row.index;
    axiosDeleteWithAuth("/api/strategy/deleteStrategy/" + id)
      .then(res => {
        alert("Deleted strategy successfully.");
        this.props.onDeleteStrategySuccess(index);
      })
      .catch(error => {
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
                title={
                  <div>
                    Strategies
                    <Button
                      className="add_button"
                      variant="primary"
                      onClick={this.handleShow}
                    >
                      Add Strategy
                    </Button>
                  </div>
                }
                content={
                  <div>
                    <Modal
                      show={this.state.show}
                      onHide={this.handleClose}
                      dialogClassName="modal-90w"
                    >
                      <Modal.Header closeButton />
                      <Modal.Body>
                        <AddStrategy />
                      </Modal.Body>
                    </Modal>
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
                        },
                        {
                          Header: "Actions",
                          width: 100,
                          style: {
                            textAlign: "center"
                          },
                          Cell: row => (
                            <Button
                              onClick={() => {
                                this.deleteStrategy(row);
                              }}
                              bsStyle="danger"
                              simple
                              icon
                            >
                              <i className="fa fa-times" />
                            </Button>
                          ),
                          sortable: false,
                          filterable: false
                        }
                      ]}
                      defaultPageSize={10}
                      showPaginationTop
                      showPaginationBottom={false}
                      className="-striped -highlight"
                    />
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

export default StrategiesComp;
