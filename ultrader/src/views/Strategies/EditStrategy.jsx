/*
{
  "id": 9128,
  "name": "test",
  "description": "test",
  "type": "buy",
  "formula": "1&,2|,1^,1"
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
import { alertSuccess, alertError } from "helpers/AlertHelper";

var strategyTypeOptions = [
  { label: "Buy", value: "Buy" },
  { label: "Sell", value: "Sell" }
];

var operatorOptions = [
  { label: "and", value: "&" },
  { label: "or", value: "|" },
  { label: "xor", value: "^" }
];

export default class EditStrategyComp extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.validate = this.validate.bind(this);
    this.saveStrategy = this.saveStrategy.bind(this);
    this.addRule = this.addRule.bind(this);
    this.setSelectedValues = this.setSelectedValues.bind(this);
    this.deleteRule = this.deleteRule.bind(this);
    this.state = {
      strategyName: this.props.strategy.name,
      strategyDescription: this.props.strategy.description,
      selectedStrategyTypeOption: {},
      strategyRuleOptions: [],
      strategyOperatorOptions: [],
      selectedStrategyType: "",
      strategyRules: [""],
      strategyOperators: [""],
      ruleOptions: []
    };
  }

  deleteRule(index) {
    var strategyRules = this.state.strategyRules;
    var strategyRuleOptions = this.state.strategyRuleOptions;
    var strategyOperators = this.state.strategyOperators;
    var strategyOperatorOptions = this.state.strategyOperatorOptions;
    strategyRules.splice(index, 1);
    strategyRuleOptions.splice(index, 1);
    strategyOperators.splice(index, 1);
    strategyOperatorOptions.splice(index, 1);
    this.setState({
      strategyRules: strategyRules,
      strategyRuleOptions: strategyRuleOptions,
      strategyOperators: strategyOperators,
      strategyOperatorOptions: strategyOperatorOptions
    });
  }

  componentDidMount() {
    axiosGetWithAuth("/api/rule/getRules")
      .then(res => {
        this.props.onGetRulesSuccess(res);
        this.setSelectedValues();
      })
      .catch(error => {
        alertError(error);
      });
  }

  setSelectedValues(ruleOptions) {
    var selectedStrategyTypeOption = strategyTypeOptions.find(
      e => e.value == this.props.strategy.type
    );
    this.setStrategyType(selectedStrategyTypeOption);
    var formulaParts = this.props.strategy.formula.split(",");
    var strategyRules = [];
    var strategyRuleOptions = [];
    var strategyOperators = [];
    var strategyOperatorOptions = [];
    for (var i = 0; i < formulaParts.length; i++) {
      var formulaPart = formulaParts[i];
      var r = /\d+/;
      var id = formulaPart.match(r);
      var operator = formulaPart.replace(id, "");
      var selectedStrategyRuleOption = this.state.ruleOptions.find(
        e => e.value == id
      );
      var selectedStrategyOperatorOption = operatorOptions.find(
        e => e.value == operator
      );
      strategyRules[i] = id;
      strategyRuleOptions[i] = selectedStrategyRuleOption;
      strategyOperators[i] = operator;
      strategyOperatorOptions[i] = selectedStrategyOperatorOption;
    }
    this.setState({
      strategyRules: strategyRules,
      strategyRuleOptions: strategyRuleOptions,
      strategyOperators: strategyOperators,
      strategyOperatorOptions: strategyOperatorOptions
    });
  }

  componentWillReceiveProps(nextProps) {
    let ruleOptions = nextProps.rules.map((item, index) => {
      return { label: item.name, value: item.id };
    });
    this.setState({ ruleOptions: ruleOptions });
  }

  validate() {
    if (
      this.state.strategyName &&
      this.state.strategyDescription &&
      this.state.selectedStrategyType &&
      this.state.strategyRules &&
      this.state.strategyOperators &&
      Object.values(this.state.strategyRules).length ===
        this.state.strategyRules.length
    ) {
      return true;
    } else {
      return false;
    }
  }

  saveStrategy() {
    if (this.validate()) {
      let formula = "";
      for (var i = 0; i < this.state.strategyRules.length - 1; i++) {
        formula +=
          this.state.strategyRules[i] + this.state.strategyOperators[i] + ",";
      }
      formula += this.state.strategyRules[this.state.strategyRules.length - 1];
      let strategy = {
        id: this.props.strategy.id,
        name: this.state.strategyName,
        description: this.state.strategyDescription,
        type: this.state.selectedStrategyType,
        formula: formula
      };
      axiosPostWithAuth("/api/strategy/addStrategy", strategy)
        .then(res => {
          alertSuccess("Saved strategy successfully.");
          this.props.onEditStrategySuccess(res);
        })
        .catch(error => {
          alertError(error);
        });
    } else {
      alertError("All fields need to be filled");
    }
  }
  setRuleValue(option, index) {
    let strategyRules = this.state.strategyRules;
    let strategyRuleOptions = this.state.strategyRuleOptions;
    strategyRules[index] = option ? option.value : "";
    strategyRuleOptions[index] = option ? option : "";
    this.setState({
      strategyRules: strategyRules,
      strategyRuleOptions: strategyRuleOptions
    });
  }

  setOperatorValue(option, index) {
    let strategyOperators = this.state.strategyOperators;
    let strategyOperatorOptions = this.state.strategyOperatorOptions;
    strategyOperators[index] = option ? option.value : "";
    strategyOperatorOptions[index] = option ? option : {};
    this.setState({
      strategyOperators: strategyOperators,
      strategyOperatorOptions: strategyOperatorOptions
    });
  }

  setStrategyType(option) {
    this.setState({
      selectedStrategyTypeOption: option,
      selectedStrategyType: option ? option.value : ""
    });
  }

  ruleFields() {
    return (
      <div>
        {this.state.strategyRules.map((item, index) => {
          return (
            <fieldset>
              <FormGroup>
                <ControlLabel className="col-sm-2">Rule</ControlLabel>
                <Col sm={7}>
                  <Select
                    placeholder={"rule" + (index + 1)}
                    name={"rule" + (index + 1)}
                    value={this.state.strategyRuleOptions[index]}
                    options={this.state.ruleOptions}
                    onChange={option => this.setRuleValue(option, index)}
                  />
                </Col>
                <Col sm={2}>
                  <Select
                    placeholder={"operator" + (index + 1)}
                    name={"operator" + (index + 1)}
                    value={this.state.strategyOperatorOptions[index]}
                    options={operatorOptions}
                    onChange={option => this.setOperatorValue(option, index)}
                  />
                </Col>
                <Col sm={1}>
                  <Button
                    onClick={() => {
                      this.deleteRule(index);
                    }}
                    bsStyle="danger"
                    simple
                    icon
                  >
                    <i className="fa fa-times" />
                  </Button>
                </Col>
              </FormGroup>
            </fieldset>
          );
        })}
      </div>
    );
  }

  addRule() {
    let strategyRules = [...this.state.strategyRules, ""];
    let strategyOperators = [...this.state.strategyOperators, ""];
    let strategyRuleOptions = [...this.state.strategyRuleOptions, {}];
    let strategyOperatorOptions = [...this.state.strategyOperatorOptions, {}];
    this.setState({
      strategyRules: strategyRules,
      strategyOperators: strategyOperators,
      strategyRuleOptions: strategyRuleOptions,
      strategyOperatorOptions: strategyOperatorOptions
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
                title="Add A Strategy"
                content={
                  <Form horizontal>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Strategy Name
                        </ControlLabel>
                        <Col sm={10}>
                          <FormControl
                            type="text"
                            value={this.state.strategyName}
                            onChange={e =>
                              this.setState({ strategyName: e.target.value })
                            }
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Strategy Description
                        </ControlLabel>
                        <Col sm={10}>
                          <FormControl
                            type="text"
                            value={this.state.strategyDescription}
                            onChange={e =>
                              this.setState({
                                strategyDescription: e.target.value
                              })
                            }
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Strategy Type
                        </ControlLabel>
                        <Col sm={10}>
                          <Select
                            placeholder="Strategy Type"
                            name="strategyType"
                            value={this.state.selectedStrategyTypeOption}
                            options={strategyTypeOptions}
                            onChange={option => this.setStrategyType(option)}
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <hr />
                    {this.ruleFields()}

                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2" />
                        <Col sm={10}>
                          <Button bsStyle="info" fill onClick={this.addRule}>
                            Add Rule
                          </Button>
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <hr />
                    <Button bsStyle="info" fill onClick={this.saveStrategy}>
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
