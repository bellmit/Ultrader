import Dashboard from "containers/Dashboard/Dashboard.jsx";
import SettingsWizard from "containers/Settings/SettingsWizard.jsx";

import GreetingMonitor from "containers/Monitors/GreetingMonitor.jsx";
import AddRule from "containers/Rules/AddRule.jsx";
import Rules from "containers/Rules/Rules.jsx";
import AddStrategy from "containers/Strategies/AddStrategy.jsx";
import Strategies from "containers/Strategies/Strategies.jsx";
import Positions from "containers/Positions/Positions.jsx";
import PendingOrders from "containers/Orders/PendingOrders.jsx";
import HistoryOrders from "containers/Orders/HistoryOrders.jsx";

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
    icon: "pe-7s-config",
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
    path: "/rules",
    name: "Rules",
    icon: "pe-7s-config",
    component: Rules
  },
  {
    path: "/strategies",
    name: "Strategies",
    icon: "pe-7s-config",
    component: Strategies
  },
  {
    path: "/positions",
    name: "Positions",
    icon: "pe-7s-graph1",
    component: Positions
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
  { redirect: true, path: "/", pathTo: "/dashboard", name: "Dashboard" }
];
export default dashboardRoutes;
