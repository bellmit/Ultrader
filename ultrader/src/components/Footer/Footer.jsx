import React, { Component } from "react";

class Footer extends Component {
  render() {
    return (
      <footer
        className={
          "footer" +
          (this.props.transparent !== undefined ? " footer-transparent" : "")
        }
      >
        <div
          className={
            "container" + (this.props.fluid !== undefined ? "-fluid" : "")
          }
        >
          <p className="copyright pull-left">
              Version 1.0.0
          </p>
          <p className="copyright pull-right">
            Copyright &copy; 2020{". "}
            Ultrader All rights reserved.
          </p>
        </div>
      </footer>
    );
  }
}
export default Footer;
