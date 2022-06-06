require("dotenv").config();

const authService = require("../service/authService");
const userService = require("../service/userService");
const bcrypt = require("bcrypt");
const JWT = require("jsonwebtoken");

exports.register = async (req, res, next) => {
  try {
    const { username, email, password } = req.body;
    const salt = await bcrypt.genSalt();
    const hashPassword = await bcrypt.hash(password, salt);

    const result = await authService.register({
      username,
      email,
      password: hashPassword,
    });
    if (result.errorMsg !== undefined) {
      return res.status(409).json(result);
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
  console.log("hello");
  try {
    const { email, password } = req.body;

    const findUser = await userService.findByEmail(email);
    if (!findUser) {
      return res.status(401).json({ msg: "Invalid email" }); //TODO: change the msg
    }
    if (!(await bcrypt.compare(password, findUser.user_password))) {
      return res.status(401).json({ msg: "Invalid password" });
    }

    const apartmentId = await userService.findUserApartment(findUser.ID);
    const jwtToken = await generateToken({
      userId: findUser.ID,
      apartmentId: apartmentId !== undefined ? apartmentId.apartmentId : null,
    });
    return res.status(200).json({
      msg: "success",
      jwtToken,
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

exports.hello = async (req, res, next) => {
  return res.send({ msg: "hello", token: req.tokenData });
};
