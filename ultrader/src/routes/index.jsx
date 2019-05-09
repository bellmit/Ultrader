import Pages from "layouts/Pages/Pages.jsx";
import Dashboard from "containers/layouts/Dashboard.jsx";

var indexRoutes = [
  { path: "/pages", name: "Pages", component: Pages, private: false},
  { path: "/", name: "Home", component: Dashboard, private: true}
];

export default indexRoutes;
