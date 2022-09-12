const { v4: uuidv4 } = require("uuid");

const authService = require("./authService");
const userService = require("../../routes/userRoutes/userService");

const bcrypt = require("bcrypt");

const {
  generateAccessToken,
  generateRefreshToken,
} = require("../../utilities/jwtHandler");
const {
  isStrongPassword,
  hashPassword,
  compareHashPassword
} = require("../../utilities/passwordHandler");
const valuesValidate = require("../../utilities/valuesValidate");

const resetTokenMap = new Map();

exports.register = async (req, res, next) => {
  const { username, email, password } = req.body;
  if (!username || !email || !password) {
    return res.status(400).json({ success: false, msg: "Send all data" });
  }

  if (!valuesValidate.validateEmail(email)) {
    return res.status(400).json({ success: false, msg: "email not valid" });
  }
  if (!isStrongPassword(password)) {
    return res.status(400).json({ success: false, msg: "password not valid" });
  }

  try {
    const salt = await bcrypt.genSalt();
    const hashPassword = await bcrypt.hash(password, salt);

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
      password: hashPassword,
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

    const findUser = await userService.findByEmailOrUsername(email);
    if (!findUser) {
      return res.status(401).json({ success: false, msg: "Invalid credentials" });
    }
    if (!(await compareHashPassword(password, findUser.user_password))) {
      return res.status(401).json({ success: false, msg: "Invalid credentials" });
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

exports.hello = async (req, res, next) => {
  return res.send({ success: true, msg: "hello", jwtToken: req.tokenData });
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
  const expirityDate = generateExprityDate();

  resetTokenMap.set(resetTokenUUID, { expirityDate, email });

  sendEmail(email, resetTokenUUID);

  return res.send({ success: true, msg: "reset token send to ypur email" });
};

// genrate a expirtion time
function generateExprityTime() {
  const EXPIRATION_TIME_IN_MINUTES = 15;
  return new Date().getTime() + EXPIRATION_TIME_IN_MINUTES * 60000;
}

// check if the token of the email expired
function checkIfExpired(time) {
  return time - Date.now() < 0;
}

// TODO
function sendEmail(email, token) {}

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
