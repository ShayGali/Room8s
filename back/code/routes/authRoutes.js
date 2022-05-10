require("dotenv").config();

const express = require("express");
const router = express.Router();

const authController = require("../controllers/authController");

const { authenticateToken } = require("../server");

router.route("/login").post(authController.login);
router.route("/register").post(authController.register);
router.route("/findByEmail").get(authController.findByEmail); //TODO: delete. only for development
router.route("/hello").get(authenticateToken, authController.hello);

module.exports = router;
