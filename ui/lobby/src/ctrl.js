import { make as makeStores } from './store';
import LobbySocket from './socket';

export default function LobbyController(opts, redraw) {
  
  this.opts = opts;
  this.data = opts.data;
  this.trans = opts.trans;
  this.data.hooks = [];
  this.redraw = redraw;

  this.socket = new LobbySocket(opts.socketSend, this);
  
  this.stores = makeStores(this.data.me ? this.data.me.username.toLowerCase() : null);
  this.tab = this.stores.tab.get();

  this.setTab = (tab) => {
    if (tab !== this.tab) {
      if (tab === 'real_time') this.socket.realTimeIn();
      else if (this.tab === 'real_time') {
        this.socket.realTimeOut();
        this.data.hooks = [];
      }
      this.tab = this.stores.tab.set(tab);
    }
  };

}
