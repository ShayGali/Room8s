const express = require("express");
const router = express.Router();

const apartmentController = require("../controllers/apartmentController");

const { authenticateTokenFromRequest } = require("../middleware/auth");
const { matchUserToApartment } = require("../middleware/validate");
const { isApartmentOwner } = require("../middleware/rolesCheck");

router.use(authenticateTokenFromRequest);

router.post("/create", apartmentController.createApartment);

router
  .route("/data")
  .get(matchUserToApartment, apartmentController.getApartmentData);

router.post(
  "/addUser",
  isApartmentOwner,
  apartmentController.addUserToApartment
);

router.delete(
  "/removeUserFromApartment",
  matchUserToApartment,
  isApartmentOwner,
  apartmentController.removeUserFromApartment
);

module.exports = router;
