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

var componentTypeOptions = [
  { label: "Rule", value: "Rule" },
  { label: "Strategy", value: "Strategy" }
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
    this.deleteRule = this.deleteRule.bind(this);
    this.loadData = this.loadData.bind(this);
    this.generateComponentValueSelect = this.generateComponentValueSelect.bind(
      this
    );
    this.state = {
      strategyName: "",
      strategyDescription: "",
      selectedStrategyTypeOption: {},
      strategyOperatorOptions: [],
      selectedStrategyType: "",
      strategyComponents: [
        {
          selectedType: componentTypeOptions[0].value,
          selectedComponentValue: "",
          selectedTypeOption: componentTypeOptions[0],
          selectedComponentValueOption: {}
        }
      ],
      strategyOperators: [""],
      ruleOptions: [],
      buyStrategyOptions: [],
      sellStrategyOptions: []
    };
  }

  loadData() {
    var promises = [];
    promises.push(axiosGetWithAuth("/api/rule/getRules"));
    promises.push(axiosGetWithAuth("/api/strategy/getStrategies"));

    Promise.all(promises)
      .then(responses => {
        this.props.onGetRulesSuccess(responses[0]);

        var strategies = responses[1].data;
        var buyStrategyOptions = strategies
          .filter(strategy => {
            return strategy.type === "Buy";
          })
          .map(strategy => {
            return { label: strategy.name, value: strategy.id };
          });
        this.setState({
          buyStrategyOptions: buyStrategyOptions
        });
        var sellStrategyOptions = strategies
          .filter(strategy => {
            return strategy.type === "Sell";
          })
          .map(strategy => {
            return { label: strategy.name, value: strategy.id };
          });
        this.setState({
          sellStrategyOptions: sellStrategyOptions
        });
      })
      .catch(error => {
        alertError(error);
      });
  }

  componentDidMount() {
    this.loadData();
  }

  deleteRule(index) {
    var strategyComponents = this.state.strategyComponents;
    var strategyOperators = this.state.strategyOperators;
    var strategyOperatorOptions = this.state.strategyOperatorOptions;
    strategyComponents.splice(index, 1);
    strategyOperators.splice(index, 1);
    strategyOperatorOptions.splice(index, 1);
    this.setState({
      strategyComponents: strategyComponents,
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
      this.state.strategyComponents &&
      this.state.strategyOperators &&
      this.state.strategyComponents.every(strategyComponent => {
        return Object.values(strategyComponent).every(x => {
          return x !== null && x !== "";
        });
      }) &&
      this.state.strategyOperators.every((strategyOperator, index) => {
        //last item in array, always true
        return (
          this.state.strategyOperators.length - 1 == index ||
          (strategyOperator !== null && strategyOperator !== "")
        );
      })
    ) {
      return true;
    } else {
      return false;
    }
  }

  saveStrategy() {
    if (this.validate()) {
      let formula = "";
      for (var i = 0; i < this.state.strategyComponents.length - 1; i++) {
        formula +=
          (this.state.strategyComponents[i].selectedType == "Strategy"
            ? "S"
            : "") +
          this.state.strategyComponents[i].selectedComponentValue +
          this.state.strategyOperators[i] +
          ",";
      }
      formula +=
        (this.state.strategyComponents[this.state.strategyComponents.length - 1]
          .selectedType == "Strategy"
          ? "S"
          : "") +
        this.state.strategyComponents[this.state.strategyComponents.length - 1]
          .selectedComponentValue;

      let strategy = {
        name: this.state.strategyName,
        description: this.state.strategyDescription,
        type: this.state.selectedStrategyType,
        formula: formula
      };
      axiosPostWithAuth("/api/strategy/addStrategy", strategy)
        .then(res => {
          alertSuccess("Saved strategy successfully.");
          this.props.onAddStrategySuccess(res);
        })
        .catch(error => {
          alertError(error);
        });
    } else {
      alertError("All fields need to be filled");
    }
  }

  setComponentValue(option, index) {
    let strategyComponents = this.state.strategyComponents;
    let newStrategyComponent = strategyComponents[index];
    newStrategyComponent.selectedComponentValue = option ? option.value : "";
    newStrategyComponent.selectedComponentValueOption = option ? option : {};
    strategyComponents[index] = newStrategyComponent;
    this.setState({
      strategyComponents: strategyComponents
    });
  }

  setComponentType(option, index) {
    let strategyComponents = this.state.strategyComponents;
    strategyComponents[index] = option
      ? {
          selectedType: option.value,
          selectedComponentValue: "",
          selectedTypeOption: option,
          selectedComponentValueOption: {}
        }
      : "";
    this.setState({
      strategyComponents: strategyComponents
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
      selectedStrategyType: option.value,
      strategyComponents: [
        {
          selectedType: componentTypeOptions[0].value,
          selectedComponentValue: "",
          selectedTypeOption: componentTypeOptions[0],
          selectedComponentValueOption: {}
        }
      ]
    });
  }

  ruleFields() {
    return (
      <div>
        {this.state.strategyComponents.map((item, index) => {
          return (
            <fieldset>
              <FormGroup>
                <Col sm={2}>
                  <Select
                    placeholder={"type"}
                    name={"type" + (index + 1)}
                    value={
                      this.state.strategyComponents[index].selectedTypeOption
                    }
                    options={componentTypeOptions}
                    onChange={option => this.setComponentType(option, index)}
                  />
                </Col>
                {this.generateComponentValueSelect(index)}
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
      </div>
    );
  }

  generateComponentValueSelect(index) {
    return this.state.strategyComponents[index].selectedType == "Rule" ? (
      <Col sm={7}>
        <Select
          placeholder={"rule" + (index + 1)}
          name={"rule" + (index + 1)}
          value={
            this.state.strategyComponents[index].selectedComponentValueOption
          }
          options={this.state.ruleOptions}
          onChange={option => this.setComponentValue(option, index)}
        />
      </Col>
    ) : this.state.selectedStrategyType == "Buy" ? (
      <Col sm={7}>
        <Select
          placeholder={"strategy" + (index + 1)}
          name={"strategy" + (index + 1)}
          value={
            this.state.strategyComponents[index].selectedComponentValueOption
          }
          options={this.state.buyStrategyOptions}
          onChange={option => this.setComponentValue(option, index)}
        />
      </Col>
    ) : (
      <Col sm={7}>
        <Select
          placeholder={"strategy" + (index + 1)}
          name={"strategy" + (index + 1)}
          value={
            this.state.strategyComponents[index].selectedComponentValueOption
          }
          options={this.state.sellStrategyOptions}
          onChange={option => this.setComponentValue(option, index)}
        />
      </Col>
    );
  }

  addRule() {
    let strategyComponents = [
      ...this.state.strategyComponents,
      {
        selectedType: componentTypeOptions[0].value,
        selectedComponentValue: "",
        selectedTypeOption: componentTypeOptions[0],
        selectedComponentValueOption: {}
      }
    ];
    let strategyOperators = [...this.state.strategyOperators, ""];
    let strategyOperatorOptions = [...this.state.strategyOperatorOptions, {}];
    this.setState({
      strategyComponents: strategyComponents,
      strategyOperators: strategyOperators,
      strategyOperatorOptions: strategyOperatorOptions
    });
  }

  render() {
    console.log(this.state);
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
                    {this.state.selectedStrategyType ? this.ruleFields() : ""}

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
