import makeCtrl from './dasher';
import view from './view';
import Mrender from 'mrender';
import { get } from 'mrender/xhr';

export default function OyunkeyfDasher(element, opts) {

  const mrender = new Mrender(element);

  let ctrl;

  const redraw = () => {
    mrender.render(view(ctrl));
  };

  redraw();

  return get('/dasher').then(data => {
    ctrl = new makeCtrl(opts, data, redraw);
    redraw();
    return ctrl;
  }).catch(e => {
    console.error(e);
  });

}
