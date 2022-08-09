const JWT = require("jsonwebtoken");

const ACCESS_TOKEN_EXPIRATION = "365d";
const REFRESH_TOKEN_EXPIRATION = "365d";

/**
 *
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
 *
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

//TODO
async function generateRefreshToken(data) {}
