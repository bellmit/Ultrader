
import * as ACTION_TYPES from 'actions/Settings/SettingsActions';

const initialState = {
     settings: {
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
    default:
      return state
  }
}

export default settings;