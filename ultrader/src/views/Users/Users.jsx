import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import { Grid, Row, Col, Modal } from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";
import axios from "axios";

import AddUser from "containers/Users/AddUser.jsx";
import EditUser from "containers/Users/EditUser.jsx";
import { tooltip } from "helpers/TooltipHelper";
import { axiosGetWithAuth, axiosDeleteWithAuth } from "helpers/UrlHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";

var roleMap = {
  1: "Admin",
  2: "Operator",
  3: "Read-Only User"
};

class UsersComp extends Component {
  constructor(props) {
    super(props);

    this.handleShowAdd = this.handleShowAdd.bind(this);
    this.handleCloseAdd = this.handleCloseAdd.bind(this);
    this.handleShowEdit = this.handleShowEdit.bind(this);
    this.handleCloseEdit = this.handleCloseEdit.bind(this);
    this.deleteUser = this.deleteUser.bind(this);
    this.editUser = this.editUser.bind(this);

    this.state = {
      showAdd: false,
      showEdit: false,
      selectedUser: {},
      selectedUserIndex: -1
    };
  }

  componentDidMount() {
    axiosGetWithAuth("/api/user/getUsers")
      .then(res => {
        this.props.onGetUsersSuccess(res);
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

  editUser(row) {
    let id = row.original.id;
    let index = row.index;
    this.setState({
      selectedUser: row.original,
      selectedUserIndex: index,
      showEdit: true
    });
  }

  deleteUser(row) {
    let id = row.original.id;
    let index = row.index;
    axiosDeleteWithAuth("/api/user/deleteUser/" + id)
      .then(res => {
        alertSuccess("Deleted user successfully.");
        this.props.onDeleteUserSuccess(index);
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
                  <Row>
                    <Col sm={8}>Users{tooltip("UsersPage")}</Col>
                    <Col sm={4}>
                      <Button
                        className="add_button"
                        variant="primary"
                        onClick={this.handleShowAdd}
                      >
                        Add User
                      </Button>
                    </Col>
                  </Row>
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
                        <AddUser />
                      </Modal.Body>
                    </Modal>
                    <Modal
                      show={this.state.showEdit}
                      onHide={this.handleCloseEdit}
                      dialogClassName="modal-90w"
                    >
                      <Modal.Header closeButton />
                      <Modal.Body>
                        <EditUser
                          editUser={this.state.selectedUser}
                          index={this.state.selectedUserIndex}
                        />
                      </Modal.Body>
                    </Modal>
                    <ReactTable
                      data={this.props.users}
                      filterable
                      columns={[
                        {
                          Header: "User Name",
                          accessor: "username"
                        },
                        {
                          Header: "Role",
                          accessor: "roleId",
                          Cell: cell => roleMap[cell.value]
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
                                  this.editUser(row);
                                }}
                                bsStyle="danger"
                                simple
                                icon
                              >
                                <i className="fa fa-edit" />
                              </Button>
                              {this.props.users.filter(user => user.roleId == 1)
                                .length > 1 || row.original.roleId != 1 ? (
                                <Button
                                  onClick={() => {
                                    this.deleteUser(row);
                                  }}
                                  bsStyle="danger"
                                  simple
                                  icon
                                >
                                  <i className="fa fa-times" />
                                </Button>
                              ) : (
                                ""
                              )}
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

export default UsersComp;
