const emailRegex = /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/;

/**
 *
 * @param {string} password
 * @returns {boolean}
 */
exports.validateEmail = (email) => {
  return email !== undefined && emailRegex.test(email);
};

/**
 *
 * @param {string} password
 * @returns {boolean}
 */
exports.validatePassword = (password) => {
  return true;
};
