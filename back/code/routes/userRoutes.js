require("dotenv").config();

const JWT = require("jsonwebtoken");

const express = require("express");
const router = express.Router();

const userController = require("../controllers/userController");

const { authenticateToken } = require("../middleware/auth");

router.get("/apartmentId", userController.findUserApartment);
router.get("/findById", authenticateToken, userController.findById);
router.route("/findByEmail").get(userController.findByEmail); //TODO: check roll admin

module.exports = router;
