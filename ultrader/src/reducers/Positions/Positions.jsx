
import * as ACTION_TYPES from 'actions/Positions/PositionsActions';

const initialState = {
    positions:[]
};

const positions = (state = initialState, action) => {
  switch (action.type) {
    case ACTION_TYPES.GET_POSITIONS_SUCCESS:
        let positions = action.response.data;
        return {
          ...state,
          positions: positions
        };
    default:
      return state
  }
}

export default positions;