const JWT = require("jsonwebtoken");
/** Global middleware function to validate JWT token
 * will return:
 * 401 if token not send
 * 403 if the token is invalid
 * if the token is valid we add to the req the token data
 * add go to the next() function
 */
exports.authenticateTokenFromRequest = (req, res, next) => {
  console.log("1");
  const token = req.header("x-auth-token");
  if (!token) return res.status(401).send({ msg: "Send token" }); // TODO: make better error message
  JWT.verify(token, process.env.ACCESS_TOKEN_SECRET, (err, tokenData) => {
    if (err) return res.status(403).send({ msg: "Invalid token" });
    req.tokenData = tokenData;
    next(); // move to the function
  });
};

exports.authenticateToken = (token) => {
  console.log("2");

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
