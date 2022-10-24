const emailRegex = /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/;

/**
 *
 * @param {string} password
 * @returns {boolean}
 */
exports.validateEmail = (email) => {
  return (
    email !== undefined && typeof email == "string" && emailRegex.test(email)
  );
};
