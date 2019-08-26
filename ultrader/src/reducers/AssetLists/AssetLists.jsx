import * as ACTION_TYPES from "actions/AssetLists/AssetListsActions";

const initialState = {
  assetLists: [],
  assets: [],
  assetOptions: []
};

const assetLists = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.ADD_ASSET_LIST_SUCCESS:
      var assetList = action.response.data;
      return {
        ...state,
        assetLists: [...state.assetLists, assetList]
      };
    case ACTION_TYPES.EDIT_ASSET_LIST_SUCCESS:
      var assetList = action.response.data;
      var assetLists = [...state.assetLists];
      var index = assetLists.map(function(e) { return e.name; }).indexOf(assetList.name);
      assetLists[index] = assetList;
      return {
        ...state,
        assetLists: assetLists
      };
    case ACTION_TYPES.DELETE_ASSET_LIST_SUCCESS:
      var index = action.index;
      var assetLists = [...state.assetLists];
      assetLists.splice(index, 1);
      return {
        ...state,
        assetLists: assetLists
      };
    case ACTION_TYPES.GET_ASSET_LISTS_SUCCESS:
      var assetLists = action.response.data;
      return {
        ...state,
        assetLists: assetLists
      };
    case ACTION_TYPES.GET_ALL_ASSETS_SUCCESS:
      let assets = action.response.data;
      return {
        ...state,
        assets: assets,
        assetOptions: assets.map(asset => {
            return {
                label: asset.symbol,
                value: asset.symbol
            };
        })
      };
    default:
      return state;
  }
};

export default assetLists;
