import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import { Grid, Row, Col, Modal } from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import PrivateButton from "components/CustomButton/CustomPrivateButton.jsx";
import axios from "axios";

import AddStrategy from "containers/Strategies/AddStrategy.jsx";
import EditStrategy from "containers/Strategies/EditStrategy.jsx";
import { tooltip } from "helpers/TooltipHelper";
import { axiosGetWithAuth, axiosDeleteWithAuth } from "helpers/UrlHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";

var operatorMap = { "&": " and ", "|": " or ", "^": " xor " };
var operators = ["&", "|", "^"];

class StrategiesComp extends Component {
  constructor(props) {
    super(props);

    this.handleShowAdd = this.handleShowAdd.bind(this);
    this.handleCloseAdd = this.handleCloseAdd.bind(this);
    this.handleShowEdit = this.handleShowEdit.bind(this);
    this.handleCloseEdit = this.handleCloseEdit.bind(this);
    this.deleteStrategy = this.deleteStrategy.bind(this);
    this.editStrategy = this.editStrategy.bind(this);

    this.state = {
      showAdd: false,
      showEdit: false,
      selectedStrategy: {},
      selectedStrategyIndex: -1
    };
  }

  componentDidMount() {
    axiosGetWithAuth("/api/strategy/getStrategies")
      .then(res => {
        this.props.onGetStrategiesSuccess(res);
      })
      .catch(error => {
        console.log(error);
        alertError(error);
      });

    axiosGetWithAuth("/api/rule/getRules")
      .then(res => {
        this.props.onGetRulesSuccess(res);
      })
      .catch(error => {
        console.log(error);
        alertError(error);
      });
  }

  handleCloseAdd() {
    this.setState({ showAdd: false });
  }

  handleShowAdd() {
    this.setState({ showAdd: true });
  }

  handleCloseEdit() {
    this.setState({ showEdit: false });
  }

  handleShowEdit() {
    this.setState({ showEdit: true });
  }

  editStrategy(row) {
    let id = row.original.id;
    let index = row.index;
    this.setState({
      selectedStrategy: row.original,
      selectedStrategyIndex: index,
      showEdit: true
    });
  }

  parseFormula(cell) {
    let formula = cell.value;
    if (formula) {
      var formulaWithoutCommas = formula.replace(/\,/g, "");
      let tokens = formulaWithoutCommas.split(
        new RegExp("([" + operators.join("") + "])", "g")
      );

      let parsed = tokens.map(token => {
        if (operatorMap[token]) {
          return operatorMap[token];
        } else if (token.includes("S")) {
          let foundStrategy = this.props.strategies.filter(strategy => {
            return strategy.id == parseInt(token.replace("S", ""));
          });

          return foundStrategy.length > 0
            ? "(" + foundStrategy[0].name + ")"
            : "(Unknown Strategy)";
        } else {
          let foundRule = this.props.rules.filter(rule => {
            return rule.id == parseInt(token);
          });

          return foundRule.length > 0
            ? "(" + foundRule[0].name + ")"
            : "(Unknown Rule)";
        }
      });
      let resultString = parsed.join(" ");
      return <span>{resultString}</span>;
    }
  }

  deleteStrategy(row) {
    let id = row.original.id;
    let index = row.index;
    axiosDeleteWithAuth("/api/strategy/deleteStrategy/" + id)
      .then(res => {
        alertSuccess("Deleted strategy successfully.");
        this.props.onDeleteStrategySuccess(index);
      })
      .catch(error => {
        alertError(error);
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
                    Strategies{tooltip("StrategiesPage")}
                    <PrivateButton
                      user={this.props.user}
                      requiredRoleId={2}
                      className="add_button"
                      variant="primary"
                      onClick={this.handleShowAdd}
                    >
                      Add Strategy
                    </PrivateButton>
                  </div>
                }
                content={
                  <div>
                    <Modal
                      show={this.state.showAdd}
                      onHide={this.handleCloseAdd}
                      dialogClassName="modal-90w"
                    >
                      <Modal.Header closeButton />
                      <Modal.Body>
                        <AddStrategy />
                      </Modal.Body>
                    </Modal>
                    <Modal
                      show={this.state.showEdit}
                      onHide={this.handleCloseEdit}
                      dialogClassName="modal-90w"
                    >
                      <Modal.Header closeButton />
                      <Modal.Body>
                        <EditStrategy
                          strategy={this.state.selectedStrategy}
                          index={this.state.selectedStrategyIndex}
                        />
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
                            <div>
                              <PrivateButton
                                user={this.props.user}
                                requiredRoleId={2}
                                onClick={() => {
                                  this.editStrategy(row);
                                }}
                                bsStyle="danger"
                                simple
                                icon
                              >
                                <i className="fa fa-edit" />
                              </PrivateButton>
                              <PrivateButton
                                user={this.props.user}
                                requiredRoleId={2}
                                onClick={() => {
                                  this.deleteStrategy(row);
                                }}
                                bsStyle="danger"
                                simple
                                icon
                              >
                                <i className="fa fa-times" />
                              </PrivateButton>
                            </div>
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
