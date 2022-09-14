const db = require("../../config/db");
const usersTable = "users";
const apartmentsUserTable = "user_in_apartment";
const userLevelTable = "user_level";

exports.findUserApartmentId = async (userID) => {
  if (userID === undefined) return;
  const query = `
  select apartment_ID as apartmentId
  from user_in_apartment
  where user_ID = ?`;

  const [id, _] = await db.execute(query, [userID]);
  return id[0]?.apartmentId;
};

exports.findById = async (userID) => {
  const query = `SELECT ${usersTable}.*,level_name FROM ${usersTable}
  INNER JOIN ${userLevelTable}
  ON ${usersTable}.user_level = ${userLevelTable}.ID
  WHERE ${usersTable}.ID = ? ;`;

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

exports.findByEmailOrUsername = async (emailOrUsername) => {
  const query = `SELECT ${usersTable}.*,level_name FROM ${usersTable}
    INNER JOIN ${userLevelTable}
    ON ${usersTable}.user_level = ${userLevelTable}.ID
    WHERE email = ? OR user_name = ?;
    `;
  const [user, _] = await db.execute(query, [emailOrUsername, emailOrUsername]);
  return user[0];
};

exports.findByUserName = async (userName) => {
  const query = `SELECT ${usersTable}.*,level_name FROM ${usersTable}
    INNER JOIN ${userLevelTable}
    ON ${usersTable}.user_level = ${userLevelTable}.ID
    WHERE user_name = ?
    `;
  const [user, _] = await db.execute(query, [userName]);
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

exports.getRoommatesData = async (apartmentId, userId) => {
  const query = `SELECT user_with_level.* FROM 
    (SELECT ${usersTable}.ID, user_name, user_level,level_name, profile_icon_id
    FROM ${usersTable}
    INNER JOIN ${userLevelTable}
    ON ${usersTable}.user_level = ${userLevelTable}.ID) AS user_with_level
    INNER JOIN ${apartmentsUserTable}
    ON user_with_level.ID = ${apartmentsUserTable}.user_ID
    WHERE apartment_ID = ? AND NOT user_with_level.ID = ?; 
    `;
  const [result, _] = await db.execute(query, [apartmentId, userId]);
  return result;
};

exports.changeRole = async (userId, roleId) => {
  const [r, _] = await db.execute(
    `SELECT * FROM ${userLevelTable} WHERE ID = ?`,
    [roleId]
  );
  if (r[0] === undefined) return;
  const query = `UPDATE ${usersTable}
   SET user_level = ?
   WHERE ID = ?
   `;
  const res = await db.execute(query, [roleId, userId]);
  return res;
};

/**
 *
 * @param {number} userId
 * @param {string} password
 */
exports.changePassword = (userId, password) => {
  const query = `UPDATE ${usersTable}
   SET user_password = ?
   WHERE ID = ?
   `;
  db.execute(query, [password, userId]);
};

exports.changeProfileImg = (userId, iconId) => {
  const query = `UPDATE ${usersTable}
   SET profile_icon_id = ?
   WHERE ID = ?
   `;
  db.execute(query, [iconId, userId]);
};
