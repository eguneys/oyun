import h from 'mithril/hyperscript';
import { spinner } from 'mrender';

import renderTabs from './tabs';
import renderRealTime from './realtime';
import renderPools from './pools';

export default function(ctrl) {

  let body;

  if (ctrl.redirecting) body = spinner();
  else switch(ctrl.tab) {
    case 'pools':
    body = renderPools(ctrl);
    case 'real_time':
    body = renderRealTime(ctrl);
  }


  return [h('divs.tabs-horiz', renderTabs(ctrl)),
          h('div.lobby__app__content', body)
         ];
  
}
