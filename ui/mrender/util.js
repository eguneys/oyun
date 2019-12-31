"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.spinner = spinner;

var _hyperscript = _interopRequireDefault(require("mithril/hyperscript"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

function spinner() {
  return (0, _hyperscript["default"])('div.spinner', [(0, _hyperscript["default"])('svg', {
    viewBox: '0 0 40 40'
  }, [(0, _hyperscript["default"])('circle', {
    cx: 20,
    cy: 20,
    r: 18,
    fill: 'none'
  })])]);
}

;