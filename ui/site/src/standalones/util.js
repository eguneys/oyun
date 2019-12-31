window.oyunkeyf = window.oyunkeyf || {};

oyunkeyf.raf = window.requestAnimationFrame.bind(window);
oyunkeyf.requestIdleCallback = (window.requestIdleCallback || window.setTimeout).bind(window);
oyunkeyf.dispatchEvent = (el, eventName) => el.dispatchEvent(new Event(eventName));

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
