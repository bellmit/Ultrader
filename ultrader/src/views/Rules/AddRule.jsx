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

var booleanOptions = [
  { value: "true", label: "true" },
  { value: "false", label: "false" }
];

var noValuesNeeded = ["ClosePrice", "TimeSeries"];

export default class AddRuleComp extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.initializeRuleFields = this.initializeRuleFields.bind(this);
    this.validate = this.validate.bind(this);
    this.saveRule = this.saveRule.bind(this);
    this.state = {
      ruleName: "",
      ruleDescription: "",
      ruleFieldTypes: [],
      ruleFieldTypeOptions: [],
      ruleFields: {},
      selectedRuleTypeOption: {},
      selectedRuleFieldTypeOption: "",
      ruleFieldValues: []
    };
  }

  componentDidMount() {}

  validate() {
    if (
      this.state.ruleName &&
      this.state.ruleDescription &&
      this.state.selectedRuleTypeOption &&
      this.state.ruleFieldValues &&
      this.state.selectedRuleTypeOption &&
      this.state.selectedRuleTypeOption.value &&
      this.state.ruleFieldValues.every(
        o =>
          noValuesNeeded.includes(o.ruleFieldName) ||
          (o.value &&
            (o.value.value ||
              (o.value.indicatorArgs &&
                o.value.indicatorArgs.every(ia => noValuesNeeded.includes(ia.label) || ia.value))))
      )
    ) {
      return true;
    } else {
      return false;
    }
  }

  getStringForRuleFieldValue(ruleFieldValue) {
    let res = ruleFieldValue.label;
    switch (ruleFieldValue.ruleFieldName) {
      case "NumIndicator":
        if (ruleFieldValue.value && ruleFieldValue.value.indicatorArgs) {
          for (var i = 0; i < ruleFieldValue.value.indicatorArgs.length; i++) {
            if (
              ruleFieldValue.value.indicatorArgs[i].value &&
              ruleFieldValue.value.indicatorArgs[i].value !== "N/A"
            ) {
              res += ":" + ruleFieldValue.value.indicatorArgs[i].value;
            } else {
              res += ":" + ruleFieldValue.value.indicatorArgs[i].label;
            }
          }
          return res;
        } else {
          return "";
        }
        break;
      default:
        if (ruleFieldValue.value.value == "N/A") {
          return res;
        } else {
          return res + ":" + ruleFieldValue.value.value;
        }
        break;
    }
  }

  saveRule() {
    if (this.validate()) {
      let formulaParts = this.state.ruleFieldValues.map((value, i) => {
        return this.getStringForRuleFieldValue(value);
      });

      let formula = formulaParts.join(",");
      let rule = {
        name: this.state.ruleName,
        description: this.state.ruleDescription,
        type: this.state.selectedRuleTypeOption.classz,
        formula: formula
      };
      axiosPostWithAuth("/api/rule/addRule", rule)
        .then(res => {
          console.log(res);
          alertSuccess("Saved rule successfully.");
          this.props.onAddRuleSuccess(res);
        })
        .catch(error => {
          alertError(error);
        });
    } else {
      alertError("All fields need to be filled");
    }
  }

  selectRuleFieldsType(option) {
    let selectedRuleFieldTypeOption = option ? option : {};
    this.setState({
      selectedRuleFieldTypeOption: selectedRuleFieldTypeOption
    });
  }

  selectRuleType(option) {
    let ruleFieldTypes = option ? option.value : [];

    let ruleFieldTypeOptions = option
      ? option.value.map(ruleFieldType => {
          return { label: ruleFieldType, value: ruleFieldType };
        })
      : [];
    let selectedRuleFieldTypeOption =
      ruleFieldTypeOptions.length > 0 ? ruleFieldTypeOptions[0] : {};

    this.initializeRuleFields(selectedRuleFieldTypeOption);
    this.setState({
      ruleFieldTypes: ruleFieldTypes,
      ruleFieldTypeOptions: ruleFieldTypeOptions,
      selectedRuleTypeOption: option,
      selectedRuleFieldTypeOption: selectedRuleFieldTypeOption
    });
  }

  initializeRuleFields(selectedRuleFieldTypeOption) {
    console.log(selectedRuleFieldTypeOption);
    let ruleFields = selectedRuleFieldTypeOption.value.split("|");
    var ruleFieldValues = ruleFields.map(ruleField => {
      switch (ruleField) {
        case "ClosePrice":
        case "TimeSeries":
          var ruleFieldValue = {
            label: ruleField,
            ruleFieldName: ruleField,
            value: {
              value: "N/A"
            }
          };
          return ruleFieldValue;
        default:
          var ruleFieldValue = {
            label: ruleField,
            ruleFieldName: ruleField,
            value: {
              value: ""
            }
          };
          return ruleFieldValue;
      }
    });
    this.setState({
      ruleFieldValues: ruleFieldValues
    });
  }

  setValueAndPopulateIndicatorArgs(ruleFieldName, ruleFieldValue, index) {
    switch (ruleFieldName) {
      case "NumIndicator":
        if (ruleFieldValue && ruleFieldValue.args) {
          let indicatorName = ruleFieldValue.label;
          let indicatorArgs = ruleFieldValue.args.split("|");
          let indicatorArgInputs = indicatorArgs.map(indicatorArg => {
            switch (indicatorArg) {
              default:
                return { label: indicatorArg, value: "" };
            }
          });

          let ruleFieldValues = this.state.ruleFieldValues;
          ruleFieldValues[index] = {
            label: indicatorName,
            ruleFieldName: ruleFieldName,
            value: {
              indicatorArgs: indicatorArgInputs
            }
          };
          this.setState({
            ruleFieldValues: ruleFieldValues
          });
        } else {
          let ruleFieldValues = this.state.ruleFieldValues;
          ruleFieldValues[index] = {};
          this.setState({
            ruleFieldValues: ruleFieldValues
          });
        }
        break;
      default:
        break;
    }
  }

  ruleFields() {
    if (
      this.state.selectedRuleFieldTypeOption &&
      this.state.selectedRuleFieldTypeOption.value
    ) {
      let ruleFieldNames = this.state.selectedRuleFieldTypeOption.value.split(
        "|"
      );
      return (
        <div>
          {ruleFieldNames.map((ruleFieldName, index) => (
            <fieldset>
              <FormGroup>
                <ControlLabel className="col-sm-2">
                  {ruleFieldName}
                </ControlLabel>
                <Col sm={10}>{this.ruleField(ruleFieldName, index)}</Col>
              </FormGroup>
            </fieldset>
          ))}
        </div>
      );
    } else {
      return;
    }
  }

  setRuleFieldValue(ruleFieldName, ruleFieldValue, index) {
    let ruleFieldValues = this.state.ruleFieldValues;
    ruleFieldValues[index] = {
      label: ruleFieldName,
      ruleFieldName: ruleFieldName,
      value: {
        value: ruleFieldValue
      }
    };
    this.setState({
      ruleFieldValues: ruleFieldValues
    });
  }

  setRuleFieldIndicatorFieldValue(i, j, value) {
    let ruleFieldValues = this.state.ruleFieldValues;

    ruleFieldValues[i].value.indicatorArgs[j].value = value;
    this.setState({
      ruleFieldValues: ruleFieldValues
    });
  }

  ruleField(ruleFieldName, index) {
    switch (ruleFieldName) {
      case "NumIndicator":
        return (
          <div>
            <Select
              placeholder="Number Indicator"
              name="numIndicator"
              value={this.state.ruleFieldValues[index]}
              options={this.props.indicatorSelectOptions["NumIndicator"]}
              onChange={option =>
                this.setValueAndPopulateIndicatorArgs(
                  ruleFieldName,
                  option ? option.value : "",
                  index
                )
              }
            />

            {((this.state.ruleFieldValues[index] || {}).value || {})
              .indicatorArgs &&
              this.state.ruleFieldValues[index].value.indicatorArgs.map(
                (indicatorArg, j) => {
                  switch (indicatorArg.label) {
                    case "ClosePrice":
                    case "TimeSeries":
                      return (
                        <fieldset>
                          <FormGroup>
                            <ControlLabel className="col-sm-2">
                              {indicatorArg.label}
                            </ControlLabel>
                            <Col sm={10}>
                              <FormControl
                                type="text"
                                disabled
                                defaultValue="N/A"
                                placeholder="N/A"
                              />
                            </Col>
                          </FormGroup>
                        </fieldset>
                      );
                    case "Integer":
                      return (
                        <fieldset>
                          <FormGroup>
                            <ControlLabel className="col-sm-2">
                              {indicatorArg.label}
                            </ControlLabel>
                            <Col sm={10}>
                              <FormControl
                                type="text"
                                pattern="[+-]?[0-9]*"
                                value={
                                  (
                                    (
                                      (this.state.ruleFieldValues[index] || {})
                                        .value || {}
                                    ).indicatorArgs[j] || {}
                                  ).value
                                }
                                onChange={e => {
                                  if (e.target.validity.valid) {
                                    this.setRuleFieldIndicatorFieldValue(
                                      index,
                                      j,
                                      e.target.value
                                    );
                                  }
                                }}
                              />
                            </Col>
                          </FormGroup>
                        </fieldset>
                      );
                    case "Number":
                    case "Double":
                    case "Decimal":
                      return (
                        <fieldset>
                          <FormGroup>
                            <ControlLabel className="col-sm-2">
                              {indicatorArg.label}
                            </ControlLabel>
                            <Col sm={10}>
                              <FormControl
                                type="text"
                                pattern="^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$"
                                value={
                                  (
                                    (
                                      (this.state.ruleFieldValues[index] || {})
                                        .value || {}
                                    ).indicatorArgs[j] || {}
                                  ).value
                                }
                                onChange={e => {
                                  if (e.target.validity.valid) {
                                    this.setRuleFieldIndicatorFieldValue(
                                      index,
                                      j,
                                      e.target.value
                                    );
                                  }
                                }}
                              />
                            </Col>
                          </FormGroup>
                        </fieldset>
                      );
                    default:
                      return (
                        <fieldset>
                          <FormGroup>
                            <ControlLabel className="col-sm-2">
                              {indicatorArg.label}
                            </ControlLabel>
                            <Col sm={10}>
                              <FormControl
                                type="text"
                                onChange={e =>
                                  this.setRuleFieldIndicatorFieldValue(
                                    index,
                                    j,
                                    e.target.value
                                  )
                                }
                              />
                            </Col>
                          </FormGroup>
                        </fieldset>
                      );
                  }
                }
              )}
          </div>
        );
      case "Integer":
        return (
          <FormControl
            type="text"
            pattern="[+-]?[0-9]*"
            placeholder={ruleFieldName}
            value={
              ((this.state.ruleFieldValues[index] || {}).value || {}).value
                ? this.state.ruleFieldValues[index].value.value
                : ""
            }
            onChange={e => {
              if (e.target.validity.valid) {
                this.setRuleFieldValue(ruleFieldName, e.target.value, index);
              }
            }}
          />
        );
      case "Number":
      case "Double":
      case "Decimal":
        return (
          <FormControl
            type="text"
            pattern="^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$"
            placeholder={ruleFieldName}
            value={
              ((this.state.ruleFieldValues[index] || {}).value || {}).value
                ? this.state.ruleFieldValues[index].value.value
                : ""
            }
            onChange={e => {
              if (e.target.validity.valid) {
                this.setRuleFieldValue(ruleFieldName, e.target.value, index);
              }
            }}
          />
        );
      case "ClosePrice":
      case "TimeSeries":
        return (
          <FormControl
            type="text"
            disabled
            defaultValue="N/A"
            placeholder="N/A"
          />
        );
      case "Boolean":
        return (
          <Select
            placeholder="Boolean"
            name="boolean"
            value={this.state.ruleFieldValues[index]}
            options={booleanOptions}
            onChange={option =>
              this.setRuleFieldValue(
                ruleFieldName,
                option ? option.value : "",
                index
              )
            }
          />
        );
      default:
        return (
          <FormControl
            type="text"
            placeholder={ruleFieldName}
            onChange={e =>
              this.setRuleFieldValue(ruleFieldName, e.target.value, index)
            }
          />
        );
    }
  }

  render() {
    return (
      <div className="main-content">
        <Grid fluid>
          <Row>
            <Col md={8} mdOffset={2}>
              <Card
                textCenter
                title="Add A Rule"
                content={
                  <Form horizontal>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Rule Name
                        </ControlLabel>
                        <Col sm={10}>
                          <FormControl
                            type="text"
                            value={this.state.ruleName}
                            onChange={e =>
                              this.setState({ ruleName: e.target.value })
                            }
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Rule Description
                        </ControlLabel>
                        <Col sm={10}>
                          <FormControl
                            type="text"
                            value={this.state.ruleDescription}
                            onChange={e =>
                              this.setState({ ruleDescription: e.target.value })
                            }
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Rule Type
                        </ControlLabel>
                        <Col sm={10}>
                          <Select
                            placeholder="Rule Type"
                            name="ruleType"
                            value={this.state.selectedRuleTypeOption}
                            options={this.props.ruleTypeSelectOptions}
                            onChange={value => this.selectRuleType(value)}
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    {this.state.ruleFieldTypes.length > 1 && (
                      <fieldset>
                        <FormGroup>
                          <ControlLabel className="col-sm-2">
                            Input Type
                          </ControlLabel>
                          <Col sm={10}>
                            <Select
                              placeholder="Input Type"
                              name="singleSelect"
                              value={this.state.selectedRuleFieldTypeOption}
                              options={this.state.ruleFieldTypeOptions}
                              onChange={value =>
                                this.selectRuleFieldsType(value)
                              }
                            />
                          </Col>
                        </FormGroup>
                      </fieldset>
                    )}
                    <hr />
                    {this.ruleFields()}
                    <hr />
                    <Button bsStyle="info" fill onClick={this.saveRule}>
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
