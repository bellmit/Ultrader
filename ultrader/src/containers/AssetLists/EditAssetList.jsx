import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/AssetLists/AssetListsActions";

import EditAssetListComp from "views/AssetLists/EditAssetList";

class EditAssetList extends Component {
  render() {
    return (
      <EditAssetListComp
        assetList={this.props.assetList}

        assetLists={this.props.assetLists}
        assets={this.props.assets}
        assetOptions={this.props.assetOptions}
        onEditAssetListSuccess={this.props.onEditAssetListSuccess}

        {...this.props}
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
    onEditAssetListSuccess: (response,index) =>
      dispatch({
        type: ACTION_TYPES.EDIT_ASSET_LIST_SUCCESS,
        response: response,
        index: index
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EditAssetList);
