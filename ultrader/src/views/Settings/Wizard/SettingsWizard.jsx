import React, { Component } from "react";
// react component that creates a form divided into multiple steps
import StepZilla from "react-stepzilla";
import { Grid, Row, Col } from "react-bootstrap";

import "./tabwidth.css";

import axios from "axios";

import Card from "components/Card/Card.jsx";

import Step1 from "./Step1.jsx";
import Step2 from "./Step2.jsx";
import FinalStep from "./FinalStep.jsx";

import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";

class SettingsWizardComp extends Component {
  constructor(props) {
    super(props);
    this.saveSettings = this.saveSettings.bind(this);
    this.selectTradingPlatformOption = this.selectTradingPlatformOption.bind(
      this
    );
    this.selectMarketDataPlatformOption = this.selectMarketDataPlatformOption.bind(
      this
    );
    this.selectStrategyTemplateOption = this.selectStrategyTemplateOption.bind(
      this
    );

    this.onExchangeInputChange = this.onExchangeInputChange.bind(this);
    this.state = {
      selectedTradingPlatformOption: {},
      selectedMarketDataPlatformOption: {},
      selectedExchangeOptions: [],
      selectedStrategyTemplateOption: {}
    };
  }

  selectTradingPlatformOption(option) {
    let selectedTradingPlatformOption = option ? option : {};
    this.setState({
      selectedTradingPlatformOption: selectedTradingPlatformOption
    });
    this.props.onAddSetting("GLOBAL_TRADING_PLATFORM", option.value);
  }

  selectMarketDataPlatformOption(option) {
    let selectedMarketDataPlatformOption = option ? option : {};
    this.setState({
      selectedMarketDataPlatformOption: selectedMarketDataPlatformOption
    });
    this.props.onAddSetting("GLOBAL_MARKETDATA_PLATFORM", option.value);
  }

  selectStrategyTemplateOption(option) {
    let selectedStrategyTemplateOption = option ? option : {};
    this.setState({
      selectedStrategyTemplateOption: selectedStrategyTemplateOption
    });
  }

  onExchangeInputChange(option) {
    let selectedExchangeOptions = option ? option : [];
    this.setState({
      selectedExchangeOptions: selectedExchangeOptions
    });
    let optionStringList = selectedExchangeOptions.map(o => o.value).join(",");
    this.props.onAddSetting("TRADE_EXCHANGE_LIST", optionStringList);
    this.props.onAddSetting("TRADE_WHITE_LIST_ENABLE", false);
    this.props.onAddSetting("TRADE_PRICE_LIMIT_MAX", 2000);
    this.props.onAddSetting("TRADE_PRICE_LIMIT_MIN", 1);
    this.props.onAddSetting("TRADE_PERIOD_SECOND", 300);
    this.props.onAddSetting("TRADE_BUY_MAX_LIMIT", "5%");
    this.props.onAddSetting("TRADE_VOLUME_LIMIT_MAX", -1);
    this.props.onAddSetting("TRADE_VOLUME_LIMIT_MIN", -1);
    this.props.onAddSetting("TRADE_BUY_HOLDING_LIMIT", 20);
    this.props.onAddSetting("TRADE_SELL_ORDER_TYPE", "market");
    this.props.onAddSetting("TRADE_BUY_ORDER_TYPE", "market");
  }

  saveSettings() {
    var settings = [];
    for (var key in this.props.settings) {
      if (this.props.settings.hasOwnProperty(key)) {
        settings.push({ name: key, value: this.props.settings[key] });
      }
    }

    var strategyBundle = JSON.parse(
      this.state.selectedStrategyTemplateOption.strategy
    );
    var promises = [];
    promises.push(axiosPostWithAuth("/api/setting/addSettings", settings));
    promises.push(axiosPostWithAuth("/api/strategy/import", strategyBundle));

    Promise.all(promises)
      .then(responses => {
        alertSuccess(
          "Saved " +
            responses[0].data.length +
            " settings and strategy template"
        );
        axiosGetWithAuth("/api/setting/restart");
        window.location = "/";
      })
      .catch(error => {
        alertError(error);
      });
  }

  render() {
    const steps = [
      {
        name: "Licenses",
        component: <Step1 {...this.props} />
      },
      {
        name: "Trading",
        component: (
          <Step2
            {...this.props}
            selectedTradingPlatformOption={
              this.state.selectedTradingPlatformOption
            }
            selectedMarketDataPlatformOption={
              this.state.selectedMarketDataPlatformOption
            }
            selectedExchangeOptions={this.state.selectedExchangeOptions}
            selectTradingPlatformOption={this.selectTradingPlatformOption}
            selectMarketDataPlatformOption={this.selectMarketDataPlatformOption}
            onExchangeInputChange={this.onExchangeInputChange}
          />
        )
      },
      {
        name: "Strategy",
        component: (
          <FinalStep
            {...this.props}
            saveSettings={this.saveSettings}
            selectedStrategyTemplateOption={
              this.state.selectedStrategyTemplateOption
            }
            selectStrategyTemplateOption={this.selectStrategyTemplateOption}
          />
        )
      }
    ];
    return (
      <Grid fluid>
        <Row>
          <Col md={8} mdOffset={2}>
            <Card
              wizard
              id="wizardCard"
              textCenter
              title="Setting Wizard"
              category="This is the first time you are using Ultrader, this wizard will help you set up the basic settings needed to get started, and we will provide you some example strategy templates to experiment with."
              content={
                <StepZilla
                  steps={steps}
                  stepsNavigation={false}
                  nextButtonCls="btn btn-prev btn-info btn-fill pull-right btn-wd"
                  backButtonCls="btn btn-next btn-default btn-fill pull-left btn-wd"
                />
              }
            />
          </Col>
        </Row>
      </Grid>
    );
  }
}

export default SettingsWizardComp;
