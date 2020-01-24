import throttle from 'common/throttle';

function makeSerial() {
  let lp = Promise.resolve();
  return f => (...args) => {
    lp = lp.then(() => f(...args));
  };
}

export function make(send, ctrl) {

  function reload(o, isRetry) {
    
  }

  const handleS = makeSerial();

  const d = ctrl.data;

  const handlers = {
    buyin: handleS(ctrl.buyIn),
    sitoutnext: handleS(ctrl.sitoutNext),
    deal: handleS(ctrl.deal),
    me: handleS(ctrl.meSet),
    move: handleS(ctrl.apiMove)
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
