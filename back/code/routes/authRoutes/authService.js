const db = require("../../config/db");
const usersTable = "users";
const emailCol = "email";
const passCol = "user_password";

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

exports.resetPassword = async (email, password) => {
  const query = `UPDATE ${usersTable}
                 SET ${passCol} = '${password}'
                 WHERE ${emailCol} = '${email}';`;
  const [updatedPass,_] = await db.execute(query);
  return updatedPass;
};
