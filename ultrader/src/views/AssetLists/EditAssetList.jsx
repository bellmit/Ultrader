import React from "react";

import axios from "axios";
import {
  Grid,
  Row,
  Col,
  FormGroup,
  ControlLabel,
  FormControl,
  HelpBlock,
  Form
} from "react-bootstrap";
import WindowedSelect, { createFilter } from "react-windowed-select";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";

import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { isFloat, isInt } from "helpers/ParseHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";
import { tooltip } from "helpers/TooltipHelper";

var booleanOptions = [
  { value: "true", label: "true" },
  { value: "false", label: "false" }
];

var noValuesNeeded = ["ClosePrice", "TimeSeries"];

export default class EditAssetListComp extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.validate = this.validate.bind(this);
    this.saveAssetList = this.saveAssetList.bind(this);
    this.onAssetsInputChange = this.onAssetsInputChange.bind(this);
    this.handleAssetsChange = this.handleAssetsChange.bind(this);

    this.state = {
      assetListName: this.props.assetList.name,
      assetListDescription: this.props.assetList.description,
      selectedAssetsOptions: [],
      selectedAssetsList: this.props.assetList.symbols,
      assetsError: null
    };
  }

  componentDidMount() {
    var selectedSymbols = this.props.assetList.symbols.split(",");
    var selectedAssetsOptions = selectedSymbols.map(symbol => {
      return {
        label: symbol,
        value: symbol
      };
    });
    this.setState({
      selectedAssetsOptions: selectedAssetsOptions
    });
  }

  validate() {
    if (this.state.assetListName && this.state.assetListDescription) {
      return true;
    } else {
      return false;
    }
  }

  saveAssetList() {
    if (this.validate()) {
      let assetList = {
        name: this.state.assetListName,
        description: this.state.assetListDescription,
        symbols: this.state.selectedAssetsList
      };
      axiosPostWithAuth("/api/asset/setAssetList", assetList)
        .then(res => {
          alertSuccess("Saved asset list successfully.");
          this.props.onEditAssetListSuccess(res);
        })
        .catch(error => {
          alertError(error);
        });
    } else {
      alertError("All fields need to be filled");
    }
  }

  onAssetsInputChange(option) {
    let selectedAssetsOptions = option ? option : [];
    let optionStringList = selectedAssetsOptions.map(o => o.value).join(",");
    this.setState({
      selectedAssetsOptions: selectedAssetsOptions,
      selectedAssetsList: optionStringList
    });
  }

  handleAssetsChange(event) {
    var regex = /^([A-Z]{1,5}?,)*([A-Z]{1,5}?)+$/;
    this.setState({
      selectedAssetsList: event.target.value
    });
    !regex.test(event.target.value)
      ? this.setState({
          assetsError: (
            <small className="text-danger">
              Please enter 5 or less capital letters stock symbols separated by
              commas (AAPL,GOOGL).
            </small>
          )
        })
      : this.setState({ assetsError: null });
  }

  render() {
    const { assetsInputValue, assetsMenuOpen } = this.state;
    return (
      <div className="main-content">
        <Grid fluid>
          <Row>
            <Col md={8} mdOffset={2}>
              <Card
                textCenter
                title="Edit A AssetList"
                content={
                  <Form horizontal>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          AssetList Name
                        </ControlLabel>
                        <Col sm={10}>
                          <FormControl
                            type="text"
                            value={this.state.assetListName}
                            onChange={e =>
                              this.setState({ assetListName: e.target.value })
                            }
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          AssetList Description
                        </ControlLabel>
                        <Col sm={10}>
                          <FormControl
                            type="text"
                            value={this.state.assetListDescription}
                            onChange={e =>
                              this.setState({
                                assetListDescription: e.target.value
                              })
                            }
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Assets{" "}
                          {tooltip(
                            "Please enter 5 or less capital letters stock symbols separated by commas (AAPL,GOOGL)."
                          )}
                        </ControlLabel>
                        <Col sm={10}>
                          {/*
                          <WindowedSelect
                            isMulti
                            value={this.props.selectedAssetsOptions}
                            isClearable
                            isSearchable
                            filterOption={createFilter({ignoreAccents: false})}
                            inputValue={assetsInputValue}
                            onChange={this.onAssetsInputChange}
                            name="assets"
                            id="TRADE_EXCHANGE_LIST"
                            options={this.props.assetOptions}
                            menuIsOpen={assetsMenuOpen}
                          />*/}
                          <textarea
                            className="form-control"
                            id="exampleFormControlTextarea1"
                            placeholder="Please enter 5 or less capital letters stock symbols separated by commas (AAPL,GOOGL)."
                            rows="5"
                            value={this.state.selectedAssetsList}
                            onChange={event => this.handleAssetsChange(event)}
                          />

                          {this.state.assetsError}
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <Button bsStyle="info" fill onClick={this.saveAssetList}>
                      Submit
                    </Button>
                  </Form>
                }
              />
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}
