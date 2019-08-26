import { combineReducers } from 'redux';
import authentication from './Authentication/Authentication';
import global from './layouts/Dashboard';
import greetingMonitor from './Monitors/GreetingMonitor';
import rules from './Rules/Rules';
import settings from './Settings/Settings';
import strategies from './Strategies/Strategies';
import positions from './Positions/Positions';
import assetLists from './AssetLists/AssetLists';
import orders from './Orders/Orders';
import dashboard from './Dashboard/Dashboard';
import backtest from './Backtest/Backtest';


export default combineReducers({
  authentication,
  global,
  rules,
  strategies,
  positions,
  assetLists,
  orders,
  greetingMonitor,
  settings,
  dashboard,
  backtest
})