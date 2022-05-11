require("dotenv").config();

const express = require("express");
const router = express.Router();

const apartmentController = require("../controllers/apartmentController");

const { authenticateToken } = require("../server");

router.post("/create", authenticateToken, apartmentController.createApartment);

router
  .route("/data")
  .get(authenticateToken, apartmentController.getApartmentData);

router.post(
  "/addUser",
  authenticateToken,
  apartmentController.addUserToApartment
);

module.exports = router;
