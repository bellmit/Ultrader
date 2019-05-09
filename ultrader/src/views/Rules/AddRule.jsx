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
import "react-select/dist/react-select.css";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";

import {
  axiosGetWithAuth,
  axiosPostWithAuth,
  handleResponse
} from "helpers/UrlHelper";

var booleanOptions = [
  { value: "true", label: "true" },
  { value: "false", label: "false" }
];

export default class AddRuleComp extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.validate = this.validate.bind(this);
    this.saveRule = this.saveRule.bind(this);
    this.state = {
      ruleName: "",
      ruleDescription: "",
      ruleFieldTypes: [],
      ruleFieldTypeOptions: [],
      ruleFields: {},
      selectedRuleTypeOption: {},
      selectedRuleFieldType: "",
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
    console.log(res);
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
      console.log(this.state);
      let formulaParts = this.state.ruleFieldValues.map((value, i) => {
        return this.getStringForRuleFieldValue(value);
      });

      let formula = formulaParts.join(",");
      let rule = {
        name: this.state.ruleName,
        description: this.state.ruleDescription,
        type: this.state.selectedRuleTypeOption.label,
        formula: formula
      };
      console.log(rule);
      /*axiosPostWithAuth("/api/rule/addRule", rule)
        .then(handleResponse)
        .then(res => {
          alert("Saved rule " + res);
        })
        .catch(error => {
          alert(error);
        });*/
    } else {
      alert("All fields need to be filled");
    }
  }

  populateRuleFields(option) {
    let selectedRuleFieldType = option ? option.value : "";
    this.setState({
      selectedRuleFieldType: selectedRuleFieldType
    });
  }

  populateRuleFieldTypes(option) {
    let ruleFieldTypes = option ? option.value : [];

    let ruleFieldTypeOptions = option
      ? option.value.map(ruleFieldType => {
          return { label: ruleFieldType, value: ruleFieldType };
        })
      : [];
    let selectedRuleFieldType = option ? option.value[0] : "";
    this.setState({
      ruleFieldTypes: ruleFieldTypes,
      ruleFieldTypeOptions: ruleFieldTypeOptions,
      selectedRuleTypeOption: option,
      selectedRuleFieldType: selectedRuleFieldType
    });
  }

  setValueAndPopulateIndicatorArgs(ruleFieldName, ruleFieldValue, index) {
    switch (ruleFieldName) {
      case "NumIndicator":
        if (ruleFieldValue && ruleFieldValue.args) {
          let indicatorName = ruleFieldValue.label;
          console.log(ruleFieldValue);
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
    if (this.state.selectedRuleFieldType) {
      let ruleFieldNames = this.state.selectedRuleFieldType.split("|");
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
    console.log("setRuleFieldIndicatorFieldValue");
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
                                pattern="[0-9]*"
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
            pattern="[0-9]*"
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
                            onChange={value =>
                              this.populateRuleFieldTypes(value)
                            }
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
                              placeholder="Single Select"
                              name="singleSelect"
                              value={this.state.selectedRuleFieldType}
                              options={this.state.ruleFieldTypeOptions}
                              onChange={value => this.populateRuleFields(value)}
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
