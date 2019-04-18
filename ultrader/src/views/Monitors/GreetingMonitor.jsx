import React from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

class GreetingMonitorComp extends React.Component {
  constructor(props, context) {
    super(props, context);

  }


  render() {
    return (
      <div className="main-content">
        <ul>
          {this.props.monitorMessages.map(item => (
            <li key={item}>{item}</li>
          ))}
        </ul>
      </div>
    );
  }
}

export default GreetingMonitorComp;