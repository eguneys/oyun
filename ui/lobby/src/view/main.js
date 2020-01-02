import h from 'mithril/hyperscript';
import renderTabs from './tabs';

export default function(ctrl) {

  return [h('divs.tabs-horiz', renderTabs(ctrl)),
          h('div.lobby__app__content')
         ];
  
}
