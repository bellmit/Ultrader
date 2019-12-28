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
import { tooltip } from "helpers/TooltipHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";
import { parseJavaLocalDatetime } from "helpers/ParseHelper";

import Datetime from "react-datetime";
import "react-datetime/css/react-datetime.css";

var intervalOptions = [
  { value: "60", label: "1 Minute" },
  { value: "300", label: "5 Minutes" },
  { value: "900", label: "15 Minutes" },
  { value: "86400", label: "1 Day" }
];

var booleanOptions = [
  { value: "true", label: "true" },
  { value: "false", label: "false" }
];

export default class AddHistoryMarketDataComp extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.validate = this.validate.bind(this);
    this.saveHistoryMarketData = this.saveHistoryMarketData.bind(this);
    this.state = {
      historyMarketDataName: "",
      historyMarketDataDescription: "",
      historyMarketDataStartDate: "",
      historyMarketDataEndDate: "",
      historyMarketDataInterval: "",
      historyMarketDataAssetList: "",
      selectedAssetListOption: {},
      assetListOptions: []
    };
  }

  componentDidMount() {
    axiosGetWithAuth("/api/asset/getAssetLists")
      .then(response => {
        var assetLists = response.data;
        var assetListOptions = assetLists.map(assetList => {
          return { label: assetList.name, value: assetList.symbols };
        });

        this.setState({
          assetListOptions: assetListOptions
        });
      })
      .catch(error => {
        alertError(error);
      });
  }

  validate() {
    if (
      this.state.historyMarketDataName &&
      this.state.historyMarketDataDescription
    ) {
      return true;
    } else {
      return false;
    }
  }

  saveHistoryMarketData() {
    if (this.validate()) {
      let historyMarketData = {
        name: this.state.historyMarketDataName,
        description: this.state.historyMarketDataDescription,
        assetListName: this.state.selectedAssetListOption.label,
        startDate: parseJavaLocalDatetime(this.state.historyMarketDataStartDate),
        endDate: parseJavaLocalDatetime(this.state.historyMarketDataEndDate),
        period: this.state.selectedIntervalOption.value
      };
      console.log(historyMarketData);
      axiosPostWithAuth("/api/historymarketdata/add", historyMarketData)
        .then(res => {
          alertSuccess("Saved historyMarketData successfully.");
          this.props.onAddHistoryMarketDataSuccess(res);
        })
        .catch(error => {
          alertError(error);
        });
    } else {
      alertError("All fields need to be filled");
    }
  }

  selectAssetListOption(option) {
    let selectedAssetListOption = option ? option : {};
    this.setState({
      selectedAssetListOption: selectedAssetListOption
    });
  }

  selectIntervalOption(option) {
    let selectedIntervalOption = option ? option : {};
    this.setState({
      selectedIntervalOption: selectedIntervalOption
    });
  }

  render() {
    return (
      <div className="main-content">
        <Grid fluid>
          <Row>
            <Col md={8} mdOffset={2}>
              <Card
                textCenter
                title="Add A History Market Data"
                content={
                  <Form horizontal>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          History Market Data Name
                        </ControlLabel>
                        <Col sm={10}>
                          <FormControl
                            type="text"
                            value={this.state.historyMarketDataName}
                            onChange={e =>
                              this.setState({
                                historyMarketDataName: e.target.value
                              })
                            }
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          History Market Data Description
                        </ControlLabel>
                        <Col sm={10}>
                          <FormControl
                            type="text"
                            value={this.state.historyMarketDataDescription}
                            onChange={e =>
                              this.setState({
                                historyMarketDataDescription: e.target.value
                              })
                            }
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>

                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Start Date{" "}
                          {tooltip("Start date of the training data")}
                        </ControlLabel>
                        <Col sm={10}>
                          <Datetime
                            id="startDate"
                            inputProps={{ placeholder: "Test Start Date" }}
                            onChange={e => {
                              this.setState({ historyMarketDataStartDate: e });
                            }}
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          End Date {tooltip("End date of the training data")}
                        </ControlLabel>
                        <Col sm={10}>
                          <Datetime
                            id="endDate"
                            inputProps={{ placeholder: "Test End Date" }}
                            onChange={e => {
                              this.setState({ historyMarketDataEndDate: e });
                            }}
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Trading Period{" "}
                          {tooltip("Trading period used in the training")}
                        </ControlLabel>
                        <Col sm={10}>
                          <Select
                            placeholder="One bar represent how long"
                            name="intervalInput"
                            options={intervalOptions}
                            value={this.state.selectedIntervalOption}
                            id="intervalInput1"
                            onChange={option =>
                              this.selectIntervalOption(option)
                            }
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Asset List{" "}
                          {tooltip(
                            "Assets in the list will be regarded as the training data"
                          )}
                        </ControlLabel>
                        <Col sm={10}>
                          <Select
                            placeholder="Choose a created Asset List"
                            name="tradingStockList"
                            options={this.state.assetListOptions}
                            value={this.state.selectedAssetListOption}
                            id="stocks"
                            onChange={option =>
                              this.selectAssetListOption(option)
                            }
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <hr />
                    <Button
                      bsStyle="info"
                      fill
                      onClick={this.saveHistoryMarketData}
                    >
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
