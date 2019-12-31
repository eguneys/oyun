"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.get = get;
var headers = {
  'Accept': 'application/vnd.oyunkeyf.v2+json'
};

function get(url, cache) {
  return $.ajax({
    url: url,
    headers: headers,
    cache: cache
  });
}