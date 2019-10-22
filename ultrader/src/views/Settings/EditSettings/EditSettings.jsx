import React, { Component } from "react";
// react component that creates a form divided into multiple steps
import StepZilla from "react-stepzilla";
import { Row, Col, Nav, NavItem, Tab, Tabs } from "react-bootstrap";
import Button from "components/CustomButton/CustomButton.jsx";

import "./tabwidth.css";

import axios from "axios";

import Card from "components/Card/Card.jsx";

import LicensesStep from "./LicensesStep.jsx";
import GlobalStep from "./GlobalStep.jsx";
import TradingStep from "./TradingStep.jsx";
import AlpacaStep from "./AlpacaStep.jsx";

import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";

var tradingPlatformOptions = [
  { value: "Alpaca", label: "Alpaca" },
  { value: "AlpacaPaper", label: "Alpaca Paper" }
];

var marketDataPlatformOptions = [
  { value: "IEX", label: "The Investor Exchange" },
  { value: "POLYGON", label: "Polygon API" }
];

var marginCheckOptions = [
  { value: "both", label: "both" },
  { value: "entry", label: "entry" },
  { value: "exit", label: "exit" }
];

var emailNotificationOptions = [
  { value: "all", label: "all" },
  { value: "none", label: "none" }
];

var booleanOptions = [
  { value: "true", label: "true" },
  { value: "false", label: "false" }
];

var intervalOptions = [
  { value: "60", label: "1 Minute" },
  { value: "300", label: "5 Minutes" },
  { value: "900", label: "15 Minutes" },
  { value: "86400", label: "1 Day" }
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
    this.selectPeriodOption = this.selectPeriodOption.bind(this);
    this.selectAssetListOption = this.selectAssetListOption.bind(this);
    this.selectOption = this.selectOption.bind(this);

    this.onExchangeInputChange = this.onExchangeInputChange.bind(this);

    this.setSelectedOptions = this.setSelectedOptions.bind(this);
    this.setStrategiesSelectedOptions = this.setStrategiesSelectedOptions.bind(
      this
    );

    this.state = {
      selectedOptions: {},
      assetListOptions: [],
      buyStrategyOptions: [],
      sellStrategyOptions: [],
      selectedTradingPlatformOption: {},
      periodOption: {},
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
    promises.push(axiosGetWithAuth("/api/asset/getAssetLists"));

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

        var assetLists = responses[2].data;
        var assetListOptions = assetLists.map(assetList => {
          return { label: assetList.name, value: assetList.name };
        });

        this.setState({
          assetListOptions: assetListOptions
        });

        this.props.onGetSettingsSuccess(responses[0]);
        this.setSelectedOptions();
        this.setStrategiesSelectedOptions(
          buyStrategyOptions,
          sellStrategyOptions
        );
        this.setAssetListsSelectedOptions(assetListOptions);

        this.setState({
          assetListOptions: assetListOptions
        });
      })
      .catch(error => {
        alertError(error);
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

  setAssetListsSelectedOptions(assetListOptions) {
    /*****************************************************************************/
    var assetListOption = assetListOptions.find(
      e => e.value == this.props.settings["TRADE_STOCK_LIST"]
    );
    let selectedAssetListOption = assetListOption ? assetListOption : {};
    this.setState({
      selectedAssetListOption: selectedAssetListOption
    });
  }

  setSelectedOptions() {
    var periodOption = intervalOptions.find(
      e => e.value == this.props.settings["TRADE_PERIOD_SECOND"]
    );
    periodOption = periodOption ? periodOption : {};
    this.setState({
      periodOption: periodOption
    });
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

    /*****************************************************************************/
    var marginCheckOption = marginCheckOptions.find(
      e => e.value == this.props.settings["ALPACA_DTMC"]
    );
    let selectedMarginCheckOption = marginCheckOption ? marginCheckOption : {};
    this.setState({
      selectedOptions: {
        ...this.state.selectedOptions,
        ALPACA_DTMC: selectedMarginCheckOption
      }
    });

    /*****************************************************************************/
    var noShortingTradeOption = booleanOptions.find(
      e => e.value == this.props.settings["ALPACA_NO_SHORTING"]
    );
    let selectedNoShortingTradeOption = noShortingTradeOption
      ? noShortingTradeOption
      : {};
    this.setState({
      selectedOptions: {
        ...this.state.selectedOptions,
        ALPACA_NO_SHORTING: selectedNoShortingTradeOption
      }
    });

    /*****************************************************************************/
    var suspendTradeOption = booleanOptions.find(
      e => e.value == this.props.settings["ALPACA_SUSPEND_TRADE"]
    );
    let selectedSuspendTradeOption = suspendTradeOption
      ? suspendTradeOption
      : {};
    this.setState({
      selectedOptions: {
        ...this.state.selectedOptions,
        ALPACA_SUSPEND_TRADE: selectedSuspendTradeOption
      }
    });

    /*****************************************************************************/
    var emailNotificationOption = emailNotificationOptions.find(
      e => e.value == this.props.settings["ALPACA_TRADE_CONFIRM_EMAIL"]
    );
    let selectedEmailNotificationOption = emailNotificationOption
      ? emailNotificationOption
      : {};
    this.setState({
      selectedOptions: {
        ...this.state.selectedOptions,
        ALPACA_TRADE_CONFIRM_EMAIL: selectedEmailNotificationOption
      }
    });

    /*****************************************************************************/
    var useMarginOption = booleanOptions.find(
      e => e.value == this.props.settings["ALPACA_USE_MARGIN"]
    );
    let selectedUseMarginOption = useMarginOption ? useMarginOption : {};
    this.setState({
      selectedOptions: {
        ...this.state.selectedOptions,
        ALPACA_USE_MARGIN: selectedUseMarginOption
      }
    });
  }

  selectPeriodOption(option) {
    let periodOption = option ? option : {};
    this.setState({
      periodOption: periodOption
    });
    this.props.onAddSetting("TRADE_PERIOD_SECOND", option.value);
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

  selectAssetListOption(option) {
    let selectedAssetListOption = option ? option : {};
    this.setState({
      selectedAssetListOption: selectedAssetListOption
    });
    this.props.onAddSetting("TRADE_STOCK_LIST", option.value);
  }

  selectAssetListOption(option) {
    let selectedAssetListOption = option ? option : {};
    this.setState({
      selectedAssetListOption: selectedAssetListOption
    });
    this.props.onAddSetting("TRADE_STOCK_LIST", option.value);
  }

  selectAssetListOption(option) {
    let selectedAssetListOption = option ? option : {};
    this.setState({
      selectedAssetListOption: selectedAssetListOption
    });
    this.props.onAddSetting("TRADE_STOCK_LIST", option.value);
  }

  selectOption(settingName, option) {
    console.log(settingName);
    console.log(option);
    let selectedOption = option ? option : {};
    let selectedOptions = this.state.selectedOptions;
    selectedOptions[settingName] = option;
    this.setState({
      selectedOptions: selectedOptions
    });
    this.props.onAddSetting(settingName, option.value);
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
        alertSuccess("Saved " + responses[0].data.length + " settings.");
        axiosGetWithAuth("/api/setting/restart");
      })
      .catch(error => {
        alertError(error);
      });
  }

  render() {
    const steps = [
      {
        name: "Licenses",
        component: <LicensesStep {...this.props} />
      },
      {
        name: "Global",
        component: (
          <GlobalStep
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
          <TradingStep
            {...this.props}
            selectedExchangeOptions={this.state.selectedExchangeOptions}
            periodOption={this.state.periodOption}
            selectPeriodOption={this.selectPeriodOption}
            onExchangeInputChange={this.onExchangeInputChange}
            buyStrategyOptions={this.state.buyStrategyOptions}
            sellStrategyOptions={this.state.sellStrategyOptions}
            selectedBuyStrategyOption={this.state.selectedBuyStrategyOption}
            selectedSellStrategyOption={this.state.selectedSellStrategyOption}
            selectBuyStrategyOption={this.selectBuyStrategyOption}
            selectSellStrategyOption={this.selectSellStrategyOption}
            selectedBuyOrderTypeOption={this.state.selectedBuyOrderTypeOption}
            selectedSellOrderTypeOption={this.state.selectedSellOrderTypeOption}
            selectedAssetListOption={this.state.selectedAssetListOption}
            selectBuyOrderTypeOption={this.selectBuyOrderTypeOption}
            selectSellOrderTypeOption={this.selectSellOrderTypeOption}
            selectAssetListOption={this.selectAssetListOption}
            orderTypeOptions={orderTypeOptions}
            assetListOptions={this.state.assetListOptions}
          />
        )
      },
      {
        name: "Alpaca",
        component: (
          <AlpacaStep
            {...this.props}
            selectedOptions={this.state.selectedOptions}
            selectOption={this.selectOption}
            marginCheckOptions={marginCheckOptions}
            emailNotificationOptions={emailNotificationOptions}
            booleanOptions={booleanOptions}
          />
        )
      }
    ];
    return (
      <div className="main-content">
        <Tabs defaultActiveKey={steps[0].name} animation={false}>
          {steps.map(step => {
            return (
              <Tab
                eventKey={step.name}
                title={step.name}
                style={{ padding: 0 }}
              >
                {step.component}
              </Tab>
            );
          })}
        </Tabs>
        <div style={{ textAlign: "center" }}>
          <Button bsStyle="info" fill wd onClick={this.saveSettings} pullRight>
            Finish
          </Button>
        </div>
      </div>
    );
  }
}

export default EditSettingsComp;
