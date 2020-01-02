import Mrender from 'mrender';

import makeCtrl from './ctrl';
import view from './view/main';
import boot from './boot';

export function start(opts) {

  const mrender = new Mrender(opts.element);
  
  let ctrl = new makeCtrl(opts, redraw);

  const redraw = () => {
    mrender.render(view(ctrl));
  };

  return {
    socketReceive: ctrl.socket.receive,
    redraw: ctrl.redraw
  };

}

window.onload = function() {
  boot(window['oyunkeyf_lobby'], document.querySelector('.lobby__app'));
};
