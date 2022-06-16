const db = require("../config/db");
const messagingTable = "messaging";
const usersTable = "users";

exports.saveMessageToDB = async (apartmentId, userId, message, timestamp) => {
  const query = `
      INSERT INTO ${messagingTable} (
        apartment_ID, sender_ID, msg_data, send_time
      )
      VALUE (?,?,?,?);
  `;
  await db.execute(query, [apartmentId, userId, message, timestamp]);
};

exports.getMessages = async (apartmentId, userId) => {
  const query = `
    SELECT sender_id, user_name ,msg_data AS message, send_time
    FROM ${messagingTable}
    INNER JOIN ${usersTable} ON
    sender_id = ${usersTable}.ID
    WHERE apartment_ID = ?
    `;
  let res = await db.execute(query, [apartmentId]);
  let addIfSent = res[0].map((msg) => {
    msg.isSent = msg.sender_id == userId;
    delete msg.sender_id;
    return msg;
  });
  return addIfSent;
};
