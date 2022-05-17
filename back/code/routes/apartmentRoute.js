require("dotenv").config();

const express = require("express");
const router = express.Router();

const apartmentController = require("../controllers/apartmentController");

const { authenticateToken } = require("../middleware/auth");

router.post("/create", authenticateToken, apartmentController.createApartment);

router
  .route("/data")
  .get(authenticateToken, apartmentController.getApartmentData);

//TODO: add middleware to check the user role to be an apartment_owner
router.post(
  "/addUser",
  authenticateToken,
  apartmentController.addUserToApartment
);

router.delete(
  "/removeUserFromApartment",
  apartmentController.removeUserFromApartment
);

module.exports = router;
