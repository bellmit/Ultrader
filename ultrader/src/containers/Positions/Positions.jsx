import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/Positions/PositionsActions";

import PositionsComp from "views/Positions/Positions";

class Positions extends Component {
  render() {
    return (
      <PositionsComp
        positions={this.props.positions}

        onGetPositionsSuccess={this.props.onGetPositionsSuccess}

        {...this.props}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    positions: state.positions.positions
  };
};

const mapDispatchToProps = dispatch => {
  return {
      onGetPositionsSuccess: (response) =>
        dispatch({
          type: ACTION_TYPES.GET_POSITIONS_SUCCESS,
          response: response
        })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Positions);
