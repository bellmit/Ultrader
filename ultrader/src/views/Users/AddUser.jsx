import React from "react";

import axios from "axios";
import {
  Grid,
  Row,
  Col,
  FormGroup,
  ControlLabel,
  FormControl,
  HelpBlock,
  Form
} from "react-bootstrap";
import Select from "react-select";

import Card from "components/Card/Card.jsx";
import Button from "components/CustomButton/CustomButton.jsx";

import { axiosGetWithAuth, axiosPostWithAuth } from "helpers/UrlHelper";
import { tooltip } from "helpers/TooltipHelper";
import { alertSuccess, alertError } from "helpers/AlertHelper";

export default class AddUserComp extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.handleUsernameChange = this.handleUsernameChange.bind(this);
    this.handlePasswordChange = this.handlePasswordChange.bind(this);
    this.handleCfPasswordChange = this.handleCfPasswordChange.bind(this);
    this.saveUser = this.saveUser.bind(this);
    this.validate = this.validate.bind(this);
    this.state = {
      // Register
      username: "",
      password: "",
      cfpassword: "",
      usernameError: null,
      passwordError: null,
      cfpasswordError: null,
      selectedRoleOption: this.props.roleOptions[2]
    };
  }

  componentDidMount() {}

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

  validate() {
    return (
      this.state.username &&
      this.state.password.length >= 6 &&
      this.state.cfpassword == this.state.password &&
      this.state.selectedRoleOption
    );
  }

  saveUser() {
    if (this.validate()) {
      axiosPostWithAuth("/api/user/addUser", {
        username: this.state.username,
        passwordHash: this.state.password,
        roleId: this.state.selectedRoleOption.value
      })
        .then(res => {
          console.log(res);
          alertSuccess("Saved user successfully.");
          this.props.onAddUserSuccess(res);
        })
        .catch(error => {
          if (
            error.response &&
            error.response.status &&
            error.response.status == 409
          ) {
            alertError("Username or email already in use!");
          } else {
            alertError(error);
          }
        });
    } else {
      alertError("Please fix the errors.");
    }
  }

  render() {
    return (
      <div className="main-content">
        <Grid fluid>
          <Row>
            <Col md={8} mdOffset={2}>
              <Card
                textCenter
                title="Add A User"
                content={
                  <Form horizontal>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Username: <span className="star">*</span>
                        </ControlLabel>

                        <Col sm={10}>
                          <FormControl
                            type="text"
                            name="username"
                            onChange={this.handleUsernameChange}
                          />
                          {this.state.usernameError}
                        </Col>
                      </FormGroup>
                    </fieldset>

                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Password: <span className="star">*</span>
                        </ControlLabel>

                        <Col sm={10}>
                          <FormControl
                            type="password"
                            name="password"
                            onChange={event => this.handlePasswordChange(event)}
                          />
                          {this.state.passwordError}
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Confirm password: <span className="star">*</span>
                        </ControlLabel>

                        <Col sm={10}>
                          <FormControl
                            type="password"
                            name="cfpassword"
                            onChange={event =>
                              this.handleCfPasswordChange(event)
                            }
                          />
                          {this.state.cfpasswordError}
                        </Col>
                      </FormGroup>
                    </fieldset>

                    <fieldset>
                      <FormGroup>
                        <ControlLabel className="col-sm-2">
                          Role <span className="star">*</span>{" "}
                          {tooltip("The role of this user")}
                        </ControlLabel>
                        <Col sm={10}>
                          <Select
                            placeholder="Role"
                            name="roleOption"
                            options={this.props.roleOptions}
                            value={this.state.selectedRoleOption}
                            id="roleOption"
                            onChange={selectedOption => {
                              this.setState({
                                selectedRoleOption: selectedOption
                              });
                            }}
                          />
                        </Col>
                      </FormGroup>
                    </fieldset>
                    <div className="category">
                      <span className="star">*</span> Required fields
                    </div>
                    <hr />
                    <Button bsStyle="info" fill onClick={this.saveUser}>
                      Submit
                    </Button>
                  </Form>
                }
              />
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}
