const bcrypt = require("bcrypt");

/**
 *
 * @param {string} password
 * @returns {Promise<string>}
 */
exports.hashPassword = async (password) => {
  const salt = await bcrypt.genSalt();
  const hash = await bcrypt.hash(password, salt);
  return hash;
};

/**
 * 
 * @param {string} password
 * @param {string} hash
 * @returns {Promise<boolean>}
 */
exports.compareHashPassword = async (password, hash) => {
  const res = await bcrypt.compare(password, hash);
  return res;
};

/**
 * check if password is strong
 * @param {string} password
 * @returns {boolean}
 * @throws {TypeError}
 */
exports.isStrongPassword = (password) => {
  if (typeof password !== "string") {
    throw new TypeError(
      `type of password need to be a string, you passed ${typeof password}`
    );
  }

  if (password.length < 6) return false;

  return true;
};
