import moment from 'moment'
import React, { Component } from "react";

export function parseDate(date) {
  var parsed = moment(date);
  return parsed.isValid() ? parsed.format('YYYY/MM/DD HH:mm:ss') : date;
}

export function parsePercentage(num) {
  return (num * 100).toFixed(1) + "%";
}

export function parseMoney(num) {
  return "$" + Number(num).toFixed(2);
}

export function parseProfit(value, base) {
console.log(value + " " + base);
 value = Number(value);
 base = Number(base);
 var color = value >= 0 ? "green" : "red";
 var profitPercentText = (value / base * 100).toFixed(2) + "%";
 return (<span style={{ color: color }}>
           {"$" + value.toFixed(2) + " (" + profitPercentText + ")"}
         </span>
        );
}

export function isFloat(val) {
    var floatRegex = /^-?\d+(?:[.,]\d*?)?$/;
    if (!floatRegex.test(val))
        return false;

    val = parseFloat(val);
    if (isNaN(val))
        return false;
    return true;
}

export function isInt(val) {
    var intRegex = /^-?\d+$/;
    if (!intRegex.test(val))
        return false;

    var intVal = parseInt(val, 10);
    return parseFloat(val) == intVal && !isNaN(intVal);
}