const express = require("express");
const router = express.Router();

const userController = require("./userController");

const { authenticateTokenFromRequest } = require("../../middleware/auth");
const { matchUserToApartment } = require("../../middleware/validate");

router.use(authenticateTokenFromRequest);

router.get("/apartmentId", userController.findUserApartmentId);
router.get("/findById", userController.findById);
router.route("/findByEmail").get(userController.findByEmail); //TODO: check roll admin
router.get("/room8", matchUserToApartment, userController.getRoommatesData);
router.put("/password", userController.changePassword);
router.delete("/delete", userController.delete);

module.exports = router;