import React from 'react';
import ReactDOM from 'react-dom';
import {Form} from 'react-bootstrap';
class License extends React.Component {
	constructor(props) {
      super(props);
      this.textOnChange = this.textOnChange.bind(this);
    }

    textOnChange(e) {

    	this.props.onChange(e.target.id, e.target.value)
    }
	render() {
		return (
			<Form>
			<Form.Control id="KEY_ULTRADER_KEY" onChange={this.textOnChange} type="text" placeholder="Ultrader Bot Key" />
			<Form.Control id="KEY_ULTRADER_SECRET" onChange={this.textOnChange} type="text" placeholder="Ultrader Bot Secret" />
			<Form.Control id="KEY_ALPACA_KEY" onChange={this.textOnChange} type="text" placeholder="Alpaca Key" />
			<Form.Control id="KEY_ALPACA_SECRET" onChange={this.textOnChange} type="text" placeholder="Alpaca Secret" />
			</Form>

			);
	}
}

export default License;