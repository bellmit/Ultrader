import Dashboard from "containers/Dashboard/Dashboard.jsx";
import SettingsWizard from "containers/Settings/SettingsWizard.jsx";
import EditSettings from "containers/Settings/EditSettings.jsx";
import EditConditionalSettings from "containers/Settings/EditConditionalSettings.jsx";

import GreetingMonitor from "containers/Monitors/GreetingMonitor.jsx";
import Rules from "containers/Rules/Rules.jsx";
import Users from "containers/Users/Users.jsx";
import Strategies from "containers/Strategies/Strategies.jsx";
import Positions from "containers/Positions/Positions.jsx";
import AssetLists from "containers/AssetLists/AssetLists.jsx";
import HistoryMarketData from "containers/HistoryMarketData/HistoryMarketData.jsx";
import Backtest from "containers/Backtest/Backtest.jsx";
import Optimization from "containers/Optimization/Optimization.jsx";
import PendingOrders from "containers/Orders/PendingOrders.jsx";
import HistoryOrders from "containers/Orders/HistoryOrders.jsx";

var dashboardRoutes = [
  {
    path: "/dashboard",
    name: "Dashboard",
    icon: "pe-7s-display1",
    component: Dashboard,
    requiredRoleId: 3
  },
  {
    path: "/users",
    name: "Users",
    icon: "pe-7s-users",
    component: Users,
    requiredRoleId: 1
  },
  {
    collapse: true,
    path: "/settings",
    name: "Settings",
    state: "openSettings",
    icon: "pe-7s-config",
    requiredRoleId: 2,
    views: [
      {
        path: "/settings/editSettings",
        tour: "tour-settings",
        name: "Settings",
        mini: "S",
        component: EditSettings,
        requiredRoleId: 2
      },
      {
        path: "/settings/editConditionalSettings",
        name: "Conditional Settings",
        mini: "CS",
        component: EditConditionalSettings,
        requiredRoleId: 2
      }
    ]
  },
  {
    path: "/rules",
    name: "Rules",
    icon: "pe-7s-file",
    component: Rules,
    requiredRoleId: 3
  },
  {
    path: "/strategies",
    name: "Strategies",
    icon: "pe-7s-copy-file",
    component: Strategies,
    requiredRoleId: 3
  },
  {
    path: "/positions",
    name: "Positions",
    icon: "pe-7s-portfolio",
    component: Positions,
    requiredRoleId: 3
  },
  {
    path: "/assetLists",
    name: "Asset Lists",
    icon: "pe-7s-note2",
    component: AssetLists,
    requiredRoleId: 3
  },
  {
    collapse: true,
    path: "/orders",
    name: "Orders",
    state: "openOrders",
    icon: "pe-7s-cash",
    requiredRoleId: 3,
    views: [
      {
        path: "/orders/pendingOrders",
        name: "Pending Orders",
        mini: "PO",
        component: PendingOrders,
        requiredRoleId: 3
      },
      {
        path: "/orders/historyOrders",
        name: "History Orders",
        mini: "HO",
        component: HistoryOrders,
        requiredRoleId: 3
      }
    ]
  },
  {
    path: "/historyMarketData",
    name: "History Market Data",
    icon: "pe-7s-server",
    component: HistoryMarketData,
    requiredRoleId: 2
  },
  {
    path: "/backtest",
    name: "Backtest",
    icon: "pe-7s-science",
    component: Backtest,
    requiredRoleId: 2
  },
  {
    path: "/optimization",
    name: "Optimization",
    icon: "pe-7s-target",
    component: Optimization,
    requiredRoleId: 2
  },

  { redirect: true, path: "/", pathTo: "/dashboard", name: "Dashboard" }
];
export default dashboardRoutes;
