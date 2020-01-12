export default function throttle(delay, callback, ...args) {

  let timer,
      lastExec = 0;

  return function(...args) {
    const elapsed = performance.now() - lastExec;

    function exec() {
      timer = undefined;
      lastExec = performance.now();
      callback.apply(self, args);
    }
    
    if (timer) {
      clearTimeout(timer);
    }
    
    if (elapsed > delay) exec();
    else timer = setTimeout(exec, delay - elapsed);
  };

}
