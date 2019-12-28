import React, { Component } from "react";
import { Modal } from "react-bootstrap";
import Button from "components/CustomButton/CustomButton.jsx";
import "components/CustomButton/full-screen-modal.css";

class CustomModalLink extends Component {
  constructor(props) {
    super(props);
    this.state = {
      show: props.show ? true : false
    };
    this.setShow = this.setShow.bind(this);
  }
  setShow() {
    this.setState({ show: !this.state.show });
  }
  render() {
    const handleClose = () => this.setShow(false);
    const handleShow = () => this.setShow(true);

    return (
      <div>
        <a color="primary" onClick={handleShow} style={{cursor:'pointer'}}>
          {this.props.linkText}
        </a>

        <Modal
          show={this.state.show}
          onHide={handleClose}
          className="full-screen-modal"
        >
          <Modal.Header closeButton>
            <Modal.Title>{this.props.modalTitle}</Modal.Title>
          </Modal.Header>
          <Modal.Body>{this.props.modalBody}</Modal.Body>
        </Modal>
      </div>
    );
  }
}

export default CustomModalLink;
