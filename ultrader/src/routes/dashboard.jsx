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
    path: "/rules",
    name: "Rules",
    icon: "pe-7s-note2",
    component: Rules
  },
  {
    path: "/strategies",
    name: "Strategies",
    icon: "pe-7s-note2",
    component: Strategies
  },
  {
    path: "/positions",
    name: "Positions",
    icon: "pe-7s-note2",
    component: Positions
  },
  {
    path: "/orders",
    name: "Orders",
    icon: "pe-7s-note2",
    component: PendingOrders
  },
  { redirect: true, path: "/", pathTo: "/dashboard", name: "Dashboard" }
];
export default dashboardRoutes;
