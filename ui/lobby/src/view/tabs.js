import h from 'mithril/hyperscript';
import { mclasses } from 'mrender/util';

function tab(ctrl, key, active, content) {
  return h('span', {
    class: mclasses({
      active: key === active
    }),
    onclick: () => {
      ctrl.setTab(key);
      ctrl.redraw();
    }
  }, content);
}

export default function(ctrl) {
  const active = ctrl.tab;
  return [
    tab(ctrl, 'pools', active, [ctrl.trans.noarg('quickGame')]),
    tab(ctrl, 'real_time', active, [ctrl.trans.noarg('lobby')])
  ];
}
