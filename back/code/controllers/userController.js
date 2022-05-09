const userService = require("../service/userService");
const SHA256 = require("crypto-js/sha256");

exports.register = async (req, res, next) => {
  try {
    const { username, email, password } = req.body;
    const hashPassword = SHA256(password).toString();
    const result = await userService.register({
      username,
      email,
      password: hashPassword,
    });
    if (result.errorMsg !== undefined) {
      return res.status(400).json(result);
    }
    return res.status(201).json({ msg: "success", databaseChanges: result });
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
    const hashPassword = SHA256(password).toString();
    const findUser = await userService.findByEmail(email);
    if (!findUser) {
      return res.status(401).json({ msg: "Invalid email" });
    }
    if (findUser.user_password === hashPassword) {
      return res.status(200).json({
        msg: "success",
        data: {
          user: findUser,
          jwtToken: "TODO",
        },
      });
    } else {
      return res.status(401).json({ msg: "Invalid password" });
    }
  } catch (err) {
    next(err);
  }
};

//TODO: delete. only for development
exports.findByEmail = async (req, res, next) => {
  const { email } = req.body;
  try {
    const result = await userService.findByEmail(email);
    if (!result) {
      return res.status(404).json({ msg: "User not found" });
    }

    return res.status(200).json({ user: result });
  } catch (err) {
    next(err);
  }
};
