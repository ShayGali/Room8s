const SocketServer = require("websocket").server;
const app = require("./rest control");

const server = require("http").createServer(app);

const { authenticateToken } = require("./middleware/auth");
const webSocketServer = new SocketServer({
  httpServer: server,
  path: "/messages",
});

let connections = {};

webSocketServer.on("request", async (req) => {
  const token = authenticateToken(req.httpRequest.headers["x-auth-token"]);

  if (!token) {
    console.log("reject connection - token invalid");
    req.reject(401, "invalid token");
    return;
  }
  if (!token.apartmentId) {
    console.log("reject connection - user dont have apartment");
    req.reject(403, "user dont have apartment");
    return;
  }

  const connection = req.accept();
  console.log(`New connection`);

  if (connections[token.apartmentId] != undefined) {
    connections[token.apartmentId].push(connection);
  } else {
    connections[token.apartmentId] = [connection];
  }

  connection.on("message", (msg) => {
    connections[token.apartmentId].forEach((element) => {
      if (element !== connection) element.sendUTF(msg.utf8Data);
    });
  });

  connection.on("close", (resCode, desc) => {
    console.log(`connection closed. resCode:${resCode}, description: ${desc}`);

    currentApartmentConnections = connections[token.apartmentId];

    currentApartmentConnections = currentApartmentConnections.filter(
      (element) => element !== connection
    );
    if (currentApartmentConnections.length == 0) {
      delete connections[token.apartmentId];
    }
  });
});

const PORT = process.env.PORT || 3000;

server.listen(PORT, () => console.log(`Listening in port ${PORT}`));
