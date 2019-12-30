import moment from "moment";
import React, { Component } from "react";
import ModalLink from "components/CustomButton/CustomModalLink.jsx";
import TradingViewWidget from "react-tradingview-widget";

export function parseDate(date) {
  var parsed = moment(date);
  return parsed.isValid() ? parsed.format("YYYY/MM/DD HH:mm:ss") : date;
}

export function parseDatePickerDate(date) {
  var parsed = moment(date);
  return parsed.isValid() ? parsed.format("MM/DD/YYYY HH:mm A") : date;
}

export function parseJavaLocalDatetime(date) {
  var parsed = moment(date);
  return parsed.isValid() ? parsed.format("YYYY-MM-DDTHH:mm:ss") : date;
}

export function parseSeconds(seconds) {
  if (seconds < 60) {
    return seconds + " Seconds";
  } else if (seconds == 60) {
    return seconds / 60 + " Minute";
  } else if (seconds < 3600) {
    return seconds / 60 + " Minutes";
  } else if (seconds == 3600) {
    return seconds / 60 + " Hour";
  } else if (seconds < 86400) {
    return seconds / 3600 + " Hours";
  } else if (seconds == 86400) {
    return seconds / 3600 + " Day";
  } else {
    return seconds / 86400 + " Days";
  }
}

export function parsePercentage(num) {
  return (num * 100).toFixed(3) + "%";
}

export function parseMoney(num) {
  return "$" + Number(num).toFixed(2);
}

export function parseProfit(value, base) {
  value = Number(value);
  base = Number(base);
  var color = value >= 0 ? "green" : "red";
  var profitPercentText = ((value / (base - value)) * 100).toFixed(2) + "%";
  return (
    <span style={{ color: color }}>
      {"$" + value.toFixed(2) + " (" + profitPercentText + ")"}
    </span>
  );
}

export function parseSymbolGraphModal(value) {
  var symbol = value ? value : "NASDAQ:AAPL";
  return (
    <ModalLink
      linkText={value}
      modalTitle={"Chart for " + value}
      modalBody={<TradingViewWidget symbol={symbol} autosize />}
    />
  );
}

export function isFloat(val) {
  var floatRegex = /^-?\d+(?:[.,]\d*?)?$/;
  if (!floatRegex.test(val)) return false;

  val = parseFloat(val);
  if (isNaN(val)) return false;
  return true;
}

export function isInt(val) {
  var intRegex = /^-?\d+$/;
  if (!intRegex.test(val)) return false;

  var intVal = parseInt(val, 10);
  return parseFloat(val) == intVal && !isNaN(intVal);
}

export function parseReadableFileSizeString(fileSizeInBytes) {
    var i = -1;
    var byteUnits = [' kB', ' MB', ' GB', ' TB', 'PB', 'EB', 'ZB', 'YB'];
    do {
        fileSizeInBytes = fileSizeInBytes / 1024;
        i++;
    } while (fileSizeInBytes > 1024);

    return Math.max(fileSizeInBytes, 0.1).toFixed(1) + byteUnits[i];
};