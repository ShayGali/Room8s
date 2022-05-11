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
exports.findById = async (req, res, next) => {
  const { userId } = req.body;
  const result = await userService.findById(userId);
  if (!result) {
    return res.status(404).send({ msg: "User not found" });
  }
  delete result["user_password"];
  return res.send({ msg: "success", result });
};
