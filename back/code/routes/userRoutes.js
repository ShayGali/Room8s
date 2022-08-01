const express = require("express");
const router = express.Router();

const userController = require("../controllers/userController");

const { authenticateTokenFromRequest } = require("../middleware/auth");

router.use(authenticateTokenFromRequest);

router.get("/apartmentId", userController.findUserApartmentId);
router.get("/findById", userController.findById);
router.route("/findByEmail").get(userController.findByEmail); //TODO: check roll admin

router.delete("/delete", userController.delete);

module.exports = router;
