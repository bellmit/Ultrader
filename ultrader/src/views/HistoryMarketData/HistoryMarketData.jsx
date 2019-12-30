import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import { Grid, Row, Col, Modal, ProgressBar } from "react-bootstrap";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";
import PrivateButton from "components/CustomButton/CustomPrivateButton.jsx";
import axios from "axios";

import AddHistoryMarketData from "containers/HistoryMarketData/AddHistoryMarketData.jsx";
import EditHistoryMarketData from "containers/HistoryMarketData/EditHistoryMarketData.jsx";
import { tooltip } from "helpers/TooltipHelper";
import { axiosGetWithAuth, axiosDeleteWithAuth } from "helpers/UrlHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";
import {
  parseDate,
  parseSeconds,
  parseReadableFileSizeString
} from "helpers/ParseHelper";

class HistoryMarketDataComp extends Component {
  constructor(props) {
    super(props);
    this.handleShowAdd = this.handleShowAdd.bind(this);
    this.handleCloseAdd = this.handleCloseAdd.bind(this);
    this.handleShowEdit = this.handleShowEdit.bind(this);
    this.handleCloseEdit = this.handleCloseEdit.bind(this);
    this.downloadActions = this.downloadActions.bind(this);
    this.deleteHistoryMarketData = this.deleteHistoryMarketData.bind(this);
    this.editHistoryMarketData = this.editHistoryMarketData.bind(this);
    this.downloadHistoryMarketData = this.downloadHistoryMarketData.bind(this);
    this.removeDownloadHistoryMarketData = this.removeDownloadHistoryMarketData.bind(
      this
    );

    this.state = {
      showAdd: false,
      showEdit: false,
      selectedHistoryMarketData: {},
      selectedHistoryMarketDataIndex: -1
    };
  }

  componentDidMount() {
    axiosGetWithAuth("/api/historymarketdata/list")
      .then(res => {
        this.props.onGetHistoryMarketDataSuccess(res);
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

  downloadActions(cell) {
    let id = cell.original.id;
    if (this.props.isDownloading) {
      return <p>Downloading</p>;
    } else if (cell.original.isDownloaded) {
      return (
        <PrivateButton
          user={this.props.user}
          requiredRoleId={2}
          onClick={() => {
            this.removeDownloadHistoryMarketData(cell);
          }}
          bsStyle="danger"
          icon
        >
          Remove from local
        </PrivateButton>
      );
    } else {
      return (
        <PrivateButton
          user={this.props.user}
          requiredRoleId={2}
          onClick={() => {
            this.downloadHistoryMarketData(cell);
          }}
          bsStyle="primary"
          icon
        >
          Download to local
        </PrivateButton>
      );
    }
  }

  editHistoryMarketData(cell) {
    let id = cell.original.id;
    let index = cell.index;
    this.setState({
      selectedHistoryMarketData: cell.original,
      selectedHistoryMarketDataIndex: index,
      showEdit: true
    });
  }

  downloadHistoryMarketData(cell) {
    let id = cell.original.id;
    let index = cell.index;
    axiosGetWithAuth("/api/historymarketdata/download/" + id)
      .then(res => {
        alertSuccess("Downloaded History Market Data successfully.");
        this.props.onEditHistoryMarketDataSuccess(res, index);
      })
      .catch(error => {
        alertError(error);
      });
  }

  removeDownloadHistoryMarketData(cell) {
    let id = cell.original.id;
    let index = cell.index;
    axiosDeleteWithAuth("/api/historymarketdata/download/" + id)
      .then(res => {
        alertSuccess("Removed downloaded History Market Data successfully.");
        this.props.onEditHistoryMarketDataSuccess(res, index);
      })
      .catch(error => {
        alertError(error);
      });
  }

  deleteHistoryMarketData(cell) {
    let id = cell.original.id;
    let index = cell.index;
    axiosDeleteWithAuth("/api/historymarketdata/delete/" + id)
      .then(res => {
        alertSuccess("Deleted History Market Data successfully.");
        this.props.onDeleteHistoryMarketDataSuccess(index);
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
                    <Col sm={8}>
                      History Market Data{tooltip("History Market Data Page")}
                    </Col>
                    <Col sm={4}>
                      <PrivateButton
                        className="add_button pull-right"
                        variant="primary"
                        onClick={this.handleShowAdd}
                        user={this.props.user}
                        requiredRoleId={2}
                      >
                        Add History Market Data
                      </PrivateButton>
                    </Col>
                  </Row>
                }
                content={
                  <div>
                    <Modal
                      show={this.props.isDownloading}
                      dialogClassName="modal-90w"
                    >
                      <Modal.Header>
                        <Modal.Title>
                          Market Data Download: {this.props.progress.status}
                        </Modal.Title>
                      </Modal.Header>
                      <Modal.Body>
                        <p>{this.props.progress.message}</p>
                        <ProgressBar now={this.props.progress.progress} />
                      </Modal.Body>
                    </Modal>
                    <Modal
                      show={this.state.showAdd}
                      onHide={this.handleCloseAdd}
                      dialogClassName="modal-90w"
                    >
                      <Modal.Header closeButton />
                      <Modal.Body>
                        <AddHistoryMarketData />
                      </Modal.Body>
                    </Modal>
                    <Modal
                      show={this.state.showEdit}
                      onHide={this.handleCloseEdit}
                      dialogClassName="modal-90w"
                    >
                      <Modal.Header closeButton />
                      <Modal.Body>
                        <EditHistoryMarketData
                          historyMarketData={
                            this.state.selectedHistoryMarketData
                          }
                          index={this.state.selectedHistoryMarketDataIndex}
                        />
                      </Modal.Body>
                    </Modal>
                    <ReactTable
                      data={this.props.historyMarketDatas}
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
                          Header: "Asset List",
                          accessor: "assetListName"
                        },
                        {
                          Header: "Start Date",
                          accessor: "startDate",
                          Cell: cell => parseDate(cell.value)
                        },
                        {
                          Header: "End Date",
                          accessor: "endDate",
                          Cell: cell => parseDate(cell.value)
                        },
                        {
                          Header: "Period",
                          accessor: "period",
                          Cell: cell => parseSeconds(cell.value)
                        },
                        {
                          Header: "Data Size",
                          accessor: "size",
                          Cell: cell => parseReadableFileSizeString(cell.value * 1024)
                        },
                        {
                          Header: "Assets",
                          accessor: "assetCount",
                          width: 70
                        },

                        {
                          Header: "Download Actions",
                          accessor: "isDownloaded",
                          Cell: cell => this.downloadActions(cell)
                        },
                        {
                          Header: "Actions",
                          width: 100,
                          style: {
                            textAlign: "center"
                          },
                          Cell: cell => (
                            <div>
                              <PrivateButton
                                user={this.props.user}
                                requiredRoleId={2}
                                onClick={() => {
                                  this.editHistoryMarketData(cell);
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
                                  this.deleteHistoryMarketData(cell);
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

export default HistoryMarketDataComp;
