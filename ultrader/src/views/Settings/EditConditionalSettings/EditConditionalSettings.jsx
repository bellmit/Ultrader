import React, { Component } from "react";
import { FormGroup, FormControl, ControlLabel } from "react-bootstrap";
import Select from "react-select";
import Button from "components/CustomButton/CustomButton.jsx";

import axios from "axios";

import Card from "components/Card/Card.jsx";

import SettingsForm from "./SettingsForm.jsx";

import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";

var marketTrendOptions = [
  { value: "BULL", label: "Bull" },
  { value: "BEAR", label: "Bear" },
  { value: "NORMAL", label: "Normal" },
  { value: "SUPER_BULL", label: "Super Bull" },
  { value: "SUPER_BEAR", label: "Super Bear" }
];

var marketTrendValues = ["BULL", "BEAR", "NORMAL", "SUPER_BULL", "SUPER_BEAR"];

var selectSettings = [
  "TRADE_PERIOD_SECOND",
  "TRADE_STOCK_LIST",
  "TRADE_BUY_ORDER_TYPE",
  "TRADE_SELL_ORDER_TYPE"
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

class EditConditionalSettingsCompComp extends Component {
  constructor(props) {
    super(props);

    this.initData();
    this.saveSettings = this.saveSettings.bind(this);
    this.selectMarketTrendOption = this.selectMarketTrendOption.bind(this);
    this.selectConditionalOption = this.selectConditionalOption.bind(this);

    this.onExchangeInputChange = this.onExchangeInputChange.bind(this);

    this.setSelectedOptions = this.setSelectedOptions.bind(this);
    this.setStrategiesSelectedOptions = this.setStrategiesSelectedOptions.bind(
      this
    );

    var selectedConditionalSettingOptions = marketTrendValues.reduce(function(
      selectedConditionalSettingOptions,
      marketTrendValue,
      i
    ) {
      selectedConditionalSettingOptions[marketTrendValue] = {};
      return selectedConditionalSettingOptions;
    },
    {});

    this.state = {
      selectedMarketTrendOption: marketTrendOptions[0],
      selectedMarketTrend: marketTrendOptions[0].value,

      selectedConditionalSettingOptions: selectedConditionalSettingOptions
    };
  }

  componentDidMount() {}

  initData() {
    var promises = [];
    promises.push(axiosGetWithAuth("/api/setting/getConditionalSettings"));
    promises.push(axiosGetWithAuth("/api/strategy/getStrategies"));
    promises.push(axiosGetWithAuth("/api/asset/getAssetLists"));

    Promise.all(promises)
      .then(responses => {
        this.props.onGetConditionalSettingsSuccess(responses[0]);

        /*****************************************************************************/
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

        /*****************************************************************************/
        var assetLists = responses[2].data;
        var assetListOptions = assetLists.map(assetList => {
          return { label: assetList.name, value: assetList.name };
        });

        this.setState({
          assetListOptions: assetListOptions
        });

        /*****************************************************************************/

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
        console.log(error);
        alertError(error);
      });
  }

  setStrategiesSelectedOptions(buyStrategyOptions, sellStrategyOptions) {
    let selectedConditionalSettingOptions = this.state
      .selectedConditionalSettingOptions;
    marketTrendValues.forEach((marketTrendValue, index) => {
      /*****************************************************************************/
      var buyStrategyOption = buyStrategyOptions.find(
        e =>
          e.value ==
          this.props.conditionalSettings[marketTrendValue]["TRADE_BUY_STRATEGY"]
      );
      var selectedBuyStrategyOption = buyStrategyOption
        ? buyStrategyOption
        : {};

      selectedConditionalSettingOptions[marketTrendValue][
        "TRADE_BUY_STRATEGY"
      ] = selectedBuyStrategyOption;

      /*****************************************************************************/
      var sellStrategyOption = sellStrategyOptions.find(
        e =>
          e.value ==
          this.props.conditionalSettings[marketTrendValue][
            "TRADE_SELL_STRATEGY"
          ]
      );
      var selectedSellStrategyOption = sellStrategyOption
        ? sellStrategyOption
        : {};

      selectedConditionalSettingOptions[marketTrendValue][
        "TRADE_SELL_STRATEGY"
      ] = selectedSellStrategyOption;
    });
    this.setState({
      selectedConditionalSettingOptions: selectedConditionalSettingOptions
    });
  }

  setAssetListsSelectedOptions(assetListOptions) {
    let selectedConditionalSettingOptions = this.state
      .selectedConditionalSettingOptions;
    marketTrendValues.forEach((marketTrendValue, index) => {
      /*****************************************************************************/
      var assetListOption = assetListOptions.find(
        e =>
          e.value ==
          this.props.conditionalSettings[marketTrendValue]["TRADE_STOCK_LIST"]
      );
      let selectedAssetListOption = assetListOption ? assetListOption : {};
      selectedConditionalSettingOptions[marketTrendValue][
        "TRADE_STOCK_LIST"
      ] = selectedAssetListOption;
    });
    this.setState({
      selectedConditionalSettingOptions: selectedConditionalSettingOptions
    });
  }

  setSelectedOptions() {
    let selectedConditionalSettingOptions = this.state
      .selectedConditionalSettingOptions;
    marketTrendValues.forEach((marketTrendValue, index) => {
      if (
        this.props.conditionalSettings[marketTrendValue]["TRADE_PERIOD_SECOND"]
      ) {
        var periodOption = intervalOptions.find(
          e =>
            e.value ==
            this.props.conditionalSettings[marketTrendValue][
              "TRADE_PERIOD_SECOND"
            ]
        );
        periodOption = periodOption ? periodOption : {};
        selectedConditionalSettingOptions[marketTrendValue][
          "TRADE_PERIOD_SECOND"
        ] = periodOption;
      }

      /*****************************************************************************/
      if (
        this.props.conditionalSettings[marketTrendValue]["TRADE_EXCHANGE_LIST"]
      ) {
        var exchanges = this.props.conditionalSettings[marketTrendValue][
          "TRADE_EXCHANGE_LIST"
        ].split(",");
        var selectedExchangeOptions = exchangeOptions.filter(exchangeOption => {
          return exchanges.includes(exchangeOption.value);
        });

        selectedConditionalSettingOptions[marketTrendValue][
          "TRADE_EXCHANGE_LIST"
        ] = selectedExchangeOptions;
      }
      /*****************************************************************************/
      if (
        this.props.conditionalSettings[marketTrendValue]["TRADE_BUY_ORDER_TYPE"]
      ) {
        var buyOrderTypeOption = orderTypeOptions.find(
          e =>
            e.value ==
            this.props.conditionalSettings[marketTrendValue][
              "TRADE_BUY_ORDER_TYPE"
            ]
        );
        let selectedBuyOrderTypeOption = buyOrderTypeOption
          ? buyOrderTypeOption
          : {};

        selectedConditionalSettingOptions[marketTrendValue][
          "TRADE_BUY_ORDER_TYPE"
        ] = selectedBuyOrderTypeOption;
      }
      /*****************************************************************************/
      if (
        this.props.conditionalSettings[marketTrendValue][
          "TRADE_SELL_ORDER_TYPE"
        ]
      ) {
        var sellOrderTypeOption = orderTypeOptions.find(
          e =>
            e.value ==
            this.props.conditionalSettings[marketTrendValue][
              "TRADE_SELL_ORDER_TYPE"
            ]
        );
        let selectedSellOrderTypeOption = sellOrderTypeOption
          ? sellOrderTypeOption
          : {};

        selectedConditionalSettingOptions[marketTrendValue][
          "TRADE_SELL_ORDER_TYPE"
        ] = selectedSellOrderTypeOption;
      }
    });

    this.setState({
      selectedConditionalSettingOptions: selectedConditionalSettingOptions
    });
  }

  selectConditionalOption(trend, settingName, option) {
    let selectedConditionalOption = option ? option : {};
    let selectedConditionalSettingOptions = this.state
      .selectedConditionalSettingOptions;
    selectedConditionalSettingOptions[trend][settingName] = option;
    this.setState({
      selectedConditionalSettingOptions: selectedConditionalSettingOptions
    });
    this.props.onAddConditionalSetting(trend, settingName, option.value);
  }

  onExchangeInputChange(trend, option) {
    let selectedExchangeOptions = option ? option : [];
    let selectedConditionalSettingOptions = this.state
      .selectedConditionalSettingOptions;
    selectedConditionalSettingOptions[trend][
      "TRADE_EXCHANGE_LIST"
    ] = selectedExchangeOptions;
    this.setState({
      selectedConditionalSettingOptions: selectedConditionalSettingOptions
    });
    let optionStringList = selectedExchangeOptions.map(o => o.value).join(",");
    this.props.onAddConditionalSetting(
      trend,
      "TRADE_EXCHANGE_LIST",
      optionStringList
    );
  }

  selectMarketTrendOption(option) {
    let selectedMarketTrendOption = option ? option : [];
    let selectedMarketTrend = option
      ? option.value
      : this.state.selectedMarketTrend;
    this.setState({
      selectedMarketTrendOption: selectedMarketTrendOption,
      selectedMarketTrend: selectedMarketTrend
    });
  }

  saveSettings() {
    var conditionalSettings = [];

    marketTrendValues.forEach((marketTrendValue, index) => {
      for (var key in this.props.conditionalSettings[marketTrendValue]) {
        if (
          this.props.conditionalSettings[marketTrendValue].hasOwnProperty(key)
        ) {
          conditionalSettings.push({
            marketTrend: marketTrendValue,
            settingName: key,
            settingValue: this.props.conditionalSettings[marketTrendValue][key]
          });
        }
      }
    });

    var promises = [];
    promises.push(
      axiosPostWithAuth(
        "/api/setting/addConditionalSettings",
        conditionalSettings
      )
    );

    Promise.all(promises)
      .then(responses => {
        alertSuccess(
          "Saved " + responses[0].data.length + " conditional settings."
        );
        axiosGetWithAuth("/api/setting/restart");
      })
      .catch(error => {
        console.log(error);

        alertError(error);
      });
  }

  render() {
    return (
      <div className="main-content">
        <FormGroup>
          <ControlLabel>Trend</ControlLabel>
          <Select
            name="trend"
            options={marketTrendOptions}
            value={this.state.selectedMarketTrendOption}
            onChange={option => this.selectMarketTrendOption(option)}
          />
        </FormGroup>
        <SettingsForm
          {...this.props}
          conditionalSettings={this.props.conditionalSettings}
          key={this.state.selectedMarketTrend}
          selectedMarketTrend={this.state.selectedMarketTrend}
          selectedConditionalSettingOptions={
            this.state.selectedConditionalSettingOptions
          }
          selectedExchangeOptions={this.state.selectedExchangeOptions}
          onExchangeInputChange={this.onExchangeInputChange}
          selectConditionalOption={this.selectConditionalOption}
          orderTypeOptions={orderTypeOptions}
          assetListOptions={this.state.assetListOptions}
          sellStrategyOptions={this.state.sellStrategyOptions}
          buyStrategyOptions={this.state.buyStrategyOptions}
        />
        <div style={{ textAlign: "center" }}>
          <Button bsStyle="info" fill wd onClick={this.saveSettings} pullRight>
            Finish
          </Button>
        </div>
      </div>
    );
  }
}

export default EditConditionalSettingsCompComp;
