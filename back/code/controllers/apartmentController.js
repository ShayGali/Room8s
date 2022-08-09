const apartmentService = require("../service/apartmentService");
const userService = require("../service/userService");

const { generateAccessToken } = require("../utilities/jwtHandler");
/**
 * get the data of the apartment of the user
 * if the dont have apartment it will return message
 */
exports.getApartmentData = async (req, res, next) => {
  const { userId } = req.tokenData;
  try {
    const userApartmentId = await userService.findUserApartmentId(userId);

    if (!userApartmentId) {
      return res.status(200).send({ msg: "dont have apartment" });
    }

    const apartmentData = await apartmentService.getData(userApartmentId);

    return res.status(200).send({ msg: "success", data: apartmentData });
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
  const { userId } = req.tokenData;

  const { newUserId } = req.body;

  if (!newUserId) {
    return res.status(400).send({ msg: "new user id is required" });
  }

  try {
    if (await userService.findUserApartmentId(newUserId)) {
      return res.status(200).send({ msg: "user are already in apartment" });
    }

    const result = await apartmentService.addUserToApartment(
      userApartmentId,
      newUserId
    );
    if (result)
      return res.status(201).send({
        msg: `user with the id ${newUserId} add to apartment ${userApartmentId}`,
      });
  } catch (err) {
    next(err);
  }
};

exports.removeUserFromApartment = async (req, res, next) => {
  const { userId } = req.params;

  try {
    const result = await apartmentService.removeUserFromApartment(11, 15);
    res.send(result);
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
