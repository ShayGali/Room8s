require("dotenv").config();

const userService = require("../service/userService");

/**
 * get the userId from the JWT token, and return the apartment id
 * if the user nut in an apartment, the apartment id will be null
 */
exports.findUserApartment = async (req, res, next) => {
  const { userId } = req.tokenData;

  try {
    const result = await userService.findUserApartment(userId);

    if (!result) {
      return res
        .status(200)
        .send({ msg: "User not in apartment", apartmentId: null });
    }

    return res
      .status(200)
      .send({ msg: "success", apartmentId: result.apartmentId });
  } catch (err) {
    next(err);
  }
};

//TODO: check rules
/**
 * get user id from the request body,
 * and return the data of the user with out his password
 */
exports.findById = async (req, res, next) => {
  try {
    const { userId } = req.tokenData;
    const result = await userService.findById(userId);
    if (!result) {
      return res.status(404).send({ msg: "User not found" });
    }

    delete result["user_password"]; // for remove the password field from the object
    return res.send({ msg: "success", result });
  } catch (err) {
    next(err);
  }
};

exports.findByEmail = async (req, res, next) => {
  const { email } = req.body;

  if (!email) {
    return res.status(400).send({ msg: "email no send" });
  }
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
