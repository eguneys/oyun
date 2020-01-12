"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = throttle;

function throttle(delay, callback) {
  var timer,
      lastExec = 0;
  return function () {
    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    var elapsed = performance.now() - lastExec;

    function exec() {
      timer = undefined;
      lastExec = performance.now();
      callback.apply(self, args);
    }

    if (timer) {
      clearTimeout(timer);
    }

    if (elapsed > delay) exec();else timer = setTimeout(exec, delay - elapsed);
  };
}