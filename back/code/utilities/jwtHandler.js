const JWT = require("jsonwebtoken");

const ACCESS_TOKEN_EXPIRATION = "30m";
const REFRESH_TOKEN_EXPIRATION = "365d";

/**
 * check if the token is valid
 * @param {string} token
 * @param {string} key
 * @returns {undefined | {userId: number, apartmentId: number, iat: number, exp: number}}
 */
function authenticateToken(token, key) {
  if (!token) return;
  return JWT.verify(token, key, (err, tokenData) => {
    if (err) {
      if (err.name === "TokenExpiredError") return { expired: true };
    }
    return tokenData;
  });
}

exports.authenticateAccessToken = (token) => {
  return authenticateToken(token, process.env.ACCESS_TOKEN_SECRET);
};
exports.authenticateRefreshToken = (token) => {
  return authenticateToken(token, process.env.REFRESH_TOKEN_SECRET);
};
/**
 * generate ner JWT access token with the data in the body of the token
 * @param {any} data
 * @returns {string}
 */
exports.generateAccessToken = (data) => {
  const token = JWT.sign(
    {
      ...data,
    },
    process.env.ACCESS_TOKEN_SECRET,
    { expiresIn: ACCESS_TOKEN_EXPIRATION }
  );
  return token;
};

// generate ner JWT refresh token with the data in the body of the token
exports.generateRefreshToken = (data) => {
  const refToken = JWT.sign(
    {
      ...data,
    },
    process.env.REFRESH_TOKEN_SECRET,
    { expiresIn: REFRESH_TOKEN_EXPIRATION }
  );
  return refToken;
};
