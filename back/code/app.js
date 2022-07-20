require("dotenv").config();

const restServer = require("./rest control");

const server = require("http").createServer(restServer);
module.exports = { server };

const PORT = process.env.PORT || 3000;

server.listen(PORT, () => console.log(`Listening in port ${PORT}`));

require("./routes/messageSocket"); // using the message route
