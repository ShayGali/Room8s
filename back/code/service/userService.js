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
  const result = await this.findUserApartment(userId);
  return result !== undefined;
};
