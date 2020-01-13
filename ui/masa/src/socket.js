import throttle from 'common/throttle';

export function make(send, ctrl) {

  function reload(o, isRetry) {
    
  }

  const d = ctrl.data;

  const handlers = {
    buyin: ctrl.buyIn,
    sitoutnext: ctrl.sitoutNext,
    me: ctrl.meJoin
  };

  return {
    send,
    handlers,
    sitoutNextHand: throttle(300, (value) => {
      console.log(value);
      send('sitoutNext', value);
    }),
    sendLoading(typ, data) {
      ctrl.setLoading(true);
      send(typ, data);
    },
    receive(typ, data) {
      if (handlers[typ]) {
        handlers[typ](data);
        return true;
      }
      return false;
    },
    reload
  };

}
