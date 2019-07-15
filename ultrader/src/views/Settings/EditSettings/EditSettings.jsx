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

var tradingPlatformOptions = [
  { value: "Alpaca", label: "Alpaca" },
  { value: "AlpacaPaper", label: "Alpaca Paper" }
];

var marketDataPlatformOptions = [
  { value: "IEX", label: "The Investor Exchange" },
  { value: "POLYGON", label: "Polygon API" }
];

var booleanOptions = [
  { value: "true", label: "true" },
  { value: "false", label: "false" }
];

var exchangeOptions = [
  { value: "NASDAQ", label: "NASDAQ" },
  { value: "AMEX", label: "AMEX" },
  { value: "ARCA", label: "ARCA" },
  { value: "BATS", label: "BATS" },
  { value: "NYSE", label: "NYSE" },
  { value: "NYSEARCA", label: "NYSEARCA" }
];

var orderTypeOptions = [
  { value: "market", label: "market" },
  { value: "limit", label: "limit" }
];

class EditSettingsComp extends Component {
  constructor(props) {
    super(props);

    this.initData();
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
    this.selectAutoTradingOption = this.selectAutoTradingOption.bind(this);
    this.selectBuyStrategyOption = this.selectBuyStrategyOption.bind(this);
    this.selectSellStrategyOption = this.selectSellStrategyOption.bind(this);
    this.selectBuyOrderTypeOption = this.selectBuyOrderTypeOption.bind(this);
    this.selectSellOrderTypeOption = this.selectSellOrderTypeOption.bind(this);

    this.onExchangeInputChange = this.onExchangeInputChange.bind(this);

    this.setSelectedOptions = this.setSelectedOptions.bind(this);
    this.setStrategiesSelectedOptions = this.setStrategiesSelectedOptions.bind(
      this
    );

    this.state = {
      buyStrategyOptions: [],
      sellStrategyOptions: [],
      selectedTradingPlatformOption: {},
      selectedMarketDataPlatformOption: {},
      selectedExchangeOptions: [],
      selectedStrategyTemplateOption: {},
      selectedAutoTradingOption: {},
      selectedBuyStrategyOption: {},
      selectedSellStrategyOption: {},
      selectedBuyOrderTypeOption: {},
      selectedSellOrderTypeOption: {}
    };
  }

  componentDidMount() {}

  initData() {
    var promises = [];
    promises.push(axiosGetWithAuth("/api/setting/getSettings"));
    promises.push(axiosGetWithAuth("/api/strategy/getStrategies"));

    Promise.all(promises)
      .then(responses => {
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

        this.props.onGetSettingsSuccess(responses[0]);
        this.setSelectedOptions();
        this.setStrategiesSelectedOptions(
          buyStrategyOptions,
          sellStrategyOptions
        );
      })
      .catch(error => {
        alert(error);
      });
  }

  setStrategiesSelectedOptions(buyStrategyOptions, sellStrategyOptions) {
    /*****************************************************************************/
    var buyStrategyOption = buyStrategyOptions.find(
      e => e.value == this.props.settings["TRADE_BUY_STRATEGY"]
    );
    var selectedBuyStrategyOption = buyStrategyOption ? buyStrategyOption : {};
    this.setState({
      selectedBuyStrategyOption: selectedBuyStrategyOption
    });

    /*****************************************************************************/
    var sellStrategyOption = sellStrategyOptions.find(
      e => e.value == this.props.settings["TRADE_SELL_STRATEGY"]
    );
    var selectedSellStrategyOption = sellStrategyOption
      ? sellStrategyOption
      : {};
    this.setState({
      selectedSellStrategyOption: selectedSellStrategyOption
    });
  }

  setSelectedOptions() {
    /*****************************************************************************/
    var tradingPlatformOption = tradingPlatformOptions.find(
      e => e.value == this.props.settings["GLOBAL_TRADING_PLATFORM"]
    );
    var selectedTradingPlatformOption = tradingPlatformOption
      ? tradingPlatformOption
      : {};
    this.setState({
      selectedTradingPlatformOption: selectedTradingPlatformOption
    });

    /*****************************************************************************/
    var marketDataPlatformOption = marketDataPlatformOptions.find(
      e => e.value == this.props.settings["GLOBAL_MARKETDATA_PLATFORM"]
    );
    let selectedMarketDataPlatformOption = marketDataPlatformOption
      ? marketDataPlatformOption
      : {};
    this.setState({
      selectedMarketDataPlatformOption: selectedMarketDataPlatformOption
    });

    /*****************************************************************************/
    var autoTradingOption = booleanOptions.find(
      e => e.value == this.props.settings["GLOBAL_AUTO_TRADING_ENABLE"]
    );
    let selectedAutoTradingOption = autoTradingOption ? autoTradingOption : {};
    this.setState({
      selectedAutoTradingOption: selectedAutoTradingOption
    });

    /*****************************************************************************/
    var exchanges = this.props.settings["TRADE_EXCHANGE_LIST"].split(",");
    var selectedExchangeOptions = exchangeOptions.filter(exchangeOption => {
      return exchanges.includes(exchangeOption.value);
    });
    this.setState({
      selectedExchangeOptions: selectedExchangeOptions
    });

    /*****************************************************************************/
    var buyOrderTypeOption = orderTypeOptions.find(
      e => e.value == this.props.settings["TRADE_BUY_ORDER_TYPE"]
    );
    let selectedBuyOrderTypeOption = buyOrderTypeOption
      ? buyOrderTypeOption
      : {};
    this.setState({
      selectedBuyOrderTypeOption: selectedBuyOrderTypeOption
    });

    /*****************************************************************************/
    var sellOrderTypeOption = orderTypeOptions.find(
      e => e.value == this.props.settings["TRADE_SELL_ORDER_TYPE"]
    );
    let selectedSellOrderTypeOption = sellOrderTypeOption
      ? sellOrderTypeOption
      : {};
    this.setState({
      selectedSellOrderTypeOption: selectedSellOrderTypeOption
    });
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

  selectAutoTradingOption(option) {
    let selectedAutoTradingOption = option ? option : {};
    this.setState({
      selectedAutoTradingOption: selectedAutoTradingOption
    });
    this.props.onAddSetting("GLOBAL_AUTO_TRADING_ENABLE", option.value);
  }

  selectBuyStrategyOption(option) {
    let selectedBuyStrategyOption = option ? option : {};
    this.setState({
      selectedBuyStrategyOption: selectedBuyStrategyOption
    });
    this.props.onAddSetting("TRADE_BUY_STRATEGY", option.value);
  }

  selectSellStrategyOption(option) {
    let selectedSellStrategyOption = option ? option : {};
    this.setState({
      selectedSellStrategyOption: selectedSellStrategyOption
    });
    this.props.onAddSetting("TRADE_SELL_STRATEGY", option.value);
  }

  selectBuyOrderTypeOption(option) {
    let selectedBuyOrderTypeOption = option ? option : {};
    this.setState({
      selectedBuyOrderTypeOption: selectedBuyOrderTypeOption
    });
    this.props.onAddSetting("TRADE_BUY_ORDER_TYPE", option.value);
  }

  selectSellOrderTypeOption(option) {
    let selectedSellOrderTypeOption = option ? option : {};
    this.setState({
      selectedSellOrderTypeOption: selectedSellOrderTypeOption
    });
    this.props.onAddSetting("TRADE_SELL_ORDER_TYPE", option.value);
  }

  onExchangeInputChange(option) {
    let selectedExchangeOptions = option ? option : [];
    this.setState({
      selectedExchangeOptions: selectedExchangeOptions
    });
    let optionStringList = selectedExchangeOptions.map(o => o.value).join(",");
    this.props.onAddSetting("TRADE_EXCHANGE_LIST", optionStringList);
  }

  saveSettings() {
    var settings = [];
    for (var key in this.props.settings) {
      if (this.props.settings.hasOwnProperty(key)) {
        settings.push({ name: key, value: this.props.settings[key] });
      }
    }

    var promises = [];
    promises.push(axiosPostWithAuth("/api/setting/addSettings", settings));

    Promise.all(promises)
      .then(responses => {
        alert("Saved " + responses[0].data.length + " settings.");
        axiosGetWithAuth("/api/setting/restart");
      })
      .catch(error => {
        alert(error);
      });
  }

  render() {
    const steps = [
      {
        name: "Licenses",
        component: <Step1 {...this.props} />
      },
      {
        name: "Global",
        component: (
          <Step2
            {...this.props}
            selectedTradingPlatformOption={
              this.state.selectedTradingPlatformOption
            }
            selectedMarketDataPlatformOption={
              this.state.selectedMarketDataPlatformOption
            }
            selectedAutoTradingOption={this.state.selectedAutoTradingOption}
            selectTradingPlatformOption={this.selectTradingPlatformOption}
            selectMarketDataPlatformOption={this.selectMarketDataPlatformOption}
            selectAutoTradingOption={this.selectAutoTradingOption}
          />
        )
      },
      {
        name: "Trading",
        component: (
          <FinalStep
            {...this.props}
            saveSettings={this.saveSettings}
            selectedExchangeOptions={this.state.selectedExchangeOptions}
            onExchangeInputChange={this.onExchangeInputChange}
            buyStrategyOptions={this.state.buyStrategyOptions}
            sellStrategyOptions={this.state.sellStrategyOptions}
            selectedBuyStrategyOption={this.state.selectedBuyStrategyOption}
            selectedSellStrategyOption={this.state.selectedSellStrategyOption}
            selectBuyStrategyOption={this.selectBuyStrategyOption}
            selectSellStrategyOption={this.selectSellStrategyOption}
            selectedBuyOrderTypeOption={this.state.selectedBuyOrderTypeOption}
            selectedSellOrderTypeOption={this.state.selectedSellOrderTypeOption}
            selectBuyOrderTypeOption={this.selectBuyOrderTypeOption}
            selectSellOrderTypeOption={this.selectSellOrderTypeOption}
            orderTypeOptions={orderTypeOptions}
          />
        )
      }
    ];
    return (
      <Grid fluid>
        <StepZilla
          steps={steps}
          stepsNavigation={false}
          nextButtonCls="btn btn-prev btn-info btn-fill pull-right btn-wd"
          backButtonCls="btn btn-next btn-default btn-fill pull-left btn-wd"
        />
      </Grid>
    );
  }
}

export default EditSettingsComp;
