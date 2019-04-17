import React from "react";
import {Grid,
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

    textOnChange(e) {
    	this.props.onAddSetting(e.target.id, e.target.value)
    }
  render() {
    return (
      <div className="wizard-step">
        <h5 className="text-center">Keys</h5>
                <Grid fluid>
                  <Row>
                    <Col md={12}>
                      <Card
                        content={
                          <form>
                            <FormGroup>
                              <ControlLabel>Ultrader Bot Key</ControlLabel>
                              <FormControl id="KEY_ULTRADER_KEY"
                              defaultValue={this.props.settings["KEY_ULTRADER_KEY"]} onChange={this.textOnChange} type="text" placeholder="Ultrader Bot Key" />
                            </FormGroup>
                            <FormGroup>
                              <ControlLabel>Ultrader Bot Secret</ControlLabel>
                              <FormControl id="KEY_ULTRADER_SECRET" onChange={this.textOnChange} type="text" placeholder="Ultrader Bot Secret"/>
                            </FormGroup>
                            <FormGroup>
                              <ControlLabel>Alpaca Key</ControlLabel>
                              <FormControl id="KEY_ALPACA_KEY" onChange={this.textOnChange} type="text" placeholder="Alpaca Key" />
                            </FormGroup>
                            <FormGroup>
                              <ControlLabel>Alpaca Secret</ControlLabel>
                              <FormControl id="KEY_ALPACA_SECRET" onChange={this.textOnChange} type="text" placeholder="Alpaca Secret" />
                            </FormGroup>
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
