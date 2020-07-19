import React, { Component } from "react";
// react component for creating dynamic tables
import ReactTable from "react-table";
import {
 Grid,
 Row,
 Col,
 Modal,
 FormGroup,
 ControlLabel,
 FormControl,
 HelpBlock,
 Form} from "react-bootstrap";
import Select from "react-select";
import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";
import PrivateButton from "components/CustomButton/CustomPrivateButton.jsx";
import axios from "axios";
import { tooltip } from "helpers/TooltipHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";
import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { parseSymbolGraphModal } from "helpers/ParseHelper";
var sideOptions = [
  { value: "buy", label: "Buy" },
  { value: "sell", label: "Sell" }
];
var typeOptions = [
  { value: "market", label: "Market" }
];
class PendingOrdersComp extends Component {
  constructor(props) {
    super(props);
    this.validate = this.validate.bind(this);
    this.handleShowAdd = this.handleShowAdd.bind(this);
    this.handleCloseAdd = this.handleCloseAdd.bind(this);
    this.submitOrder = this.submitOrder.bind(this);
    this.state = {
      showAdd: false,
      asset: "",
      side:"",
      sideOptions: sideOptions,
      qty:0
    };
    axiosGetWithAuth("/api/order/getOpenOrders")
      .then(res => {
        this.props.onGetPendingOrdersSuccess(res);
      })
      .catch(error => {
        console.log(error);
        alert(error);
      });
  }
  handleCloseAdd() {
    this.setState({ showAdd: false });
  }

  handleShowAdd() {
    this.setState({ showAdd: true });
  }
  validate() {
    if (
      this.state.asset &&
      this.state.side &&
      /^[A-Z]+(?:,[A-Z]+)*$/.test(this.state.asset)
    ) {
      return true;
    } else {
      return false;
    }
  }
  submitOrder() {
    if (this.validate()) {
    let param = "asset=" + this.state.asset + "&side=" + this.state.side.value + "&type=market&qty=" + this.state.qty + "&price=0";
      axiosPostWithAuth("/api/order/manualOrder?" + param, {})
        .then(res => {
          alertSuccess("Submit order successfully.");
        })
        .catch(error => {
          alertError(error);
        });
    } else {
      alertError(
        "All fields need to be filled, and make sure there is no errors."
      );
    }
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
                    <Col sm={8}>Pending Orders{tooltip("PendingOrdersPage")}</Col>
                    <Col sm={4}>
                      {" "}
                      <PrivateButton
                        user={this.props.user}
                        requiredRoleId={2}
                        className="add_button"
                        variant="primary"
                        onClick={this.handleShowAdd}
                      >
                        Manual Order
                      </PrivateButton>
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
					  <div className="main-content">
                        <Grid fluid>
                        <Row>
                        <Col md={8} mdOffset={2}>
                        <Card
                            textCenter
                            title="Manual Order"
                            content={
                            <Form horizontal>
                               <fieldset>
                                    <FormGroup>
                                        <ControlLabel className="col-sm-2">
                                            Asset Code
                                        </ControlLabel>
                                        <Col sm={10}>
                                            <FormControl
                                                type="text"
                                                value={this.state.asset}
                                                onChange={e =>
                                                    this.setState({ asset: e.target.value })
                                                }
                                            />
                                        </Col>
                                    </FormGroup>
                               </fieldset>
                               <fieldset>
                                           <FormGroup>
                                             <ControlLabel className="col-sm-2">
                                               Side
                                             </ControlLabel>
                                             <Col sm={10}>
                                             <Select
                                               name="sideSelect"
                                               options={this.state.sideOptions}
                                               value={this.state.side}
                                               id="manual_order_side"
                                               onChange={option =>
                                                 this.setState({ side: option })
                                               }
                                             />
                                             </Col>
                                           </FormGroup>
                               </fieldset>
                              <fieldset>
                                    <FormGroup>
                                        <ControlLabel className="col-sm-2">
                                            Quantity
                                        </ControlLabel>
                                        <Col sm={10}>
                                            <FormControl
                                                type="text"
                                                value={this.state.qty}
                                                onChange={e =>
                                                    this.setState({ qty: e.target.value })
                                                }
                                            />
                                        </Col>
                                    </FormGroup>
                               </fieldset>

                               <Button bsStyle="info" fill onClick={this.submitOrder}>
                                Submit
                               </Button>

                            </Form>
                            }
                        />
                        </Col>
                        </Row>
                        </Grid>
                      </div>
                      </Modal.Body>
                  </Modal>
                  <ReactTable
                    data={this.props.pendingOrders}
                    filterable
                    columns={[
                      {
                        Header: "Symbol",
                        accessor: "symbol",
                        Cell: cell => parseSymbolGraphModal(cell.value)
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

export default PendingOrdersComp;
