"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = MRender;

var _render = _interopRequireDefault(require("mithril/render"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

function MRender(mountPoint) {
  this.render = function (vnode) {
    (0, _render["default"])(mountPoint, vnode);
  };
}