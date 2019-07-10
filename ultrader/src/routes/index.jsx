import Pages from "layouts/Pages/Pages.jsx";
import Setup from "containers/layouts/Setup.jsx";
import Dashboard from "containers/layouts/Dashboard.jsx";

var indexRoutes = [
  { path: "/pages", name: "Pages", component: Pages, private: false},
  { path: "/setup", name: "Setup", component: Setup, private: true},
  { path: "/", name: "Home", component: Dashboard, private: true}
];

export default indexRoutes;
