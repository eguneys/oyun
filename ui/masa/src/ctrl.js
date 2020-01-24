import { make as makeSocket } from './socket';

export default function MasaController(opts, redraw) {
  
  const d = this.data = opts.data;

  this.redraw = redraw;
  
  this.pokerground = null;

  this.setPokerground = (pg) => {
    this.pokerground = pg;
  };

  const actualSendMove = (tpe, data) => {
    const socketOpts = {
      ackable: true
    };

    this.socket.send(tpe, data, socketOpts);

    this.redraw();
  };

  const sendMove = (uci) => {
    const move = {
      u: uci
    };
    actualSendMove('move', move);
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
    sendMove(move);
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
    return this.pokerground.join({
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
      return this.pokerground.leave({ seatIndex: o.side });
    }
    return Promise.resolve();
  };

  this.meSet = (o) => {
    this.pokerground.meSet(o);
  };

  this.deal = (o) => {
    o.seatIndexes = o.seatIndexes.map(_ => parseInt(_));
    return this.pokerground.deal(o);
  };

  this.apiMove = (o) => {
    let lp = this.pokerground.move(o);
    console.log(o);
    if (o.nextTurn) {
      lp = lp.then(() => this.pokerground.nextTurn(o.nextTurn));
    } else if (o.nextRound) {
      lp = lp.then(() => this.pokerground.nextRound(o.nextRound));
    } else if (o.oneWin) {
      lp = lp.then(() => this.pokerground.oneWin(o.oneWin));
    } else if (o.showdown) {
      lp = lp.then(() => this.pokerground.showdown(o.showdown));
    }
    return lp;
  };

  this.socket = makeSocket(opts.socketSend, this);
}
