import h from 'mithril/hyperscript';

import * as list from './list';

export default function(ctrl) {

  let body, modeToggle;
  let res = ctrl.data.masas;

  switch (ctrl.mode) {
  default:
    body = list.render(ctrl, res);
    modeToggle = list.toggle(ctrl);
  }
  
  return [
    modeToggle,
    body
  ];
}
