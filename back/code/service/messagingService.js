const db = require("../config/db");
const messagingTable = "messaging";

exports.saveMessageToDB = async (apartmentId, userId, message, timestamp) => {
  const query = `
      INSERT INTO ${messagingTable} (
        apartment_ID, sender_ID, msg_data, send_time
      )
      VALUE (?,?,?,?);
  `;
  await db.execute(query, [apartmentId, userId, message, timestamp]);
};
