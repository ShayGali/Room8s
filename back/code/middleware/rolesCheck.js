const userService = require("../service/userService");

exports.isApartmentOwner = async (req, res, next) => {
  const { userId } = req.tokenData;
  if (userId === undefined) {
    return res.status(403).send({ msg: "userId undefined" });
  }

  try {
    const user = await userService.findById(userId);

    if (user === undefined) {
      return res.status(404).send({ msg: "user not found" });
    }

    if (user.user_level >= 2) {
      next();
    } else {
      return res.status(403).send({
        msg: "you cant do that, you need an apartment owner(level 2) privileges or above",
      });
    }
  } catch (e) {
    return next(e);
  }
};

exports.isAdmin = async (req, res, next) => {
  const { userId } = req.tokenData;
  if (userId === undefined) {
    return res.status(403).send({ msg: "userId undefined" });
  }

  try {
    const user = await userService.findById(userId);
    if (user === undefined) {
      return res.status(404).send({ msg: "user not found" });
    }

    if (user.user_level >= 3) {
      next();
    } else {
      return res.status(403).send({
        msg: "you cant do that, you need an admin(level 3) privileges or above",
      });
    }
  } catch (e) {
    return next(e);
  }
};
