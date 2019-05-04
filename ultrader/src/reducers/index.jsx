import { combineReducers } from 'redux';
import authentication from './Authentication/Authentication';
import settings from './settings';
import global from './global';
import greetingMonitor from './Monitors/GreetingMonitor';
import addRule from './Rules/AddRule';
import rules from './Rules/Rules';
import settingsWizard from './Settings/SettingsWizard';
import strategies from './Strategies/Strategies';
import positions from './Positions/Positions';
import orders from './Orders/Orders';


export default combineReducers({
  authentication,
  global,
  addRule,
  rules,
  strategies,
  positions,
  orders,
  greetingMonitor,
  settingsWizard
})