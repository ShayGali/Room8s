const db = require("../../config/db");
const messagingTable = "messaging";
const usersTable = "users";

/**
 * save the message to the DB.
 * return the ID of the message in object with the passed UUID
 * @param {number} apartmentId
 * @param {number} userId
 * @param {string} message
 * @param {string} timestamp
 * @param {string} messageUUID
 * @returns {Promise<{insertId: number; messageUUID: string;}>}
 */
exports.saveMessageToDB = async (
  apartmentId,
  userId,
  message,
  timestamp,
  messageUUID
) => {
  const query = `
      INSERT INTO ${messagingTable} (
        apartment_ID, sender_ID, msg_data, send_time
      )
      VALUE (?,?,?,?);
  `;

  let result = await db.execute(query, [
    apartmentId,
    userId,
    message,
    timestamp,
  ]);

  let insertId = result[0].insertId;
  return { insertId, messageUUID };
};

exports.getMessages = async (apartmentId, userId) => {
  const query = `
    SELECT ${messagingTable}.ID as messageId, sender_id, user_name, profile_icon_id, msg_data AS message, send_time AS timestamp
    FROM ${messagingTable}
    INNER JOIN ${usersTable} ON
    sender_id = ${usersTable}.ID
    WHERE apartment_ID = ?
    `;
  let res = await db.execute(query, [apartmentId]);

  // foramt the date and add the isSent flag
  let formatMsg = res[0].map((msg) => {
    msg.isSent = msg.sender_id == userId; // add the isSent flag 
    msg.timestamp = msg.timestamp.toISOString().slice(0, 19).replace("T", " "); // formt the msg timestamp
    delete msg.sender_id;
    return msg;
  });
  return formatMsg;
};
