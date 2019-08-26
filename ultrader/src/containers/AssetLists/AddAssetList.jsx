import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/AssetLists/AssetListsActions";

import AddAssetListComp from "views/AssetLists/AddAssetList";

class AddAssetList extends Component {
  render() {
    return (
      <AddAssetListComp
        assetLists={this.props.assetLists}
        assets={this.props.assets}
        assetOptions={this.props.assetOptions}
        onAddAssetListSuccess={this.props.onAddAssetListSuccess}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    assetLists: state.assetLists.assetLists,
    assets: state.assetLists.assets,
    assetOptions: state.assetLists.assetOptions
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onAddAssetListSuccess: response =>
      dispatch({
        type: ACTION_TYPES.ADD_ASSET_LIST_SUCCESS,
        response: response
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AddAssetList);
