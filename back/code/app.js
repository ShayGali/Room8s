const SocketServer = require("websocket").server;
const { app } = require("./http server");

const server = require("http").createServer(app);
const webSocketServer = new SocketServer({ httpServer: server });

let connections = [];

webSocketServer.on("request", (req) => {
  const connection = req.accept();

  console.log(`New connection`);
  connections.push(connection);

  connection.on("message", (msg) => {
    console.log(msg);
    connections.forEach((element) => {
      if (element !== connection) element.sendUTF(msg.utf8Data);
    });
  });

  connection.on("close", (resCode, desc) => {
    console.log("connection closed");
    connections = connections.filter((element) => element !== connection);
  });
});

const PORT = process.env.PORT || 3000;

server.listen(PORT, () => console.log(`Listening in port ${PORT}`));
