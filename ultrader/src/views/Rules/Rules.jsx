import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import { Grid, Row, Col, Modal } from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";
import axios from "axios";

import AddRule from "containers/Rules/AddRule.jsx";
import EditRule from "containers/Rules/EditRule.jsx";

import { axiosGetWithAuth, axiosDeleteWithAuth } from "helpers/UrlHelper";

class RulesComp extends Component {
  constructor(props) {
    super(props);

    axiosGetWithAuth("/api/rule/getRules")
      .then(res => {
        this.props.onGetRulesSuccess(res);
      })
      .catch(error => {
        console.log(error);
        alert(error);
      });
    this.handleShowAdd = this.handleShowAdd.bind(this);
    this.handleCloseAdd = this.handleCloseAdd.bind(this);
    this.handleShowEdit = this.handleShowEdit.bind(this);
    this.handleCloseEdit = this.handleCloseEdit.bind(this);
    this.deleteRule = this.deleteRule.bind(this);
    this.editRule = this.editRule.bind(this);

    this.state = {
      showAdd: false,
      showEdit: false,
      selectedRule: {},
      selectedRuleIndex: -1
    };
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

  editRule(row) {
    let id = row.original.id;
    let index = row.index;
    this.setState({
        selectedRule : row.original,
        selectedRuleIndex : index,
        showEdit: true
    });
  }

  deleteRule(row) {
    let id = row.original.id;
    let index = row.index;
    axiosDeleteWithAuth("/api/rule/deleteRule/" + id)
      .then(res => {
        alert("Deleted rule successfully.");
        this.props.onDeleteRuleSuccess(index);
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
                    Rules
                    <Button
                      className="add_button"
                      variant="primary"
                      onClick={this.handleShowAdd}
                    >
                      Add Rule
                    </Button>
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
                        <AddRule />
                      </Modal.Body>
                    </Modal>
                    <Modal
                      show={this.state.showEdit}
                      onHide={this.handleCloseEdit}
                      dialogClassName="modal-90w"
                    >
                      <Modal.Header closeButton />
                      <Modal.Body>
                        <EditRule rule={this.state.selectedRule} index={this.state.selectedRuleIndex}/>
                      </Modal.Body>
                    </Modal>
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
                        },
                        {
                          Header: "Actions",
                          width: 100,
                          style: {
                            textAlign: "center"
                          },
                          Cell: row => (
                          <div>
                            <Button
                              onClick={() => {
                                this.editRule(row);
                              }}
                              bsStyle="danger"
                              simple
                              icon
                            >
                              <i className="fa fa-edit" />
                            </Button>
                            <Button
                              onClick={() => {
                                this.deleteRule(row);
                              }}
                              bsStyle="danger"
                              simple
                              icon
                            >
                              <i className="fa fa-times" />
                            </Button>
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

export default RulesComp;
