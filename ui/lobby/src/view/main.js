import H from 'mithril/hyperscript';

export default function(ctrl) {

  return h('div.lobby__app.lobby__app-' + ctrl.tab, [
    h('divs.tabs-horiz'),
    h('div.lobby__app__content')
  ]);
  
}
