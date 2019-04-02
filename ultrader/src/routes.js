import React from 'react';
import { Route, Switch } from 'react-router-dom';
import App from './components/App';
import Install from './components/install/Install'
export const Routes = () => (
    <Switch>
      <Route exact path='/' component={App} />
      <Route exact path='/install' component={Install} />
    </Switch>
);
export default Routes;