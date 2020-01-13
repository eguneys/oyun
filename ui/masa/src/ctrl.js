import { make as makeSocket } from './socket';

export default function MasaController(opts, redraw) {
  
  const d = this.data = opts.data;

  this.redraw = redraw;
  
  this.pokerground = null;

  this.setPokerground = (pg) => {
    this.pokerground = pg;
  };

  const onSitoutNextHand = (value) => {
    this.socket.sitoutNextHand(value);
  };


  const onSit = (seatIndex) => {
    const socketOpts = {
      ackable: true
    };

    this.socket.sendLoading('sit', seatIndex + "");
    this.redraw();
  };

  const onMove = (move) => {
    console.log(move);
  };

  const onFlag = () => {
    console.log('flag');
  };

  this.makePgHooks = () => ({
    onSitoutNextHand: onSitoutNextHand,
    onSit: onSit,
    onMove: onMove,
    onFlag: onFlag
  });

  this.setLoading = (v, duration = 1500) => {
    clearTimeout(this.loadingTimeout);
    if (v) {
      this.loading = true;
      this.loadingTimeout = setTimeout(() => {
        this.loading = false;
        this.redraw();
      }, duration);
    } else if (this.loading) {
      this.loading = false;
      this.redraw();
    }
  };

  this.buyIn = (o) => {
    this.pokerground.join({
      seatIndex: o.side,
      seat: {
        name: o.player.user,
        img: o.player.avatar,
        status: o.player.status
      }
    });
  };

  this.sitoutNext = (o) => {
    if (!o.player) {
      this.pokerground.leave({ seatIndex: o.side });
    }
  };

  this.meJoin = (o) => {
    this.pokerground.mejoin(o);
  };

  this.socket = makeSocket(opts.socketSend, this);
}
