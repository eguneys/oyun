function stakesOrder(a, b) {
  return (a.blinds || 0) > (b.blinds || 0) ? -1 : 1;
}

function playersOrder(a, b) {
  return a.players.length < b.players.length ? -1 : 1;
}

export function sort(ctrl, masas) {
  masas.sort(ctrl.sort === 'stakes' ? stakesOrder : playersOrder);
}

export function init(masa) {
  masa.blinds = parseFloat(masa.stakes.stakes);
}

export function initAll(ctrl) {
  ctrl.data.masas.forEach(init);
}

export function find(ctrl, id) {
  return ctrl.data.masas.find(function(m) {
    return m.id === id;
  });
}
