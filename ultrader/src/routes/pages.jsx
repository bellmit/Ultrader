import LoginPage from "containers/Pages/LoginPage.jsx";
import RegisterPage from "containers/Pages/RegisterPage.jsx";

var pagesRoutes = [
  {
    path: "/pages/login-page",
    name: "Login Page",
    mini: "LP",
    component: LoginPage
  },
  {
    path: "/pages/register-page",
    name: "Register",
    mini: "RP",
    component: RegisterPage
  }
];

export default pagesRoutes;
