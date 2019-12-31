import stream from 'mithril/stream';

const defaultMode = 'links';

export default function dasher(opts, data, redraw) {

  this.trans = window.oyunkeyf.trans(data.i18n);

  this.mode = stream(defaultMode);

  this.data = data;

  
}
