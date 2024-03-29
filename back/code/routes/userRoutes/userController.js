const userService = require("./userService");
const apartmentService = require("../../routes/apartmentRoute/apartmentService");

const {
  generateAccessToken,
  generateRefreshToken,
} = require("../../utilities/jwtHandler");

const {
  hashPassword,
  isStrongPassword,
  compareHashPassword,
} = require("../../utilities/passwordHandler");

/**
 * get the userId from the JWT token, and return the apartment id
 * if the user nut in an apartment, the apartment id will be null
 */
exports.findUserApartmentId = async (req, res, next) => {
  const { userId } = req.tokenData;

  try {
    const result = await userService.findUserApartmentId(userId);

    const newToken = generateAccessToken({
      userId,
      apartmentId: result != undefined ? result : null,
    });

    if (!result) {
      return res.status(200).send({
        success: true,
        msg: "User not in apartment",
        apartmentId: null,
        jwtToken: newToken,
      });
    }

    return res.status(200).send({
      success: true,
      msg: "success",
      apartmentId: result,
      wtToken: newToken,
    });
  } catch (err) {
    next(err);
  }
};

/**
 * get user id from the request body,
 * and return the data of the user with out his password
 */
exports.findById = async (req, res, next) => {
  try {
    const { userId } = req.tokenData;
    const result = await userService.findById(userId);
    if (!result) {
      return res.status(404).send({ success: false, msg: "User not found" });
    }

    delete result["user_password"]; // for remove the password field from the object
    return res.send({ success: true, msg: "success", data: result });
  } catch (err) {
    next(err);
  }
};

exports.delete = async (req, res, next) => {
  const { userId, apartmentId } = req.tokenData;
  if (apartmentId) {
    try {
      await apartmentService.removeUserFromApartment(apartmentId, userId);
    } catch (e) {}
  }
  try {
    const result = await userService.delete(userId);
    if (result === 0) {
      return res
        .status(200)
        .json({ success: false, msg: "user don`t deleted for some reason" });
    }
    res.status(200).json({ success: true, msg: "success", data: result });
  } catch (error) {
    next(error);
  }
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

exports.changeRole = async (req, res, next) => {
  try {
    const { userId: senderId, apartmentId } = req.tokenData;
    const { userId, roleNum } = req.body;
    const [user, sender] = await Promise.all([
      userService.findById(userId),
      userService.findById(senderId),
    ]);

    if (sender.user_level < user.user_level) {
      return res
        .status(403)
        .json({ success: false, msg: "you have less role than him" });
    }

    if (roleNum > sender.user_level) {
      return res
        .status(403)
        .json({ success: false, msg: "you cant give that role" });
    }

    if ((await userService.changeRole(userId, roleNum)) == undefined) {
      return res
        .status(400)
        .json({ success: false, msg: "role id not in range" });
    }

    return res.status(200).json({ success: true, msg: "role changed" });
  } catch (error) {
    next(error);
  }
};
exports.changePassword = async (req, res, next) => {
  const { userId } = req.tokenData;
  const { prevPassword, password } = req.body;
  if (prevPassword === undefined) {
    return res.status(400).json({ success: false, msg: "send prev password" });
  }

  try {
    const user = await userService.findById(userId);
    if (!(await compareHashPassword(prevPassword, user.user_password))) {
      return res.status(401).json({ success: false, msg: "Wrong password" });
    }
  } catch (error) {
    next(error);
  }

  if (password === undefined) {
    return res.status(400).json({ success: false, msg: "send new password" });
  }
  if (typeof password !== "string") {
    return res.status(400).json({
      success: false,
      msg: `type of password need to be a string, you passed ${typeof password}`,
    });
  }
  if (!isStrongPassword(password)) {
    return res
      .status(400)
      .json({ success: false, msg: "password not strong enough" });
  }
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

exports.changeProfileImg = async (req, res, next) => {
  const { userId } = req.tokenData;
  const { iconId } = req.body;

  if (iconId == undefined)
    return res.status(400).json({ success: false, msg: "send icodId" });

  if (isNaN(iconId))
    return res
      .status(400)
      .json({ success: false, msg: "icodId need to be number" });
  try {
    userService.changeProfileImg(userId, iconId);
    return res.json({ success: true, msg: "iconId changed successfuly" });
  } catch (error) {
    next(error);
  }
};
