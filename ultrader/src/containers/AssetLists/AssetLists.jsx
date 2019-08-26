import React, { Component } from "react";

import { connect } from "react-redux";

import * as ACTION_TYPES from "actions/AssetLists/AssetListsActions";

import AssetListsComp from "views/AssetLists/AssetLists";

class AssetLists extends Component {
  render() {
    return (
      <AssetListsComp
        assetLists={this.props.assetLists}
        assets={this.props.assets}
        assetOptions={this.props.assetOptions}
        onGetAssetListsSuccess={this.props.onGetAssetListsSuccess}
        onDeleteAssetListSuccess={this.props.onDeleteAssetListSuccess}
        onGetAllAssetsSuccess={this.props.onGetAllAssetsSuccess}
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
      onGetAllAssetsSuccess: response =>
        dispatch({
          type: ACTION_TYPES.GET_ALL_ASSETS_SUCCESS,
          response: response
        }),
    onGetAssetListsSuccess: response =>
      dispatch({
        type: ACTION_TYPES.GET_ASSET_LISTS_SUCCESS,
        response: response
      }),

    onDeleteAssetListSuccess: index =>
      dispatch({
        type: ACTION_TYPES.DELETE_ASSET_LIST_SUCCESS,
        index: index
      })
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AssetLists);
