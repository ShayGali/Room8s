const SocketServer = require("websocket").server;

const { server } = require("../../app"); // the server, fot open the socket

const { authenticateToken } = require("../../utilities/jwtHandler");

const messagingService = require("./messagingService");

const webSocketServer = new SocketServer({
  httpServer: server,
  path: "/messages",
});

/**
 * all the socket connections wiil be store in this object.
 * The object will be in the following format:
 * {apartemtnId (type - number) : connectionArray (type - Array)}
 * so each key will be the apaetment ID and the value will be list of all current user that connet to the message page.
 */
const connections = {};

// handel each request
webSocketServer.on("request", async (req) => {
  // get the token from the request header
  const token = authenticateToken(req.httpRequest.headers["x-auth-token"]);

  if (!token) {
    req.reject(401, "invalid token");
    return;
  }

  if (!token.apartmentId) {
    req.reject(403, "user dont have apartment");
    return;
  }

  const connection = req.accept();

  if (connections[token.apartmentId] === undefined) {
    // if the connections dont have connctions for this apartment
    connections[token.apartmentId] = [connection]; // create new array with this conntion as value and the apartmentId key
  } else {
    // there are already connction for this apatmenr
    connections[token.apartmentId].push(connection); // we add the connetion to the array of connection
  }

  // send all the previous messages from the db
  const previousMsg = await messagingService.getMessages(
    token.apartmentId,
    token.userId
  );
  previousMsg.forEach((msg) => connection.sendUTF(JSON.stringify(msg)));

  // handle messaging
  connection.on("message", (msg) => {
    connections[token.apartmentId].forEach((con) => {
      // loop on all the connetion of the apartment
      messageWithTime = JSON.parse(msg.utf8Data);
      let timestamp = new Date().toISOString().slice(0, 19).replace("T", " ");
      messageWithTime.timestamp = timestamp; // add timestamp to the new message

      // if the current connction of the loop, is not the sender
      if (con !== connection) {
        messageWithTime.isSent = false; // put the flag isSend to be false
        con.sendUTF(JSON.stringify(messageWithTime)); // send the messgae
      } else {
        // if the current connction of the loop, is the sender
        messagingService
          .saveMessageToDB(
            // save the message to the DB
            token.apartmentId,
            token.userId,
            messageWithTime.message,
            timestamp,
            messageWithTime.UUID
          )
          .then((msgIDAndUUID) => {
            con.sendUTF(JSON.stringify(msgIDAndUUID));
          });
      }
    });
  });

  connection.on("close", (resCode, desc) => {
    currentApartmentConnections = connections[token.apartmentId];

    //if there are only one connetion left, we can delete the key of the apartment
    if (currentApartmentConnections.length == 1) {
      delete connections[token.apartmentId];
    } else {
      // remove the cuurent connection from the connections map
      currentApartmentConnections = currentApartmentConnections.filter(
        (con) => con !== connection
      );
    }
  });
});
