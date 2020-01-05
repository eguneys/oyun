import Mrender from 'mrender';

import makeCtrl from './ctrl';
import view from './view/main';
import boot from './boot';

export function start(opts) {

  const mrender = new Mrender(opts.element);

  let ctrl;
  
  const redraw = () => {
    mrender.render(view(ctrl));
  };

  ctrl = new makeCtrl(opts, redraw);
  redraw();

  return {
    socketReceive: ctrl.socket.receive,
    setRedirecting: ctrl.setRedirecting,
    redraw: ctrl.redraw
  };

}

window.onload = function() {
  boot(window['oyunkeyf_lobby'], document.querySelector('.lobby__app'));
};
