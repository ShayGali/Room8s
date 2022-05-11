require("dotenv").config();

const JWT = require("jsonwebtoken");

const express = require("express");
const router = express.Router();

const userController = require("../controllers/userController");

const { authenticateToken } = require("../server");

router.get("/apartmentId", authenticateToken, userController.findUserApartment);
router.get("/findById", authenticateToken, userController.findById);
module.exports = router;
