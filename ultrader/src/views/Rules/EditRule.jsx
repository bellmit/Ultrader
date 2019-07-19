/****** example state ************
{
  "ruleName": "MACD Buy",
  "ruleDescription": "When MACD > 0.5",
  "ruleFieldTypes": [
    "NumIndicator|NumIndicator",
    "NumIndicator|Number"
  ],
  "ruleFieldTypeOptions": [
    {
      "label": "NumIndicator|NumIndicator",
      "value": "NumIndicator|NumIndicator"
    },
    {
      "label": "NumIndicator|Number",
      "value": "NumIndicator|Number"
    }
  ],
  "ruleFields": {},
  "selectedRuleTypeOption": {
    "value": [
      "NumIndicator|NumIndicator",
      "NumIndicator|Number"
    ],
    "label": "Crossed Up Rule",
    "classz": "CrossedUpIndicatorRule"
  },
  "selectedRuleFieldTypeOption": {
    "label": "NumIndicator|Number",
    "value": "NumIndicator|Number"
  },
  "ruleFieldValues": [
    {
      "label": "MACDIndicator",
      "ruleFieldName": "NumIndicator",
      "value": {
        "indicatorArgs": [
          {
            "label": "ClosePrice",
            "value": ""
          },
          {
            "label": "Integer",
            "value": "1111111"
          },
          {
            "label": "Integer",
            "value": "11"
          }
        ]
      }
    },
    {
      "label": "Number",
      "ruleFieldName": "Number",
      "value": {
        "value": "11"
      }
    }
  ]
}
*/

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
import { isFloat, isInt } from "helpers/ParseHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";

var booleanOptions = [
  { value: "true", label: "true" },
  { value: "false", label: "false" }
];

export default class EditRuleComp extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.validate = this.validate.bind(this);
    this.saveRule = this.saveRule.bind(this);
    this.parseRule = this.parseRule.bind(this);
    this.getFieldType = this.getFieldType.bind(this);
    this.generateIndicatorArgs = this.generateIndicatorArgs.bind(this);
    this.state = {
      ruleName: this.props.rule.name,
      ruleDescription: this.props.rule.description,
      ruleFieldTypes: [],
      ruleFieldTypeOptions: [],
      ruleFields: {},
      selectedRuleTypeOption: {},
      selectedRuleFieldTypeOption: "",
      ruleFieldValues: []
    };
  }

  getFieldType(typeString) {
    if (
      this.props.categoryIndicatorMap["NumIndicator"].indexOf(typeString) !== -1
    ) {
      return "NumIndicator";
    } else {
      return typeString;
    }
  }

  selectRuleFieldsType(option) {
    let selectedRuleFieldTypeOption = option ? option : {};
    this.setState({
      selectedRuleFieldTypeOption: selectedRuleFieldTypeOption
    });
  }

  selectRuleTypeAndFieldTypeAndArgs(
    ruleTypeValue,
    ruleFieldTypeValue,
    ruleFieldValues
  ) {
    var ruleType = this.props.ruleTypeSelectOptions.find(
      e => e.classz === ruleTypeValue
    );
    let ruleFieldTypes = ruleType ? ruleType.value : [];

    let ruleFieldTypeOptions = ruleType
      ? ruleType.value.map(ruleFieldType => {
          return { label: ruleFieldType, value: ruleFieldType };
        })
      : [];
    var selectedRuleFieldTypeOption = ruleFieldTypeOptions.find(
      e => e.label === ruleFieldTypeValue
    );
    if (!selectedRuleFieldTypeOption) {
      selectedRuleFieldTypeOption =
        ruleFieldTypeOptions.length > 0 ? ruleFieldTypeOptions[0] : {};
    }
    this.setState({
      ruleFieldTypes: ruleFieldTypes,
      ruleFieldTypeOptions: ruleFieldTypeOptions,
      selectedRuleTypeOption: ruleType,
      selectedRuleFieldTypeOption: selectedRuleFieldTypeOption,
      ruleFieldValues: ruleFieldValues
    });
  }

  componentDidMount() {
    var formulaParts = this.props.rule.formula.split(",");
    var ruleFieldValues = [];
    for (var i = 0; i < formulaParts.length; i++) {
      var formulaPart = formulaParts[i];
      var hasFieldValue = !(formulaPart.indexOf(":") === -1);
      if (hasFieldValue) {
        var ruleFieldValue = {};
        // MACDIndicator
        ruleFieldValue.label = formulaPart.substr(0, formulaPart.indexOf(":"));
        // NumIndicator
        ruleFieldValue.ruleFieldName = this.getFieldType(ruleFieldValue.label);
        // [ClosePrice,1,1]
        if (ruleFieldValue.ruleFieldName === "NumIndicator") {
          var argsValues = formulaPart
            .substr(formulaPart.indexOf(":") + 1)
            .split(":");
          ruleFieldValue.value = {
            indicatorArgs: this.generateIndicatorArgs(argsValues)
          };
        } else {
          ruleFieldValue.value = {
            value: formulaPart.substr(formulaPart.indexOf(":") + 1)
          };
        }

        ruleFieldValues[i] = ruleFieldValue;
      } else {
        var ruleFieldValue = {};
        ruleFieldValue.label = formulaPart;
        ruleFieldValue.ruleFieldName = formulaPart;
        ruleFieldValue.value = {
          value: ""
        };
        ruleFieldValue[i] = ruleFieldValue;
      }
    }
    var selectedFieldTypeValue = ruleFieldValues
      .map(ruleFieldValue => ruleFieldValue.ruleFieldName)
      .join("|");

    this.selectRuleTypeAndFieldTypeAndArgs(
      this.props.rule.type,
      selectedFieldTypeValue,
      ruleFieldValues
    );
  }

  generateIndicatorArgs(argsValues) {
    return argsValues.map(argsValue => {
      if (isInt(argsValue)) {
        return {
          label: "Integer",
          value: argsValue
        };
      } else if (isInt(argsValue)) {
        return {
          label: "Double",
          value: argsValue
        };
      } else {
        return {
          label: argsValue,
          value: ""
        };
      }
    });
  }

  parseRule(rule) {
    this.selectRuleType(
      this.props.ruleTypeSelectOptions.find(e => e.classz === rule.type)
    );
  }
  validate() {
    if (
      this.state.ruleName &&
      this.state.ruleDescription &&
      this.state.selectedRuleTypeOption &&
      this.state.ruleFieldValues &&
      this.state.selectedRuleTypeOption &&
      this.state.selectedRuleTypeOption.value &&
      Object.values(this.state.ruleFieldValues).length ===
        this.state.selectedRuleTypeOption.value.length
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
        return res + ":" + ruleFieldValue.value.value;
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
        id: this.props.rule.id,
        name: this.state.ruleName,
        description: this.state.ruleDescription,
        type: this.state.selectedRuleTypeOption.classz,
        formula: formula
      };
      axiosPostWithAuth("/api/rule/addRule", rule)
        .then(res => {
          alertSuccess("Saved rule successfully.");
          this.props.onEditRuleSuccess(res, this.props.index);
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
    this.setState({
      ruleFieldTypes: ruleFieldTypes,
      ruleFieldTypeOptions: ruleFieldTypeOptions,
      selectedRuleTypeOption: option,
      selectedRuleFieldTypeOption: selectedRuleFieldTypeOption
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
                    case "Number":
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

      case "Number":
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
                title="Edit A Rule"
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
