import React, { Component } from "react";
import {
  Grid,
  Row,
  Col,
  FormGroup,
  FormControl,
  ControlLabel
} from "react-bootstrap";
// react component used to create charts
import Select from "react-select";

import Card from "components/Card/Card.jsx";
import { tooltip } from "helpers/TooltipHelper";
import Button from "components/CustomButton/CustomButton.jsx";

class FinalStep extends Component {
  constructor(props) {
    super(props);
  }

  isValidated() {
    return true;
  }

  render() {
    return (
      <div className="wizard-step">
        <Grid fluid>
          <Row>
            <Col md={12}>
              <Card
                content={
                  <form>
                    <FormGroup>
                      <ControlLabel>Strategy Template {tooltip("StrategyTemplate")}</ControlLabel>
                      <Select
                        placeholder="Strategy Templates"
                        name="strategyTemplate"
                        options={this.props.strategyTemplateOptions}
                        value={this.props.selectedStrategyTemplateOption}
                        id="STRATEGY_TEMPLATE"
                        onChange={option =>
                          this.props.selectStrategyTemplateOption(option)
                        }
                      />
                    </FormGroup>
                    {this.props.selectedStrategyTemplateOption &&
                    this.props.selectedStrategyTemplateOption.description ? (
                      <FormGroup>
                        <ControlLabel>Strategy Description</ControlLabel>
                        <Card
                          content={
                            <div
                              dangerouslySetInnerHTML={{
                                __html: this.props
                                  .selectedStrategyTemplateOption.description
                              }}
                            />
                          }
                        ></Card>
                      </FormGroup>
                    ) : (
                      ""
                    )}
                  </form>
                }
              />
            </Col>
          </Row>
        </Grid>
        <div className="wizard-finish-button">
          <Button
            bsStyle="info"
            fill
            wd
            onClick={this.props.saveSettings}
            pullRight
          >
            Finish
          </Button>
        </div>
      </div>
    );
  }
}

export default FinalStep;
