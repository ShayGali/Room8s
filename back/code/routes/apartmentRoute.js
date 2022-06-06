require("dotenv").config();

const express = require("express");
const router = express.Router();

const apartmentController = require("../controllers/apartmentController");

const { authenticateTokenFromRequest } = require("../middleware/auth");

router.post(
  "/create",
  authenticateTokenFromRequest,
  apartmentController.createApartment
);

router
  .route("/data")
  .get(authenticateTokenFromRequest, apartmentController.getApartmentData);

//TODO: add middleware to check the user role to be an apartment_owner
router.post(
  "/addUser",
  authenticateTokenFromRequest,
  apartmentController.addUserToApartment
);

router.delete(
  "/removeUserFromApartment",
  apartmentController.removeUserFromApartment
);

module.exports = router;
