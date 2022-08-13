const express = require("express");
const router = express.Router();

const authController = require("./authController");

const { authenticateTokenFromRequest } = require("../../middleware/auth");

router.route("/login").post(authController.login);
router.route("/register").post(authController.register);
router.route("/hello").get(authenticateTokenFromRequest, authController.hello);

module.exports = router;
