const db = require("../config/db");
const usersTable = "users";

exports.findUserApartment = async (userID) => {
  const query = `
  select apartment_ID as apartmentId
  from user_in_apartment
  where user_ID = ?`;

  const [id, _] = await db.execute(query, [userID]);
  return id[0];
};
