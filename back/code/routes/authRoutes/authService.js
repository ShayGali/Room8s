const db = require("../../config/db");
const usersTable = "users";

/**
 * 
 * @param {*} user 
 * @returns 
 */
exports.register = async (user) => {
  const query = `INSERT INTO ${usersTable} (
          user_name,email,user_password
      )
      VALUE (?,?,?);
  `;
  const [newUser, _] = await db.execute(query, [
    user.username,
    user.email,
    user.password,
  ]);
  return newUser;
};
