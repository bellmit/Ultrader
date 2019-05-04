import React, { Component } from "react";
// react component that creates a form divided into multiple steps
import StepZilla from "react-stepzilla";
import { Grid, Row, Col } from "react-bootstrap";

import "./tabwidth.css";

import axios from "axios";

import Card from "components/Card/Card.jsx";

import Step1 from "./Step1.jsx";
import FinalStep from "./FinalStep.jsx";

import {
  axiosGetWithAuth,
  axiosPostWithAuth,
  handleResponse
} from "helpers/UrlHelper";

class SettingsWizardComp extends Component {
  constructor(props) {
    super(props);
    this.saveSettings = this.saveSettings.bind(this);
    this.steps = [
      {
        name: "Keys",
        component: (
          <Step1
            settings={this.props.settings}
            onAddSetting={this.props.onAddSetting}
          />
        )
      },
      {
        name: "Finish",
        component: (
          <FinalStep
            settings={this.props.settings}
            saveSettings={this.saveSettings}
          />
        )
      }
    ];
  }

  saveSettings() {
    var settings = [];
    for (var key in this.props.settings) {
      if (this.props.settings.hasOwnProperty(key)) {
        settings.push({ name: key, value: this.props.settings[key] });
      }
    }
    console.log(settings);
    axiosPostWithAuth("/setting/addSettings", settings)
      .then(handleResponse)
      .then(res => {
        alert("Saved " + res.data.length + " settings");
      })
      .catch(error => {
        alert(error);
      });
  }

  render() {
    return (
      <div className="main-content">
        <Grid fluid>
          <Row>
            <Col md={8} mdOffset={2}>
              <Card
                wizard
                id="wizardCard"
                textCenter
                title="Awesome Wizard"
                category="Split a complicated flow in multiple steps"
                content={
                  <StepZilla
                    steps={this.steps}
                    stepsNavigation={false}
                    nextButtonCls="btn btn-prev btn-info btn-fill pull-right btn-wd"
                    backButtonCls="btn btn-next btn-default btn-fill pull-left btn-wd"
                  />
                }
              />
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}

export default SettingsWizardComp;
