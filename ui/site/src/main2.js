(function() {
  
  $(onBoot);

})();

function onBoot() {

  oyunkeyf.requestIdleCallback(() => {

    dasher();
    topToggleClickHandler();

  });

  const initiatingHtml = '<div class="initiating">' + oyunkeyf.spinnerHtml + '</div>';

  function dasher() {
    let booted;
    $('#top .dasher .toggle').one('mouseover click', function() {
      if (booted) return;
      booted = true;

      const $el = $('#dasher_app').html(initiatingHtml);
      oyunkeyf.loadCssPath('dasher');
      oyunkeyf.loadScript(oyunkeyf.compiledScript('dasher')).done(() => {
        OyunkeyfDasher.default($el.empty()[0], {});
      });
    });
  }

  function topToggleClickHandler() {
    $('#top').on('click', 'a.toggle', function() {
      let $p = $(this).parent();
      $p.toggleClass('shown');
      $p.siblings('.shown').removeClass('shown');
      setTimeout(function() {
        let handler = function(e) {
          if ($.contains($p[0], e.target)) return;
          $p.removeClass('shown');
          $('html').off('click', handler);
        };
        $('html').on('click', handler);
      }, 10);
      return false;
    });
  }
}
