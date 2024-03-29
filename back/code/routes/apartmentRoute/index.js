const express = require("express");
const router = express.Router();

const apartmentController = require("./apartmentController");

const { authenticateTokenFromRequest } = require("../../middleware/auth");
const { matchUserToApartment } = require("../../middleware/validate");
const { isApartmentOwner } = require("../../middleware/rolesCheck");

router.use(authenticateTokenFromRequest); //all the requests need to have a valid JWT token

router.post("/create", apartmentController.createApartment);

router.get("/data",matchUserToApartment, apartmentController.getApartmentData);


router.get("/joinReq", apartmentController.getJoinReq);

router.post(
  "/sendJoinReq",
  matchUserToApartment,
  isApartmentOwner,
  apartmentController.sendJoinReq
);
router.post("/handleJoinReq", apartmentController.handleJoinReq);

router.delete(
  "/removeUserFromApartment/:userId",
  matchUserToApartment,
  isApartmentOwner,
  apartmentController.removeUserFromApartment
);

router.delete("/leave", matchUserToApartment, apartmentController.leave);

router.delete(
  "/delete",
  matchUserToApartment,
  isApartmentOwner,
  apartmentController.deleteApartment
);

module.exports = router;
