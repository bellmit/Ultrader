import React, { Component } from "react";
import {
  Grid,
  Row,
  Col,
  Media,
  FormControl,
  FormGroup,
  ControlLabel
} from "react-bootstrap";
import { axiosPostWithAuth } from "helpers/UrlHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";

import Card from "components/Card/Card.jsx";

import Button from "components/CustomButton/CustomButton.jsx";

class RegisterPageComp extends Component {
  constructor(props) {
    super(props);
    this.vForm = this.refs.vForm;
    this.handleUsernameChange = this.handleUsernameChange.bind(this);
    this.handlePasswordChange = this.handlePasswordChange.bind(this);
    this.handleCfPasswordChange = this.handleCfPasswordChange.bind(this);
    this.handleRegisterSubmit = this.handleRegisterSubmit.bind(this);
    this.state = {
      // Register
      username: "",
      password: "",
      cfpassword: "",
      usernameError: null,
      passwordError: null,
      cfpasswordError: null
    };
  }

  handleUsernameChange(event) {
    this.setState({
      username: event.target.value
    });
    !event.target.value
      ? this.setState({
          usernameError: (
            <small className="text-danger">Username is required.</small>
          )
        })
      : this.setState({ usernameError: null });
  }

  handlePasswordChange(event) {
    this.setState({
      password: event.target.value
    });
    event.target.value.length < 6
      ? this.setState({
          passwordError: (
            <small className="text-danger">
              You must enter a password of at least 6 characters.
            </small>
          )
        })
      : this.setState({ passwordError: null });
  }
  handleCfPasswordChange(event) {
    this.setState({
      cfpassword: event.target.value
    });
    event.target.value !== this.state.password
      ? this.setState({
          cfpasswordError: (
            <small className="text-danger">Passwords do not match.</small>
          )
        })
      : this.setState({ cfpasswordError: null });
  }
  handleRegisterSubmit() {
    !this.state.username
      ? this.setState({
          usernameError: (
            <small className="text-danger">Username is required.</small>
          )
        })
      : this.setState({ usernameError: null });
    this.state.password.length < 6
      ? this.setState({
          passwordError: (
            <small className="text-danger">
              You must enter a password of at least 6 characters.
            </small>
          )
        })
      : this.setState({ passwordError: null });
    this.state.cfpassword !== this.state.password
      ? this.setState({
          cfpasswordError: (
            <small className="text-danger">Passwords do not match.</small>
          )
        })
      : this.setState({ cfpasswordError: null });

    axiosPostWithAuth("/api/user/addRootUser", {
      username: this.state.username,
      passwordHash: this.state.password
    })
      .then(response => {
        console.log(response);
        let user = response.data;
        if (user) {
          alertSuccess("Registeration succeeded! Redirecting to login page.");
          window.location = "/#/pages/login-page";
        }
      })
      .catch(error => {
        alertError(error);
      });
  }

  render() {
    return (
      <Grid>
        <Row>
          <Col md={8} mdOffset={2}>
            <div className="header-text">
              <h2>Ultrader</h2>
              <h4>
                You will need to set up an admin account to start using this
                application, you will be redirected to a setup page after you
                first login.
              </h4>
              <hr />
            </div>
          </Col>
          <Col md={8} mdOffset={2}>
            <form>
              <Card
                plain
                content={
                  <div>
                    <FormGroup>
                      <ControlLabel style={{ color: "white" }}>
                        Username: <span className="star">*</span>
                      </ControlLabel>
                      <FormControl
                        type="text"
                        name="username"
                        onChange={this.handleUsernameChange}
                      />
                      {this.state.usernameError}
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel style={{ color: "white" }}>
                        Password: <span className="star">*</span>
                      </ControlLabel>
                      <FormControl
                        type="password"
                        name="password"
                        onChange={event => this.handlePasswordChange(event)}
                      />
                      {this.state.passwordError}
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel style={{ color: "white" }}>
                        Confirm password: <span className="star">*</span>
                      </ControlLabel>
                      <FormControl
                        type="password"
                        name="cfpassword"
                        onChange={event => this.handleCfPasswordChange(event)}
                      />
                      {this.state.cfpasswordError}
                    </FormGroup>
                    <div className="category" style={{ color: "white" }}>
                      <span className="star">*</span> Required fields
                    </div>
                  </div>
                }
                ftTextCenter
                legend={
                  <Button
                    bsStyle="info"
                    fill
                    pullRight
                    onClick={this.handleRegisterSubmit.bind(this)}
                  >
                    Register
                  </Button>
                }
              />
            </form>
          </Col>
        </Row>
      </Grid>
    );
  }
}

export default RegisterPageComp;
