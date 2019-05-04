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

  saveRule() {
    if (this.validate()) {
      let formula = "";
      formula +=
        this.state.selectedRuleTypeOption.value[0] +
        ":" +
        this.state.ruleFieldValues[0];
      for (var i = 1; i < this.state.selectedRuleTypeOption.value.length; i++) {
        formula +=
          "," +
          this.state.selectedRuleTypeOption.value[i] +
          ":" +
          this.state.ruleFieldValues[i];
      }
      let rule = {
        name: this.state.ruleName,
        description: this.state.ruleDescription,
        type: this.state.selectedRuleTypeOption.label,
        formula: formula
      };
      console.log(rule);
      axiosPostWithAuth("/api/rule/addRule", rule)
        .then(handleResponse)
        .then(res => {
          alert("Saved rule " + res);
        })
        .catch(error => {
          alert(error);
        });
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
    ruleFieldValues[index] = ruleFieldValue;
    this.setState({
      ruleFieldValues: ruleFieldValues
    });
  }

  ruleField(ruleFieldName, index) {
    switch (ruleFieldName) {
      case "NumIndicator":
        return (
          <Select
            placeholder="Number Indicator"
            name="numIndicator"
            value={this.state.ruleFieldValues[index]}
            options={this.props.indicatorSelectOptions["NumIndicator"]}
            onChange={option =>
              this.setRuleFieldValue(
                ruleFieldName,
                option ? option.value : "",
                index
              )
            }
          />
        );
      case "Boolean":
        return (
          <Select
            placeholder="Number Indicator"
            name="numIndicator"
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
    console.log(this.props);
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
