(function() {
  
  $(onBoot);

})();

function onBoot() {

  oyunkeyf.requestIdleCallback(() => {

    dasher();

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
        console.log('done');
      });

    });
  }
}
