const db = require("../config/db");
const usersTable = "users";

exports.login = async (email, password) => {
  let checkUserExists = await this.findByEmail(email);
  if (!checkUserExists) {
    return { errorMsg: "Email not found" };
  }
  return checkUserExists;
};

exports.register = async (user) => {
  let checkUserExists = await this.findByEmail(user.email);
  if (checkUserExists !== undefined) {
    return { errorMsg: "Email is already registered" };
  }
  const query = `
      INSERT INTO ${usersTable} (
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

//TODO: delete. only for development
exports.findByEmail = async (email) => {
  const query = `
    SELECT * FROM ${usersTable}
    WHERE email = '${email}'
    `;
  const [user, _] = await db.execute(query);
  return user[0];
};
