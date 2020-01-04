import h from 'mithril/hyperscript';
import { mclasses } from 'mrender/util';

import * as masaRepo from '../../masaRepo';

export function tds(bits) {
  return bits.map(function(bit) {
    return h('td', [bit]);
  });
}

function currency(amount) {
  return '$' + amount;
}

function stakes(stakes) {
  const bb = parseFloat(stakes),
        sb = bb / 2;
  return `${currency(sb)} / ${currency(bb)} `;
}

function nbSeats(masa) {
  let nbP = masa.players.length,
      nbS = masa.nbSeats;

  return `${nbP}/${nbS}`;

}

function renderMasa(ctrl, masa) {
  const noarg = ctrl.trans.noarg;

  return h('tr.masa.join', {
    title: noarg('joinMasa'),
    'data-id': masa.id
  }, tds([
    h('span', ' '),
    h('span', stakes(masa.stakes.stakes)),
    currency(masa.stakes.buyIn),
    nbSeats(masa)
  ]));
}


export function render(ctrl, allMasas) {

  let render = (masa) => renderMasa(ctrl, masa);

  const standards = allMasas.slice(0);

  masaRepo.sort(ctrl, standards);

  const renderedMasas = [
    ...standards.map(render)
  ];

  return h('table.hooks__list', [
    h('thead',
      h('tr', [
        h('th'),
        h('th', {
          class: mclasses({
            sortable: true,
            sort: ctrl.sort === 'stakes'
          }),
          onclick: _ => {
            ctrl.setSort('stakes');
            ctrl.redraw();
          }
        }, [h('i.is'), ctrl.trans('blinds')]),
        h('th', ctrl.trans('buyIn')),
        h('th', {
          class: mclasses({
            sortable: true,
            sort: ctrl.sort === 'players'
          }),
          onclick: _ => {
            ctrl.setSort('players');
            ctrl.redraw();
          }
        }, [h('i.is'), ctrl.trans('players')])
      ])
     ),
    h('tbody', {
      onclick: e => {
        let el = e.target;
        do {
          el = el.parentNode;
          if (el.nodeName === 'TR') {
            ctrl.clickMasa(el.getAttribute('data-id'));
            return;
          }
        } while (el.nodeName !== 'TABLE')
      }
    }, renderedMasas)
  ]);
  
}


export function toggle(ctrl) {
  return h('i.toggle', {
    title: ctrl.trans.noarg('graph'), 'data-icon': '9'
  });
}
