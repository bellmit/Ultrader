
import * as ACTION_TYPES from 'actions/Settings/SettingsWizardActions';

const initialState = {
     settings: {}
};

const settingsWizard = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.ADD_SETTING:
        return {
          ...state,
          settings: {
             ...state.settings,
             [action.key]: action.value
          }

        };
    default:
      return state
  }
}

export default settingsWizard;