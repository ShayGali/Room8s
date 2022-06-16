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
    SELECT sender_id, user_name, profile_icon_path, msg_data AS message, send_time AS timestamp
    FROM ${messagingTable}
    INNER JOIN ${usersTable} ON
    sender_id = ${usersTable}.ID
    WHERE apartment_ID = ?
    `;
  let res = await db.execute(query, [apartmentId]);
  let addIfSent = res[0].map((msg) => {
    msg.isSent = msg.sender_id == userId;
    msg.date = msg.timestamp
      .toLocaleDateString()
      .slice(0, 19)
      .replace("T", " ");

    msg.time = msg.timestamp
      .toLocaleTimeString("hr-IS")
      .slice(0, 19)
      .replace("T", " ");

    delete msg.timestamp;
    delete msg.sender_id;

    return msg;
  });
  console.log(addIfSent);
  return addIfSent;
};
