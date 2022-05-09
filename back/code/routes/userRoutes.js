const express = require("express");
const router = express.Router();

const userController = require("../controllers/userController");

router.route("/login").post(userController.login);
router.route("/register").post(userController.register);
router.route("/findByEmail").get(userController.findByEmail); //TODO: delete. only for development

module.exports = router;
