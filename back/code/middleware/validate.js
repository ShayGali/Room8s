const userService = require("../service/userService");

exports.matchUserToApartment = async (req, res, next) => {
  const { apartmentId, userId } = req.tokenData;

  if (userId === undefined) {
    return res.status(403).send({ msg: "userId undefined" });
  }

  if (apartmentId === undefined)
    return res.status(403).send({ msg: "user not in apartment" });
  try {
    const userApartmentId = await userService.findUserApartmentId(userId);

    console.log(userApartmentId);
    console.log(apartmentId);
    if (userApartmentId === apartmentId) {
      next();
    } else {
      return res.status(401).send({ msg: "apartmentId not match" });
    }
  } catch (e) {
    return next(e);
  }
};
