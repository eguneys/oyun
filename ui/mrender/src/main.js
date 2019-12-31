import mrender from 'mithril/render';

export default function MRender(mountPoint) {

  this.render = (vnode) => {
    mrender(mountPoint, vnode);
  };

}
