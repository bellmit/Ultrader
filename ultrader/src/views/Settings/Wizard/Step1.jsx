import React from "react";
import {
  Grid,
  Button,
  Row,
  Col,
  FormGroup,
  FormControl,
  ControlLabel
} from "react-bootstrap";

import Card from "components/Card/Card.jsx";

class Step1 extends React.Component {
  constructor(props) {
    super(props);
    this.textOnChange = this.textOnChange.bind(this);
  }

  isValidated() {
    var valid =
      this.props.settings["KEY_ULTRADER_KEY"] &&
      this.props.settings["KEY_ULTRADER_SECRET"] &&
      this.props.settings["KEY_ALPACA_PAPER_KEY"] &&
      this.props.settings["KEY_ALPACA_PAPER_SECRET"];
    if (!valid) {
      alert("Please fill in all required fields.");
    }
    return valid;
  }

  textOnChange(e) {
    this.props.onAddSetting(e.target.id, e.target.value);
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
                      <ControlLabel>
                        Ultrader Bot Key <span className="star">*</span>
                      </ControlLabel>
                      <FormControl
                        id="KEY_ULTRADER_KEY"
                        value={this.props.settings["KEY_ULTRADER_KEY"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Ultrader Bot Key"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>
                        Ultrader Bot Secret <span className="star">*</span>
                      </ControlLabel>
                      <FormControl
                        id="KEY_ULTRADER_SECRET"
                        value={this.props.settings["KEY_ULTRADER_SECRET"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Ultrader Bot Secret"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>
                        Alpaca Paper Trading Key <span className="star">*</span>
                      </ControlLabel>
                      <FormControl
                        id="KEY_ALPACA_PAPER_KEY"
                        value={this.props.settings["KEY_ALPACA_PAPER_KEY"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Alpaca Paper Trading Key"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>
                        Alpaca Paper Trading Secret{" "}
                        <span className="star">*</span>
                      </ControlLabel>
                      <FormControl
                        id="KEY_ALPACA_PAPER_SECRET"
                        value={this.props.settings["KEY_ALPACA_PAPER_SECRET"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Alpaca Paper Trading Secret"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Alpaca Live Trading Key</ControlLabel>
                      <FormControl
                        id="KEY_ALPACA_KEY"
                        value={this.props.settings["KEY_ALPACA_KEY"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Alpaca Live Trading Key"
                      />
                    </FormGroup>
                    <FormGroup>
                      <ControlLabel>Alpaca Live Trading Secret</ControlLabel>
                      <FormControl
                        id="KEY_ALPACA_SECRET"
                        value={this.props.settings["KEY_ALPACA_SECRET"]}
                        onChange={this.textOnChange}
                        type="text"
                        placeholder="Alpaca Live Trading Secret"
                      />
                    </FormGroup>
                    <div className="category" style={{ color: "red" }}>
                      <span className="star">*</span> Required fields
                    </div>
                  </form>
                }
              />
            </Col>
          </Row>
        </Grid>
      </div>
    );
  }
}

export default Step1;
