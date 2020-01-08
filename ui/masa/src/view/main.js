import h from 'mithril/hyperscript';

import { render as renderGround } from '../ground';

export default function main(ctrl) {

  return [
    h('div.masa__app__board.main-board', {
    }, [
      renderGround(ctrl)
    ])
  ];
  
}
