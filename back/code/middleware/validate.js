const userService = require("../routes/userRoutes/userService");

/**
 * middleware fore validate that the user is still in his apartement.
 */
exports.matchUserToApartment = async (req, res, next) => {
  const { apartmentId, userId } = req.tokenData;
  if (userId === undefined) {
    return res.status(403).send({ success: false, msg: "userId undefined" });
  }

  if (apartmentId === undefined)
    return res
      .status(403)
      .send({ success: false, msg: "user not in apartment" });
      
  try {
    const userApartmentId = await userService.findUserApartmentId(userId);

    if (userApartmentId === apartmentId) {
      next();
    } else {
      return res
        .status(401)
        .send({ success: false, msg: "apartmentId not match" });
    }
  } catch (e) {
    return next(e);
  }
};
