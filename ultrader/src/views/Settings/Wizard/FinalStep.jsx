import React, { Component } from "react";
// react component used to create charts
import SweetAlert from "react-bootstrap-sweetalert";

import Button from "components/CustomButton/CustomButton.jsx";

class FinalStep extends Component {
  constructor(props) {
    super(props);
    this.state = {
      alert: null
    };
  }
  isValidated() {
    return true;
  }
  successAlert() {
    this.setState({
      alert: (
        <SweetAlert
          success
          style={{ display: "block", marginTop: "-100px" }}
          title="Good job!"
          onConfirm={() => this.setState({ alert: null })}
          onCancel={() => this.setState({ alert: null })}
          confirmBtnBsStyle="info"
        >
          You clicked the finish button!
        </SweetAlert>
      )
    });
  }
  render() {
    return (
      <div className="wizard-step">

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
        {this.state.alert}
      </div>
    );
  }
}

export default FinalStep;
