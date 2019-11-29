import * as ACTION_TYPES from "actions/Settings/SettingsActions";

const initialState = {
  settings: {},
  conditionalSettings: {
    BULL: {},
    BEAR: {},
    NORMAL: {},
    SUPER_BULL: {},
    SUPER_BEAR: {}
  }
};

const settings = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.ADD_SETTING:
      return {
        ...state,
        settings: {
          ...state.settings,
          [action.key]: action.value
        }
      };
    case ACTION_TYPES.GET_SETTINGS_SUCCESS:
      var settingsArray = action.response.data;
      var settings = {};
      settingsArray.forEach(setting => {
        settings[setting.name] = setting.value;
      });
      return {
        ...state,
        settings: settings
      };
    case ACTION_TYPES.ADD_CONDITIONAL_SETTING:
      return {
        ...state,
        conditionalSettings: {
          ...state.conditionalSettings,
          [action.trend]: {
            ...state.conditionalSettings[action.trend],
            [action.key]: action.value
          }
        }
      };
    case ACTION_TYPES.GET_CONDITIONAL_SETTINGS_SUCCESS:
      var conditionalSettings = state.conditionalSettings;
      var responseConditionalSettingsArray = action.response.data;
      responseConditionalSettingsArray.forEach(conditionalSetting => {
        conditionalSettings[conditionalSetting.marketTrend][
          conditionalSetting.settingName
        ] = conditionalSetting.settingValue;
      });

      return {
        ...state,
        conditionalSettings: conditionalSettings
      };
    default:
      return state;
  }
};

export default settings;
