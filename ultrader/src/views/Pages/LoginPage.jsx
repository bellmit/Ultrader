import React, { Component } from "react";
import {
  Grid,
  Row,
  Col,
  FormGroup,
  ControlLabel,
  FormControl
} from "react-bootstrap";

import Card from "components/Card/Card.jsx";

import Button from "components/CustomButton/CustomButton.jsx";
import Checkbox from "components/CustomCheckbox/CustomCheckbox.jsx";
import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";
import axios from "axios";

class LoginPageComp extends Component {
  constructor(props) {
    super(props);
    this.state = {
      username: "",
      password: "",
      submitted: false,
      cardHidden: true
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.login = this.login.bind(this);
  }

  componentDidMount() {
    axiosGetWithAuth("/api/user/hasUsers")
      .then(res => {
        // if there is no users, redirect to register
        if (!res.data) {
          window.location = "/#/pages/register-page";
        }
      })
      .catch(error => {
        console.log(error);
        alertError(error);
      });

    setTimeout(
      function() {
        this.setState({ cardHidden: false });
      }.bind(this),
      700
    );
  }

  login(username, password) {
    axiosPostWithAuth("/api/auth/signin", {
      username: username,
      password: password
    })
      .then(user => {
        if (!user) {
          alertError("Invalid Username or Password!");
        } else {
          let userObj = {
            token: user.data.accessToken,
            roleId: user.data.roleId
          };
          let userStateObj = {
            roleId: user.data.roleId
          };
          localStorage.setItem("user", JSON.stringify(userObj));
          this.props.onLoginSuccess(userStateObj);
          if (user.data.setup) {
            window.location = "/#/Dashboard";
          } else {
            window.location = "/#/setup/wizard";
          }
        }

        return user;
      })
      .catch(err => {
        const error = err.response;
        console.log(error);
        if (error.status === 401 || error.status === 403) {
          alertError("Invalid Username or Password!");
        } else if (error.status === 500) {
          alertError(
            "Server is not responding, please make sure the server is started."
          );
        } else {
          alertError(error);
        }
      });
  }

  handleChange(e) {
    const { name, value } = e.target;
    this.setState({ [name]: value });
  }

  handleSubmit(e) {
    e.preventDefault();

    this.setState({ submitted: true });
    const { username, password } = this.state;
    const { dispatch } = this.props;
    if (username && password) {
      this.login(username, password);
    }
  }

  render() {
    const { loggingIn } = this.props;
    const { username, password, submitted } = this.state;
    return (
      <Grid>
        <Row>
          <Col md={4} sm={6} mdOffset={4} smOffset={3}>
            <form onSubmit={this.handleSubmit}>
              <Card
                hidden={this.state.cardHidden}
                textCenter
                title="Login"
                content={
                  <div>
                    <FormGroup>
                      <ControlLabel>Username</ControlLabel>
                      <FormControl
                        placeholder="Username"
                        name="username"
                        value={username}
                        onChange={this.handleChange}
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Password</ControlLabel>
                      <FormControl
                        placeholder="Password"
                        type="password"
                        name="password"
                        value={password}
                        onChange={this.handleChange}
                      />
                    </FormGroup>
                  </div>
                }
                legend={
                  <Button
                    bsStyle="info"
                    fill
                    wd
                    onClick={this.handleSubmit}
                    type="submit"
                  >
                    Login
                  </Button>
                }
                ftTextCenter
              />
            </form>
          </Col>
        </Row>
      </Grid>
    );
  }
}

export default LoginPageComp;
