const express = require("express");
const router = express.Router();

const apartmentController = require("./apartmentController");

const { authenticateTokenFromRequest } = require("../../middleware/auth");
const { matchUserToApartment } = require("../../middleware/validate");
const { isApartmentOwner } = require("../../middleware/rolesCheck");

router.use(authenticateTokenFromRequest);

router.post("/create", apartmentController.createApartment);

router.route("/data").get(apartmentController.getApartmentData);

router.post(
  "/addUser",
  matchUserToApartment,
  isApartmentOwner,
  apartmentController.addUserToApartment
);

router.delete(
  "/removeUserFromApartment",
  matchUserToApartment,
  isApartmentOwner,
  apartmentController.removeUserFromApartment
);
router.delete(
  "/delete",
  matchUserToApartment,
  isApartmentOwner,
  apartmentController.deleteApartment
);

module.exports = router;
