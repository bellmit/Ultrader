import Dashboard from "containers/Dashboard/Dashboard.jsx";
import SettingsWizard from "containers/Settings/SettingsWizard.jsx";
import EditSettings from "containers/Settings/EditSettings.jsx";

import GreetingMonitor from "containers/Monitors/GreetingMonitor.jsx";
import AddRule from "containers/Rules/AddRule.jsx";
import Rules from "containers/Rules/Rules.jsx";
import AddStrategy from "containers/Strategies/AddStrategy.jsx";
import Strategies from "containers/Strategies/Strategies.jsx";
import Positions from "containers/Positions/Positions.jsx";
import AssetLists from "containers/AssetLists/AssetLists.jsx";
import Backtest from "containers/Backtest/Backtest.jsx";
import PendingOrders from "containers/Orders/PendingOrders.jsx";
import HistoryOrders from "containers/Orders/HistoryOrders.jsx";

var dashboardRoutes = [
  {
    path: "/dashboard",
    name: "Dashboard",
    icon: "pe-7s-display1",
    component: Dashboard
  },
  {
        path: "/settings/editSettings",
        name: "Settings",
        icon: "pe-7s-config",
        component: EditSettings
  },
  {
    path: "/rules",
    name: "Rules",
    icon: "pe-7s-copy-file",
    component: Rules
  },
  {
    path: "/strategies",
    name: "Strategies",
    icon: "pe-7s-file",
    component: Strategies
  },
  {
    path: "/positions",
    name: "Positions",
    icon: "pe-7s-portfolio",
    component: Positions
  },
  {
    path: "/assetLists",
    name: "Asset Lists",
    icon: "pe-7s-cash",
    component: AssetLists
  },
  {
    collapse: true,
    path: "/orders",
    name: "Orders",
    state: "openOrders",
    icon: "pe-7s-note2",
    views: [
      {
        path: "/orders/pendingOrders",
        name: "Pending Orders",
        mini: "PO",
        component: PendingOrders
      },
      {
        path: "/orders/historyOrders",
        name: "History Orders",
        mini: "HO",
        component: HistoryOrders
      }
    ]
  },
    {
      path: "/backtest",
      name: "Backtest",
      icon: "pe-7s-graph1",
      component: Backtest
    },
  { redirect: true, path: "/", pathTo: "/dashboard", name: "Dashboard" }
];
export default dashboardRoutes;
