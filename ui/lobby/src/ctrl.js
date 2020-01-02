import LobbySocket from './socket';

export default function LobbyController(opts, redraw) {
  
  this.opts = opts;
  this.data = opts.data;
  this.data.hooks = [];
  this.redraw = redraw;

  this.socket = new LobbySocket(opts.socketSend, this);

}
