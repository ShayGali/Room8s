const authService = require("./authService");
const userService = require("../../routes/userRoutes/userService");

const bcrypt = require("bcrypt");

const { generateAccessToken } = require("../../utilities/jwtHandler");
const { isStrongPassword } = require("../../utilities/passwordHandler");
const valuesValidate = require("../../utilities/valuesValidate");

// TODO: check if user name is exist
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

    if ((await userService.findByEmail(email)) !== undefined) {
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

    const findUser = await userService.findByEmail(email);
    if (!findUser) {
      return res.status(401).json({ success: false, msg: "Invalid email" }); //TODO: change the msg
    }
    if (!(await bcrypt.compare(password, findUser.user_password))) {
      return res.status(401).json({ success: false, msg: "Invalid password" });
    }

    const apartmentId = await userService.findUserApartmentId(findUser.ID);
    const jwtToken = generateAccessToken({
      userId: findUser.ID,
      apartmentId: apartmentId !== undefined ? apartmentId : null,
    });

    return res.status(200).json({
      success: true,
      msg: "success",
      jwtToken,
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
