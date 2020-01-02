export default function LobbySocket(send, ctrl) {
  
  this.send = send;

  this.handlers = {
  };

  this.realTimeIn = () => {
    this.send('hookIn');
  };

  this.realTimeOut = () => {
    this.send('hookOut');
  };

  this.receive = (type, data) => {
    if (this.handlers[type]) {
      this.handlers[type](data);
      return true;
    }
    return false;
  };

}
