import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import { Grid, Row, Col, Modal } from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import PrivateButton from "components/CustomButton/CustomPrivateButton.jsx";
import axios from "axios";

import AddAssetList from "containers/AssetLists/AddAssetList.jsx";
import EditAssetList from "containers/AssetLists/EditAssetList.jsx";

import { axiosGetWithAuth, axiosDeleteWithAuth } from "helpers/UrlHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";
import { tooltip } from "helpers/TooltipHelper";
class AssetListsComp extends Component {
  constructor(props) {
    super(props);

    this.handleShowAdd = this.handleShowAdd.bind(this);
    this.handleCloseAdd = this.handleCloseAdd.bind(this);
    this.handleShowEdit = this.handleShowEdit.bind(this);
    this.handleCloseEdit = this.handleCloseEdit.bind(this);
    this.deleteAssetList = this.deleteAssetList.bind(this);
    this.editAssetList = this.editAssetList.bind(this);

    this.state = {
      showAdd: false,
      showEdit: false,
      selectedAssetList: {},
      selectedAssetListIndex: -1
    };
  }

  componentDidMount() {
    axiosGetWithAuth("/api/asset/getAssetLists")
      .then(res => {
        this.props.onGetAssetListsSuccess(res);
      })
      .catch(error => {
        console.log(error);
        alertError(error);
      });
    axiosGetWithAuth("/api/asset/getAllAssets")
      .then(res => {
        this.props.onGetAllAssetsSuccess(res);
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

  editAssetList(row) {
    let id = row.original.id;
    let index = row.index;
    this.setState({
      selectedAssetList: row.original,
      selectedAssetListIndex: index,
      showEdit: true
    });
  }

  deleteAssetList(row) {
    let name = row.original.name;
    let index = row.index;
    axiosDeleteWithAuth("/api/asset/deleteAssetList/" + name)
      .then(res => {
        alertSuccess("Deleted assetList successfully.");
        this.props.onDeleteAssetListSuccess(index);
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
                    Asset Lists{tooltip("AssetListsPage")}
                    <PrivateButton
                      {...this.props}
                      requiredRoleId={2}
                      className="add_button"
                      variant="primary"
                      onClick={this.handleShowAdd}
                    >
                      Add AssetList
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
                        <AddAssetList />
                      </Modal.Body>
                    </Modal>
                    <Modal
                      show={this.state.showEdit}
                      onHide={this.handleCloseEdit}
                      dialogClassName="modal-90w"
                    >
                      <Modal.Header closeButton />
                      <Modal.Body>
                        <EditAssetList
                          assetList={this.state.selectedAssetList}
                          index={this.state.selectedAssetListIndex}
                        />
                      </Modal.Body>
                    </Modal>
                    <ReactTable
                      data={this.props.assetLists}
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
                          Header: "Symbols",
                          accessor: "symbols"
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
                                {...this.props}
                                requiredRoleId={2}
                                onClick={() => {
                                  this.editAssetList(row);
                                }}
                                bsStyle="danger"
                                simple
                                icon
                              >
                                <i className="fa fa-edit" />
                              </PrivateButton>
                              <PrivateButton
                                {...this.props}
                                requiredRoleId={2}
                                onClick={() => {
                                  this.deleteAssetList(row);
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

export default AssetListsComp;
