import h from 'mithril/hyperscript';
import Pokerground from 'pokerf';

export function makeConfig(ctrl) {
  const data = ctrl.data, hooks = ctrl.makePgHooks();
  
  console.log(data);

  return {
    fen: data.fen,
    clock: data.clock,
    seatIndexes: data.seatIndexes,
    me: data.me,
    seats: data.seats,
    status: data.status,
    events: {
      sit: hooks.onSit,
      move: hooks.onMove,
      flag: hooks.onFlag
    }
  };
}

export function render(ctrl) {
  return h('div.pg-wrap', {
    oncreate: vnode => 
    ctrl.setPokerground(Pokerground(vnode.dom, makeConfig(ctrl)))
  });
}
