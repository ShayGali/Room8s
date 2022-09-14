const express = require("express");
const router = express.Router();

const userController = require("./userController");

const { authenticateTokenFromRequest } = require("../../middleware/auth");
const { matchUserToApartment } = require("../../middleware/validate");
const { isApartmentOwner } = require("../../middleware/rolesCheck");

router.use(authenticateTokenFromRequest);

router.get("/apartmentId", userController.findUserApartmentId);
router.get("/findById", userController.findById);
router.get("/room8", matchUserToApartment, userController.getRoommatesData);
router.put("/password", userController.changePassword);
router.put(
  "/changeRole",
  matchUserToApartment,
  isApartmentOwner,
  userController.changeRole
);
router.put("/ChangeProfileImg", userController.ChangeProfileImg);
router.delete("/delete", userController.delete);

module.exports = router;
