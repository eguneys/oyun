import h from 'mithril/hyperscript';

export function spinner() {
  return h('div.spinner', [
    h('svg', { viewBox: '0 0 40 40' }, [
      h('circle', { cx: 20, cy: 20, r: 18, fill: 'none' })
    ])
  ]);
};

export function mclasses(mm) {
  return Object.keys(mm)
    .filter(_ => mm[_]).join(' ');
}
