import Mrender from 'mrender';

import MasaController from './ctrl';
import view from './view/main';
import boot from './boot';

export function app(opts) {

  const mrender = new Mrender(opts.element);
  
  let ctrl;

  const redraw = () => {
    mrender.render(view(ctrl));
  };

  ctrl = new MasaController(opts, redraw);
  redraw();

  return {
    socketReceive: ctrl.socket.receive
  };

}


export { boot };
