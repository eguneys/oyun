export default function(opts) {

  const oy = window.oyunkeyf;
  const element = document.querySelector('.masa__app');
  const data = opts.data;

  let masa;
  oy.socket = oy.StrongSocket(
    data.url.socket,
    data.player.version, {
      options: { debug: true, name: 'masa' },
      params: {},
      receive(t, d) { masa.socketReceive(t, d); },
      events: {
      }
    }
  );

  opts.element = element;
  opts.socketSend = oy.socket.send;
  masa = (window['OyunkeyfMasa']).app(opts);  
}
