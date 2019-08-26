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
import Select from "react-select";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";

import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";

import { alertSuccess, alertError } from "helpers/AlertHelper";

export default class AddAssetListComp extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.validate = this.validate.bind(this);
    this.saveAssetList = this.saveAssetList.bind(this);
    this.onAssetsInputChange = this.onAssetsInputChange.bind(this);

    this.state = {
      assetListName: "",
      assetListDescription: "",
      selectedAssetsOptions: [],
      selectedAssetsList: ""
    };
  }

  componentDidMount() {}

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
          this.props.onAddAssetListSuccess(res);
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

  render() {
    const { assetsInputValue, assetsMenuOpen } = this.state;
    return (
      <div className="main-content">
        <Grid fluid>
          <Row>
            <Col md={8} mdOffset={2}>
              <Card
                textCenter
                title="Add A AssetList"
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
                          AssetList Type
                        </ControlLabel>
                        <Col sm={10}>
                          <Select
                            isMulti
                            value={this.props.selectedAssetsOptions}
                            isClearable
                            isSearchable
                            inputValue={assetsInputValue}
                            onChange={this.onAssetsInputChange}
                            name="assets"
                            id="TRADE_EXCHANGE_LIST"
                            options={this.props.assetOptions}
                            menuIsOpen={assetsMenuOpen}
                          />
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
