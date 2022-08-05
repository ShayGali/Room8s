const authService = require("../service/authService");
const userService = require("../service/userService");
const bcrypt = require("bcrypt");
const JWT = require("jsonwebtoken");

// TODO: check if user name is exist
exports.register = async (req, res, next) => {
  try {
    const { username, email, password } = req.body;
    if (!username || !email || !password) {
      return res.status(400).json({ msg: "Send all data" });
    }
    const salt = await bcrypt.genSalt();
    const hashPassword = await bcrypt.hash(password, salt);

    let checkUserExists = await userService.findByEmail(email);
    if (checkUserExists !== undefined) {
      return res.status(409).json({ msg: result.errorMsg });
    }

    const result = await authService.register({
      username,
      email,
      password: hashPassword,
    });
    if (result.errorMsg !== undefined) {
      return res.status(409).json({ msg: result.errorMsg });
    }

    jwtToken = await generateToken({
      userId: result.insertId,
      apartmentId: null,
    });
    return res
      .status(201)
      .json({ msg: "success", jwtToken, databaseChanges: result });
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
      return res.status(401).json({ msg: "Invalid email" }); //TODO: change the msg
    }
    if (!(await bcrypt.compare(password, findUser.user_password))) {
      return res.status(401).json({ msg: "Invalid password" });
    }

    const apartmentId = await userService.findUserApartmentId(findUser.ID);
    const jwtToken = await generateToken({
      userId: findUser.ID,
      apartmentId: apartmentId !== undefined ? apartmentId : null,
    });

    return res.status(200).json({
      msg: "success",
      jwtToken,
      userId: findUser.ID,
      apartmentId: apartmentId !== undefined ? apartmentId : null,
    });
  } catch (err) {
    next(err);
  }
};

async function generateToken(data) {
  const token = await JWT.sign(
    {
      ...data,
    },
    process.env.ACCESS_TOKEN_SECRET,
    { expiresIn: "365d" }
  );
  return token;
}

//TODO
async function generateRefreshToken(data) {}

exports.hello = async (req, res, next) => {
  return res.send({ msg: "hello", token: req.tokenData });
};
