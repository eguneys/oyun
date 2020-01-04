import * as masaRepo from './masaRepo';
import { make as makeStores } from './store';
import LobbySocket from './socket';

export default function LobbyController(opts, redraw) {
  
  this.opts = opts;
  this.data = opts.data;
  this.trans = opts.trans;
  this.data.hooks = [];
  this.redraw = redraw;

  masaRepo.initAll(this);

  this.socket = new LobbySocket(opts.socketSend, this);
  
  this.stores = makeStores(this.data.me ? this.data.me.username.toLowerCase() : null);
  this.tab = this.stores.tab.get();
  this.mode = this.stores.mode.get();
  this.sort = this.stores.sort.get();

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

  this.setSort = (sort) => {
    this.sort = this.stores.sort.set(sort);
  };

  this.setMode = (mode) => {
    this.mode = this.stores.sort.set(mode);
  };


  this.clickMasa = (id) => {
    const masa = masaRepo.find(this, id);
    this.socket.send('join', masa.id);
  };

}
