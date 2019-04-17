import Dashboard from "views/Dashboard/Dashboard.jsx";
import SettingsWizard from "containers/Settings/SettingsWizard.jsx";

import GreetingMonitor from "containers/Monitors/GreetingMonitor.jsx";

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
  { redirect: true, path: "/", pathTo: "/dashboard", name: "Dashboard" }
];
export default dashboardRoutes;
