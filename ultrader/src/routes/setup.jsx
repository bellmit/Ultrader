import SettingsWizard from "containers/Settings/SettingsWizard.jsx";

var setupRoutes = [
  {
    path: "/setup/wizard",
    name: "Settings Wizard",
    mini: "SW",
    component: SettingsWizard,
    requiredRoleId: 2
  }
];

export default setupRoutes;
