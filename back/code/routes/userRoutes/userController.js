const userService = require("./userService");

const { hashPassword } = require("../../utilities/passwordHandler");

/**
 * get the userId from the JWT token, and return the apartment id
 * if the user nut in an apartment, the apartment id will be null
 */
exports.findUserApartmentId = async (req, res, next) => {
  const { userId } = req.tokenData;

  try {
    const result = await userService.findUserApartmentId(userId);

    if (!result) {
      return res
        .status(200)
        .send({ msg: "User not in apartment", apartmentId: null });
    }

    return res.status(200).send({ msg: "success", apartmentId: result });
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
    return res.send({ msg: "success", data: result });
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

exports.delete = async (req, res, next) => {
  const { userId } = req.tokenData;
  if (!userId) return res.sendStatus(403);
  const result = await userService.delete(userId);
  if (result === 0) {
    return res.status(200).json({ msg: "user don`t deleted for some reason" });
  }
  res.status(200).json({ msg: "success", data: result });
};

exports.getRoommatesData = async (req, res, next) => {
  const { userId, apartmentId } = req.tokenData;
  try {
    const room8 = await userService.getRoommatesData(apartmentId, userId);
    return res.status(200).json({ success: true, msg: "success", data: room8 });
  } catch (error) {
    next(error);
  }
};

exports.changePassword = async (req, res, next) => {
  const { userId } = req.tokenData;
  const { password } = req.body;
  try {
    const hash = await hashPassword(password);
    userService.changePassword(userId, hash);
    return res
      .status(200)
      .json({ success: true, msg: "password change successfully" });
  } catch (error) {
    next(error);
  }
};
