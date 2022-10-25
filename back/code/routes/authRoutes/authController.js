const { v4: uuidv4 } = require("uuid");

const authService = require("./authService");
const userService = require("../../routes/userRoutes/userService");
const transporter = require("../../config/nodeMailer")

const {
  authenticateRefreshToken,
  generateAccessToken,
  generateRefreshToken,
} = require("../../utilities/jwtHandler");

const {
  isStrongPassword,
  hashPassword,
  compareHashPassword,
} = require("../../utilities/passwordHandler");

const valuesValidate = require("../../utilities/valuesValidate");

const resetTokenMap = new Map();

/**
 * handler for create new user request
 */
exports.register = async (req, res, next) => {
  const { username, email, password } = req.body;
  if (!username || !email || !password) {
    return res.status(400).json({ success: false, msg: "Send all data" });
  }

  if (!valuesValidate.validateEmail(email)) {
    return res.status(400).json({ success: false, msg: "email not valid" });
  }

  if (!isStrongPassword(password)) {
    return res
      .status(400)
      .json({ success: false, msg: "password not strong enough" });
  }

  try {
    const hashedPassword = await hashPassword(password);

    if ((await userService.findByEmailOrUsername(email)) !== undefined) {
      return res
        .status(409)
        .json({ success: false, msg: "email is already exists" });
    }

    if ((await userService.findByUserName(username)) !== undefined) {
      return res
        .status(409)
        .json({ success: false, msg: "username is already exists" });
    }

    const result = await authService.register({
      username,
      email,
      password: hashedPassword,
    });

    jwtToken = generateAccessToken({
      userId: result.insertId,
      apartmentId: null,
    });

    return res.status(201).json({ success: true, msg: "success", jwtToken });
  } catch (err) {
    next(err);
  }
};

/**
 * get email and not hash password
 * find the user by the email
 * and validate the password on this user
 *
 */
exports.login = async (req, res, next) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res
        .status(400)
        .json({ success: false, msg: "send email and password" });
    }

    const findUser = await userService.findByEmailOrUsername(email);

    if (!findUser) {
      return res
        .status(401)
        .json({ success: false, msg: "Invalid credentials" });
    }

    if (!(await compareHashPassword(password, findUser.user_password))) {
      return res
        .status(401)
        .json({ success: false, msg: "Invalid credentials" });
    }

    const apartmentId = await userService.findUserApartmentId(findUser.ID);

    const jwtToken = generateAccessToken({
      userId: findUser.ID,
      apartmentId: apartmentId !== undefined ? apartmentId : null,
    });

    const refreshJwtToken = generateRefreshToken({
      userId: findUser.ID,
      apartmentId: apartmentId !== undefined ? apartmentId : null,
    });

    return res.status(200).json({
      success: true,
      msg: "success",
      jwtToken,
      refreshJwtToken,
      userId: findUser.ID,
      apartmentId: apartmentId !== undefined ? apartmentId : null,
    });
  } catch (err) {
    next(err);
  }
};

exports.forgotPassword = async (req, res, next) => {
  const { email } = req.body;

  if (email === undefined) {
    return res.status(400).json({ success: false, msg: "send email" });
  }

  if ((await userService.findByEmailOrUsername(email)) === undefined)
    return res
      .status(404)
      .json({ success: false, msg: `user with the email ${email} not found` });

  const resetTokenUUID = uuidv4();
  const expirityDate = "21-10-2025"; //TODO: generateExpirationDate() 

  resetTokenMap.set(resetTokenUUID, { expirityDate, email });

  sendEmail(email, resetTokenUUID);
  const hashedPassword = await hashPassword(resetTokenUUID);

  authService.resetPassword(email,hashedPassword);

  return res.send({ success: true, msg: "reset token send to your email" });
};

function sendEmail(email, token) {
  const mailOptions = {
    from: process.env.ADMIN_MAIL,
    to: email,
    subject: "Reset Password",
    text: `${token}\n Here is your new Password, please don't lose it again...`,
  };
  transporter.sendMail(mailOptions, (err, success) => {
    if (err) {
      return res.status(500).send(err);
    }
    res.send("Email sent successfully");
  });
}

/**
 * genrate a expirtion time
 * @returns {number}
 */
function generateExprityTime() {
  const EXPIRATION_TIME_IN_MINUTES = 15;
  return new Date().getTime() + EXPIRATION_TIME_IN_MINUTES * 60000;
}

/**
 * check if the time is expired
 * @param {number} time
 * @returns {boolean}
 */
function checkIfExpired(time) {
  return time - Date.now() < 0;
}

exports.resetPassword = async (req, res, next) => {
  const { token, password } = req.body;

  if (!token)
    return res.status(401).json({ success: false, msg: "send token" });

  if (password === undefined || !isStrongPassword(password))
    return res.status(400).json({ success: false, msg: "password not valid" });

  const { expirtionTime, email } = resetTokenMap.get(token);

  if (checkIfExpired(expirtionTime))
    return res.status(410).json({ success: false, msg: "token expired" });

  const hashedPassword = await hashPassword(password);
  userService.updatePassword(email, hashedPassword); // TODO

  return res.json({ success: true, msg: "password changed" });
};

exports.refreshToken = async (req, res, next) => {
  const token = req.header("x-auth-token");
  if (!token)
    return res
      .status(401)
      .send({ success: false, msg: "Send JWT token to make this request" });

  const tokenData = authenticateRefreshToken(token);

  if (tokenData === undefined)
    return res.status(403).send({ success: false, msg: "Token is Invalid" });

  if (tokenData.expired)
    return res
      .status(401)
      .send({ success: false, msg: "Token is expired", expired: true });

  delete tokenData["iat"];
  delete tokenData["exp"];

  const newAccessToken = generateAccessToken(tokenData);
  return res.status(200).json({
    success: true,
    msg: "success",
    jwtToken: newAccessToken,
  });
};
