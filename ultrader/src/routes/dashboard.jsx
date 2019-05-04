import Dashboard from "views/Dashboard/Dashboard.jsx";
import SettingsWizard from "containers/Settings/SettingsWizard.jsx";

import GreetingMonitor from "containers/Monitors/GreetingMonitor.jsx";
import AddRule from "containers/Rules/AddRule.jsx";
import Rules from "containers/Rules/Rules.jsx";
import AddStrategy from "containers/Strategies/AddStrategy.jsx";
import Strategies from "containers/Strategies/Strategies.jsx";
import Positions from "containers/Positions/Positions.jsx";
import PendingOrders from "containers/Orders/PendingOrders.jsx";

var dashboardRoutes = [
  {
    path: "/dashboard",
    name: "Dashboard",
    icon: "pe-7s-graph",
    component: Dashboard
  },
  {
    collapse: true,
    path: "/settings",
    name: "Settings",
    state: "openSettings",
    icon: "pe-7s-note2",
    views: [
      {
        path: "/settings/wizard",
        name: "Settings Wizard",
        mini: "SW",
        component: SettingsWizard
      }
    ]
  },
  {
    collapse: true,
    path: "/monitors",
    name: "Monitors",
    state: "openMonitors",
    icon: "pe-7s-note2",
    views: [
      {
        path: "/monitors/greeting",
        name: "Greeting Monitor",
        mini: "GM",
        component: GreetingMonitor
      }
    ]
  },
  {
    collapse: true,
    path: "/rules",
    name: "Rules",
    state: "openRules",
    icon: "pe-7s-note2",
    views: [
      {
        path: "/rules/addRule",
        name: "Add Rule",
        mini: "AR",
        component: AddRule
      },
      {
        path: "/rules/rules",
        name: "Rules",
        mini: "R",
        component: Rules
      }
    ]
  },
  {
    collapse: true,
    path: "/strategies",
    name: "Strategies",
    state: "openStrategies",
    icon: "pe-7s-note2",
    views: [
      {
        path: "/strategies/addStrategy",
        name: "Add Strategy",
        mini: "AS",
        component: AddStrategy
      },
      {
        path: "/strategies/strategies",
        name: "Strategies",
        mini: "S",
        component: Strategies
      }
    ]
  },
  {
    collapse: true,
    path: "/positions",
    name: "Positions",
    state: "openPositions",
    icon: "pe-7s-note2",
    views: [
      {
        path: "/positions/positions",
        name: "Positions",
        mini: "P",
        component: Positions
      }
    ]
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
      }
    ]
  },
  { redirect: true, path: "/", pathTo: "/dashboard", name: "Dashboard" }
];
export default dashboardRoutes;
