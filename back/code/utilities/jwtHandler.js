const JWT = require("jsonwebtoken");

const ACCESS_TOKEN_EXPIRATION = "365d";
const REFRESH_TOKEN_EXPIRATION = "365d";

/**
 * check if the token is valid
 * @param {string} token
 * @returns {undefined | {userId: number, apartmentId: number, iat: number, exp: number}}
 */
exports.authenticateToken = (token) => {
  if (!token) return;
  return JWT.verify(
    token,
    process.env.ACCESS_TOKEN_SECRET,
    (err, tokenData) => {
      if (err) return;
      return tokenData;
    }
  );
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
}
