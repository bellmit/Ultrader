export function parsePercentage(num) {
  return (num * 100).toFixed(1) + "%";
}

export function parseMoney(num) {
  return "$" + num;
}
