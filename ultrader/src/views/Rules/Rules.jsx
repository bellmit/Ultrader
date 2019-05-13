import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import { Grid, Row, Col, Modal } from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";
import axios from "axios";

import AddRule from "containers/Rules/AddRule.jsx";

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
    this.handleShow = this.handleShow.bind(this);
    this.handleClose = this.handleClose.bind(this);
    this.deleteRule = this.deleteRule.bind(this);

    this.state = {
      show: false
    };
  }

  handleClose() {
    this.setState({ show: false });
  }

  handleShow() {
    this.setState({ show: true });
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
                      onClick={this.handleShow}
                    >
                      Add Rule
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
                        <AddRule />
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
