import h from 'mithril/hyperscript';
import Pokerground from 'pokerf';

export function makeConfig(ctrl) {
  const data = ctrl.data, hooks = ctrl.makePgHooks();
  
  console.log(data);


  const masa = data.masa;

  return {
    fen: masa.fen,
    clock: masa.clock,
    seatIndexes: masa.seatIndexes,
    me: masa.me,
    seats: masa.seats,
    status: masa.status,
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
