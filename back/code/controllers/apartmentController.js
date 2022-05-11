require("dotenv").config();

const apartmentService = require("../service/apartmentService");
const userService = require("../service/userService");

exports.getApartmentData = async (req, res, next) => {
  const { userId } = req.tokenData;
  try {
    const userApartment = await userService.findUserApartment(userId);

    if (!userApartment) {
      return res.status(200).send({ msg: "dont have apartment" });
    }

    const apartmentId = userApartment.apartmentId;

    const apartmentData = await apartmentService.getData(apartmentId);

    return res.status(200).send({ msg: "success", data: apartmentData });
  } catch (err) {
    next(err);
  }
};

exports.createApartment = async (req, res, next) => {
  const { userId } = req.tokenData;

  if (await userService.findUserApartment(userId)) {
    return res.status(200).send({ msg: "user are already in apartment" });
  }
  try {
    const apartmentId = await apartmentService.createApartment(userId, "hello");
    res.status(200).send({ msg: "success", apartmentId });
  } catch (err) {
    next(err);
  }
};

exports.addUserToApartment = async (req, res, next) => {
  const { userId } = req.tokenData;

  const { newUserId } = req.body;

  if (!newUserId) {
    return res.status(400).send({ msg: "new user id is required" });
  }

  try {
    if (!(await userService.checkIfUserExists(newUserId)))
      return res.status(200).send({ msg: "user not found" });

    const userApartment = await userService.findUserApartment(userId);

    if (!userApartment) {
      return res
        .status(200)
        .send({ msg: "user that try to add new user, not in apartment" });
    }

    if (await userService.findUserApartment(newUserId)) {
      return res.status(200).send({ msg: "user are already in apartment" });
    }

    const apartmentId = userApartment.apartmentId;

    const result = await apartmentService.addUserToApartment(
      apartmentId,
      newUserId
    );
    if (result)
      return res
        .status(201)
        .send({
          msg: `user with the id ${newUserId} add to apartment ${apartmentId}`,
        });
  } catch (err) {
    next(err);
  }
};
