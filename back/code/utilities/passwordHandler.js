const bcrypt = require("bcrypt");

/**
 *
 * @param {string} password
 * @returns {Promise<string>}
 */
exports.hashPassword = async (password) => {
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
