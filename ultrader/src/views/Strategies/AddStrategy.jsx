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

import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";

var strategyTypeOptions = [
  { label: "buy", value: "buy" },
  { label: "sell", value: "sell" }
];

var operatorOptions = [
  { label: "and", value: "&" },
  { label: "or", value: "|" },
  { label: "xor", value: "^" }
];

export default class AddStrategyComp extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.validate = this.validate.bind(this);
    this.saveStrategy = this.saveStrategy.bind(this);
    this.addRule = this.addRule.bind(this);
    this.state = {
      strategyName: "",
      strategyDescription: "",
      selectedStrategyType: "",
      strategyRules: [""],
      strategyOperators: [""],
      ruleOptions: []
    };

    axiosGetWithAuth("/api/rule/getRules")
      .then(res => {
        this.props.onGetRulesSuccess(res);
      })
      .catch(error => {
        alert(error);
      });
  }

  componentDidMount() {}

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
        name: this.state.strategyName,
        description: this.state.strategyDescription,
        type: this.state.selectedStrategyType,
        formula: formula
      };
      axiosPostWithAuth("/api/strategy/addStrategy", strategy)
        .then(res => {
          alert("Saved strategy successfully.");
          this.props.onAddStrategySuccess(res);
        })
        .catch(error => {
          alert(error);
        });
    } else {
      alert("All fields need to be filled");
    }
  }

  setRuleValue(option, index) {
    let strategyRules = this.state.strategyRules;
    strategyRules[index] = option ? option.value : "";
    this.setState({
      strategyRules: strategyRules
    });
  }

  setOperatorValue(option, index) {
    let strategyOperators = this.state.strategyOperators;
    strategyOperators[index] = option ? option.value : "";
    this.setState({
      strategyOperators: strategyOperators
    });
  }

  setStrategyType(option) {
    this.setState({
      selectedStrategyType: option.value
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
                <Col sm={8}>
                  <Select
                    placeholder={"rule" + (index + 1)}
                    name={"rule" + (index + 1)}
                    value={this.state.strategyRules[index]}
                    options={this.state.ruleOptions}
                    onChange={option => this.setRuleValue(option, index)}
                  />
                </Col>
                <Col sm={2}>
                  <Select
                    placeholder={"operator" + (index + 1)}
                    name={"operator" + (index + 1)}
                    value={this.state.strategyOperators[index]}
                    options={operatorOptions}
                    onChange={option => this.setOperatorValue(option, index)}
                  />
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
    this.setState({
      strategyRules: strategyRules,
      strategyOperators: strategyOperators
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
                            value={this.state.selectedStrategyType}
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
