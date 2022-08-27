const apartmentService = require("./apartmentService");
const userService = require("../userRoutes/userService");

const { generateAccessToken } = require("../../utilities/jwtHandler");
/**
 * get the data of the apartment of the user
 * if the dont have apartment it will return message
 */
exports.getApartmentData = async (req, res, next) => {
  const { userId } = req.tokenData;
  try {
    const userApartmentId = await userService.findUserApartmentId(userId);

    if (!userApartmentId) {
      return res
        .status(200)
        .send({ success: false, msg: "dont have apartment" });
    }

    const apartmentData = await apartmentService.getData(userApartmentId);

    return res
      .status(200)
      .send({ success: false, msg: "success", data: apartmentData });
  } catch (err) {
    next(err);
  }
};

/**
 * create new apartment for the user that send the request
 * if the user already in apartment it send message
 * else it will return success message with the new apartment id
 */
exports.createApartment = async (req, res, next) => {
  const { userId } = req.tokenData;
  const { name } = req.body;

  if (name === undefined) {
    return res
      .status(400)
      .send({ success: false, msg: "need to send a apartment name" });
  }

  if (await userService.findUserApartmentId(userId)) {
    return res
      .status(200)
      .send({ success: false, msg: "user are already in apartment" });
  }
  try {
    const apartmentId = await apartmentService.createApartment(userId, name);
    userService.changeRole(userId, 2);

    res.status(201).send({
      success: true,
      msg: "success",
      data: {
        apartmentId,
        token: generateAccessToken({ userId, apartmentId }),
      },
    });
  } catch (err) {
    next(err);
  }
};

/**
 * add new user to the apartment of the user that send the request
 *
 * we check:
 *  if the sender dont send the new user id, we will return 400
 *  if we dont find the new user we will return 200 with message
 *  if the user that send the request is not in a apartment
 *  if the new user that we try to add is already in apartment
 *
 * else we will return 201
 */

exports.addUserToApartment = async (req, res, next) => {
  const { apartmentId } = req.tokenData;

  const { newUserId } = req.body;

  if (!newUserId) {
    return res
      .status(400)
      .send({ success: false, msg: "new user id is required" });
  }

  try {
    if (await userService.findUserApartmentId(newUserId)) {
      return res
        .status(200)
        .send({ success: false, msg: "user are already in apartment" });
    }

    const result = await apartmentService.addUserToApartment(
      apartmentId,
      newUserId
    );
    if (result)
      return res
        .status(201)
        .send({
          success: true,
          msg: `user with the id ${newUserId} add to apartment ${apartmentId}`,
        });
  } catch (err) {
    next(err);
  }
};

exports.removeUserFromApartment = async (req, res, next) => {
  const { apartmentId } = req.tokenData;
  const { userId } = req.params;

  if (userId === undefined) {
    return res.status(400).json({
      success: false,
      msg: "you need to send userId in the request params",
    });
  }

  try {
    const result = await apartmentService.removeUserFromApartment(
      apartmentId,
      +userId
    );
    res.json({
      success: true,
      msg: `user ${userId} has deleted from apartment ${apartmentId}`,
    });
  } catch (err) {
    next(err);
  }
};

exports.deleteApartment = async (req, res, next) => {
  const { apartmentId } = req.tokenData;
  try {
    apartmentService.deleteApartment(apartmentId);
    res.send({ success: true, msg: "apartment deleted" });
  } catch (error) {
    next(error);
  }
};
