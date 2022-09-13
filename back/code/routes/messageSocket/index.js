const SocketServer = require("websocket").server;

const { server } = require("../../app");

const { authenticateToken } = require("../../utilities/jwtHandler");
const messagingService = require("./messagingService");

const webSocketServer = new SocketServer({
  httpServer: server,
  path: "/messages",
});

const connections = {};

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

  if (connections[token.apartmentId] === undefined) {
    connections[token.apartmentId] = [connection];
  } else {
    connections[token.apartmentId].push(connection);
  }
  (await messagingService.getMessages(token.apartmentId, token.userId)).forEach(
    (msg) => {
      connection.sendUTF(JSON.stringify(msg));
    }
  );

  connection.on("message", (msg) => {
    connections[token.apartmentId].forEach((element) => {
      messageWithTime = JSON.parse(msg.utf8Data);
      let timestamp = new Date().toISOString().slice(0, 19).replace("T", " ");
      messageWithTime.timestamp = timestamp;

      if (element !== connection) {
        messageWithTime.isSent = false;
        element.sendUTF(JSON.stringify(messageWithTime));
      } else {
        messagingService
          .saveMessageToDB(
            token.apartmentId,
            token.userId,
            messageWithTime.message,
            timestamp,
            messageWithTime.UUID
          )
          .then((response) => {
            element.sendUTF(JSON.stringify(response));
          });
      }
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
