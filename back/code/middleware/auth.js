const { authenticateAccessToken } = require("../utilities/jwtHandler");
/** Global middleware function to validate JWT token
 * will return:
 * 401 if token not send
 * 403 if the token is invalid
 * if the token is valid we add to the req the token data
 * add go to the next() function
 */
exports.authenticateTokenFromRequest = (req, res, next) => {
  const token = req.header("x-auth-token");
  if (!token)
    return res
      .status(401)
      .send({ success: false, msg: "Send JWT token to make this request" });

  const tokenData = authenticateAccessToken(token);
  if (tokenData === undefined)
    return res.status(403).send({ success: false, msg: "Token is Invalid" });

  if (tokenData.expired)
    return res
      .status(401)
      .send({ success: false, msg: "Token is expired", expired: true });

  req.tokenData = tokenData;
  next();
};
