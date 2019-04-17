import { combineReducers } from 'redux';
import settings from './settings';
import global from './global';
import greetingMonitor from './Monitors/GreetingMonitor';
import settingsWizard from './Settings/SettingsWizard';


export default combineReducers({
  global,
  greetingMonitor,
  settingsWizard
})