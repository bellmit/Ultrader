import { combineReducers } from 'redux';
import authentication from './Authentication/Authentication';
import global from './layouts/Dashboard';
import greetingMonitor from './Monitors/GreetingMonitor';
import rules from './Rules/Rules';
import settingsWizard from './Settings/SettingsWizard';
import strategies from './Strategies/Strategies';
import positions from './Positions/Positions';
import orders from './Orders/Orders';


export default combineReducers({
  authentication,
  global,
  rules,
  strategies,
  positions,
  orders,
  greetingMonitor,
  settingsWizard
})