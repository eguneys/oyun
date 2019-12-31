import h from 'mithril/hyperscript';

export default function(ctrl) {

  return h('div', [
    userLinks(ctrl)
  ]);
  
}

function userLinks(ctrl) {

  const d = ctrl.data, trans = ctrl.trans, noarg = trans.noarg;


  return d.user ? h('div.links', [
    h('a.user-link.online.text.is-green',
      linkCfg(`/@/${d.user.name}`, ''),
      noarg('profile')),
    h('form.logout', { 
      method: 'post', 
      action: '/logout' },
      h('button.text', {
        type: 'submit',
        'data-icon': 'w'
      }, noarg('logOut')))
  ]) : null;
  
  
}

function linkCfg(href, icon, more) {
  const cfg = {
    href,
    'data-icon': icon
  };

  if (more) for (let i in more) cfg[i] = more[i];
  return cfg;
}
