import h from 'mithril/hyperscript';
import Pokerground from 'pokerf';

export function makeConfig(ctrl) {
  const data = ctrl.data, hooks = ctrl.makePgHooks();
  
  console.log(data);

  const { game } = data;

  let gameClock,
      gameFen,
      gameSeatIndexes;

  if (game) {
    gameClock = game.clock;
    gameFen = game.fen;
    gameSeatIndexes = game.seatIndexes.map(_ => parseInt(_));
  }

  return {
    clock: gameClock,
    fen: gameFen,
    seatIndexes: gameSeatIndexes,
    me: data.me,
    seats: data.seats,
    status: data.status,
    events: {
      sitoutNextHand: hooks.onSitoutNextHand,
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
