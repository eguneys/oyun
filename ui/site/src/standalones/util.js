window.oyunkeyf = window.oyunkeyf || {};

oyunkeyf.raf = window.requestAnimationFrame.bind(window);
oyunkeyf.requestIdleCallback = (window.requestIdleCallback || window.setTimeout).bind(window);
oyunkeyf.dispatchEvent = (el, eventName) => el.dispatchEvent(new Event(eventName));

// Unique id for the current document/navigation. Should be different after
// each page load and for each tab. Should be unpredictable and secret while
// in use.
try {
  const data = window.crypto.getRandomValues(new Uint8Array(9));
  oyunkeyf.sri = btoa(String.fromCharCode(...data)).replace(/[/+]/g, '_');
} catch(_) {
  oyunkeyf.sri = Math.random().toString(36).slice(2, 12);
}


{
  const buildStorage = (storage) => {
    const api = {
      get: k => storage.getItem(k),
      set: (k, v) => storage.setItem(k, v),
      fire: (k, v) => storage.setItem(k, JSON.stringify({
        sri: oyunkeyf.sri,
        nonce: Math.random(), // ensure item changes
        value: v
      })),
      remove: k => storage.removeItem(k),
      make: k => ({
        get: () => api.get(k),
        set: v => api.set(k, v),
        fire: v => api.fire(k, v),
        remove: () => api.remove(k),
        listen: f => window.addEventListener('storage', e => {
          if (e.key !== k || e.storageArea !== storage || e.newValue === null) return;
          let parsed;
          try {
            parsed = JSON.parse(e.newValue);
          } catch(_) {
            return;
          }
          // check sri, because Safari fires events also in the original
          // document when there are multiple tabs
          if (parsed.sri && parsed.sri !== oyunkeyf.sri) f(parsed);
        })
      }),
      makeBoolean: k => ({
        get: () => api.get(k) == 1,
        set: v => api.set(k, v ? 1 : 0),
        toggle: () => api.set(k, api.get(k) == 1 ? 0 : 1)
      })
    };
    return api;
  };


  oyunkeyf.storage = buildStorage(window.localStorage);
  oyunkeyf.tempStorage = buildStorage(window.sessionStorage);
}

oyunkeyf.spinnerHtml = '<div class="spinner"><svg viewBox="0 0 40 40"><circle cx=20 cy=20 r=18 fill="none"></circle></svg></div>';
oyunkeyf.assetUrl = (path, opts) => {
  opts = opts || {};
  const baseUrl = opts.sameDomain ? '' : document.body.getAttribute('data-asset-url'),
    version = document.body.getAttribute('data-asset-version');
  return baseUrl + '/assets' + (opts.noVersion ? '' : '/_' + version) + '/' + path;
};
oyunkeyf.loadedCss = {};
oyunkeyf.loadCss = function(url) {
  if (oyunkeyf.loadedCss[url]) return;
  oyunkeyf.loadedCss[url] = true;
  $('head').append($('<link rel="stylesheet" type="text/css" />').attr('href', oyunkeyf.assetUrl(url)));
};
oyunkeyf.loadCssPath = function(key) {
  oyunkeyf.loadCss('css/' + key + '.' + $('body').data('theme') + '.' + ($('body').data('dev') ? 'dev' : 'min') + '.css');
};
oyunkeyf.compiledScript = function(name) {
  return 'compiled/oyunkeyf.' + name + ($('body').data('dev') ? '' : '.min') + '.js';
};
oyunkeyf.loadScript = function(url, opts) {
  return $.ajax({
    dataType: "script",
    cache: true,
    url: oyunkeyf.assetUrl(url, opts)
  });
};


oyunkeyf.idleTimer = function(delay, onIdle, onWakeUp) {
  var events = ['mousemove', 'touchstart'];
  var listening = false;
  var active = true;
  var lastSeenActive = performance.now();
  var onActivity = function() {
    if (!active) {
      // console.log('Wake up');
      onWakeUp();
    }
    active = true;
    lastSeenActive = performance.now();
    stopListening();
  };
  var startListening = function() {
    if (!listening) {
      events.forEach(function(e) {
        document.addEventListener(e, onActivity);
      });
      listening = true;
    }
  };
  var stopListening = function() {
    if (listening) {
      events.forEach(function(e) {
        document.removeEventListener(e, onActivity);
      });
      listening = false;
    }
  };
  setInterval(function() {
    if (active && performance.now() - lastSeenActive > delay) {
      // console.log('Idle mode');
      onIdle();
      active = false;
    }
    startListening();
  }, 10000);
};
oyunkeyf.pubsub = (function() {
  var subs = [];
  return {
    on(name, cb) {
      subs[name] = subs[name] || [];
      subs[name].push(cb);
    },
    off(name, cb) {
      if (!subs[name]) return;
      for (var i in subs[name]) {
        if (subs[name][i] === cb) {
          subs[name].splice(i);
          break;
        }
      }
    },
    emit(name /*, args... */) {
      if (!subs[name]) return;
      const args = Array.prototype.slice.call(arguments, 1);
      for (let i in subs[name]) subs[name][i].apply(null, args);
    }
  };
})();
oyunkeyf.hasToReload = false;
oyunkeyf.redirectInProgress = false;
oyunkeyf.redirect = function(obj) {
  var url;
  if (typeof obj == "string") url = obj;
  else {
    url = obj.url;
    if (obj.cookie) {
      var domain = document.domain.replace(/^.+(\.[^.]+\.[^.]+)$/, '$1');
      var cookie = [
        encodeURIComponent(obj.cookie.name) + '=' + obj.cookie.value,
        '; max-age=' + obj.cookie.maxAge,
        '; path=/',
        '; domain=' + domain
      ].join('');
      document.cookie = cookie;
    }
  }
  var href = '//' + location.host + '/' + url.replace(/^\//, '');
  oyunkeyf.redirectInProgress = href;
  location.href = href;
};
oyunkeyf.reload = function() {
  if (oyunkeyf.redirectInProgress) return;
  oyunkeyf.hasToReload = true;
  if (location.hash) location.reload();
  else location.href = location.href;
};
oyunkeyf.escapeHtml = function(str) {
  return /[&<>"']/.test(str) ?
    str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/'/g, '&#39;')
    .replace(/"/g, '&quot;') :
    str;
};


oyunkeyf.numberFormat = (function() {
  var formatter = false;
  return function(n) {
    if (formatter === false) formatter = (window.Intl && Intl.NumberFormat) ? new Intl.NumberFormat() : null;
    if (formatter === null) return n;
    return formatter.format(n);
  };
})();
