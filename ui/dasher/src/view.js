import h from 'mithril/hyperscript';
import { spinner } from 'mrender';
import links from './links';

function loading() {
  return h('div.initiating', spinner());
}

function loaded(ctrl) {
  let content;

  switch(ctrl.mode()) {
  default:
    content = links(ctrl);
  }

  return content;
}

export default function view(ctrl) {
  return ctrl ? loaded(ctrl) : loading();
}
