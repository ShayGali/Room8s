const express = require("express");
const router = express.Router();

const authController = require("./authController");

router.route("/login").post(authController.login);
router.route("/register").post(authController.register);
router.get("/refresh", authController.refreshToken);
router.route("/forgotPassword").get().post(authController.forgotPassword);

module.exports = router;
