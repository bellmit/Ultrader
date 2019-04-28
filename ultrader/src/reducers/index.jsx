import { combineReducers } from 'redux';
import settings from './settings';
import global from './global';
import greetingMonitor from './Monitors/GreetingMonitor';
import addRule from './Rules/AddRule';
import rules from './Rules/Rules';
import settingsWizard from './Settings/SettingsWizard';
import strategies from './Strategies/Strategies';


export default combineReducers({
  global,
  addRule,
  rules,
  strategies,
  greetingMonitor,
  settingsWizard
})