const apartmentService = require("./apartmentService");
const userService = require("../userRoutes/userService");

const { generateAccessToken } = require("../../utilities/jwtHandler");

/**
 * get the data of the apartment of the user
 * if the dont have apartment it will return message
 */
exports.getApartmentData = async (req, res, next) => {
  const { apartmentId } = req.tokenData;
  try {
    const apartmentData = await apartmentService.getData(apartmentId);

    return res
      .status(200)
      .send({ success: true, msg: "success", data: apartmentData });
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

  if (await userService.findUserApartmentId(userId) !== undefined) { // check if the user already in apartment
    return res
      .status(200)
      .send({ success: false, msg: "user are already in apartment" });
  }

  try {
    const apartmentId = await apartmentService.createApartment(userId, name);
    userService.changeRole(userId, 2); // cahnge his role to apartemnt owner

    res.status(201).send({
      success: true,
      msg: "success",
      data: {
        apartmentId,
        jwtToken: generateAccessToken({ userId, apartmentId }), // create new token
      },
    });
  } catch (err) {
    next(err);
  }
};


/**
 * handler for user that request to leave his apartment
 */
exports.leave = async (req, res, next) => {
  const { userId, apartmentId } = req.tokenData;
  try {
    await apartmentService.removeUserFromApartment(apartmentId, userId);
    const newToken = generateAccessToken({ userId, apartmentId: null }); // the user need new token without the apartmentId
    res.json({
      success: true,
      msg: `user ${userId} has deleted from apartment ${apartmentId}`,
      jwtToken: newToken,
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

  if(isNaN(userId)){
    return res.status(400).json({
      success: false,
      msg: "userId need to be number",
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

exports.sendJoinReq = async (req, res, next) => {
  const { userId: senderId, apartmentId } = req.tokenData;
  const { identify } = req.body;
  if (identify === undefined)
    return res
      .status(400)
      .json({ success: false, msg: "send email or username" });
  try {
    const user = await userService.findByEmailOrUsername(identify);

    if (user === undefined)
      return res.status(404).json({
        success: false,
        msg: "the user that you try to add was not found",
      });

    if (user.ID === senderId)
      return res.status(400).json({
        success: false,
        msg: "you cant send request to yourself",
      });

    if ((await userService.findUserApartmentId(user.ID)) !== undefined)
      return res
        .status(200)
        .json({ success: false, msg: "user is already in apartment" });

    if (await apartmentService.checkIfThereAreJoinReq(apartmentId, user.ID))
      return res.status(500).json({
        success: false,
        msg: "A request to join the apartment already exists",
      });

    apartmentService.sendJoinReq(apartmentId, user.ID, senderId);
    res.status(201).json({ success: true, msg: "request send successfully" });
  } catch (error) {
    next(error);
  }
};

exports.handleJoinReq = async (req, res, next) => {
  const { userId } = req.tokenData;
  const { apartmentId, join } = req.body;

  if (apartmentId === undefined)
    return res.status(400).json({ success: false, msg: "send apartmentId" });

  try {
    const userApartmentId = await userService.findUserApartmentId(userId);
    if (userApartmentId !== undefined) {
      const newToken = generateAccessToken({ userId, apartmentId });
      return res.status(200).json({
        success: false,
        msg: "user is already in apartment",
        jwtToken: newToken,
      });
    }

    if (join === "true") {
      let newToken = undefined;
      if (apartmentService.removeJoinReq(apartmentId, userId)) {
        if (
          (await apartmentService.addUserToApartment(apartmentId, userId)) ===
          undefined
        ) {
          return res
            .status(500)
            .json({ success: false, msg: "cant add you to the room" });
        }
        newToken = generateAccessToken({ userId, apartmentId: +apartmentId });
      }

      return res.status(201).json({
        success: true,
        msg: "user add to the apartment",
        jwtToken: newToken,
      });
    } else {
      apartmentService.removeJoinReq(apartmentId, userId);
      return res.status(200).json({ success: true, msg: "success" });
    }
  } catch (error) {
    next(error);
  }
};

exports.getJoinReq = async (req, res, next) => {
  const { userId } = req.tokenData;
  try {
    const result = await apartmentService.getJoinReq(userId);
    return res.json({
      success: true,
      msg: `join request for ${userId}`,
      data: result,
    });
  } catch (error) {
    next(error);
  }
};
