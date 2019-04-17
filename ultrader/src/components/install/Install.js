import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Container, Row, Col} from 'react-bootstrap';
import axios from 'axios';
import License from './settings/License'

class Install extends React.Component {
constructor(props) {
      super(props);
      this.previousBtnClick = this.previousBtnClick.bind(this);
      this.nextBtnClick = this.nextBtnClick.bind(this);
      this.addSetting = this.addSetting.bind(this);
      this.saveSettings = this.saveSettings.bind(this);
      this.state = {
          settings: {},
          currentPage: 1,
          totalPage: 1
      }
  }
  addSetting(settingName, settingValue) {
    this.state.settings[settingName] = settingValue;
  }
  previousBtnClick() {

  }
  nextBtnClick() {
    if(this.state.currentPage == this.state.totalPage) {
      //Save settings
      this.saveSettings();
    } else {
      //Go to next page
      this.state.currentPage += 1;
    }
  }

  saveSettings() {
    var settings = [];
    for (var key in this.state.settings) {
      if(this.state.settings.hasOwnProperty(key)) {
        settings.push({name:key, value:this.state.settings[key]});
      }
    }
    console.log(settings);
    axios.post('http://localhost:9191/settings', settings).then(res => {
      alert("Saved " + res.data.length + " settings");
    }).catch(error => {
      alert(error);
    });
  }

  render() {
    let page, previousBtn, nextBtn;
    switch(this.state.currentPage) {
        case 1:
        page = <License onChange={this.addSetting}/>
        break;
    }
    if(this.state.currentPage == 1) {
        previousBtn = "";
    } else {
        previousBtn = <Button onClick={this.previousBtnClick}>Previous</Button>
    }

    if(this.state.totalPage == this.state.currentPage) {
      nextBtn = <Button onClick={this.nextBtnClick}>Save</Button>
    } else {
      nextBtn = <Button onClick={this.nextBtnClick}>Next</Button>
    }

    

    return (
      <Container>
      <Row>
        <Col xs={{span:10 , offset:2}} xl={{span:10 , offset:2}}>{page}</Col>
      </Row>
      <Row>
        <Col xs={{span:4}} xl={{span:4}}>{previousBtn}</Col>
        <Col xs={{span:4, offset:8}} xl={{span:4, offset:8}}>{nextBtn}</Col>
      </Row>
      </Container>);
  }
}

export default Install;
