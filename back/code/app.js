require("dotenv").config();

const restServer = require("./rest control");

const server = require("http").createServer(restServer);
module.exports = { server };

require("./routes/messageSocket"); // using the message route

const PORT = process.env.PORT || 3000;

server.listen(PORT, () => console.log(`Listening in port ${PORT}`));
