const db = require("../config/db");
const usersTable = "users";

exports.findUserApartmentId = async (userID) => {
  if (userID === undefined) return;
  const query = `
  select apartment_ID as apartmentId
  from user_in_apartment
  where user_ID = ?`;

  const [id, _] = await db.execute(query, [userID]);
  return id[0].apartmentId;
};

exports.findById = async (userID) => {
  const query = `
  SELECT * FROM ${usersTable} WHERE ID = ? ;`;

  const [user, _] = await db.execute(query, [userID]);
  return user[0];
};

// Not route function
// get user id and return if he exists
exports.checkIfUserExists = async (userId) => {
  const result = await this.findById(userId);
  return result !== undefined;
};

// Not route function
// get user id and return if he exists
exports.checkIfUserInApartment = async (userId) => {
  const result = await this.findUserApartmentId(userId);
  return result !== undefined;
};

exports.findByEmail = async (email) => {
  const query = `
    SELECT * FROM ${usersTable}
    WHERE email = '${email}'
    `;
  const [user, _] = await db.execute(query);
  return user[0];
};

exports.delete = async (userId) => {
  const query = `
    DELETE FROM ${usersTable}
    WHERE ID = ?
  `;

  const result = await db.execute(query, [userId]);

  return result[0].affectedRows;
};
