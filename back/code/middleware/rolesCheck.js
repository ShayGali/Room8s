const userService = require("../routes/userRoutes/userService");

/**
 * Check if the user roll is apartment owner or above.
 * if not we send an 403 error message
 */
exports.isApartmentOwner = async (req, res, next) => {
  const { userId } = req.tokenData;
  if (userId === undefined) {
    return res.status(403).send({ success: false, msg: "userId undefined" });
  }

  try {
    const user = await userService.findById(userId);

    if (user === undefined) {
      return res.status(404).send({ success: false, msg: "user not found" });
    }

    if (user.user_level >= 2) {
      req.userLevel = user.user_level;
      next();
    } else {
      return res.status(403).send({
        success: false,
        msg: "you cant do that, you need an apartment owner(level 2) privileges or above",
      });
    }
  } catch (e) {
    return next(e);
  }
};

/**
 * Check if the user roll is admin or above.
 * if not we send an 403 error message
 */
exports.isAdmin = async (req, res, next) => {
  const { userId } = req.tokenData;
  if (userId === undefined) {
    return res.status(403).send({ success: false, msg: "userId undefined" });
  }

  try {
    const user = await userService.findById(userId);
    if (user === undefined) {
      return res.status(404).send({ success: false, msg: "user not found" });
    }

    if (user.user_level >= 3) {
      req.userLevel = user.user_level;
      next();
    } else {
      return res.status(403).send({
        success: false,
        msg: "you cant do that, you need an admin(level 3) privileges or above",
      });
    }
  } catch (e) {
    return next(e);
  }
};
