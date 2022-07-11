const express = require("express");
const router = express.Router();

const userController = require("../controllers/userController");

const { authenticateTokenFromRequest } = require("../middleware/auth");

router.get(
  "/apartmentId",
  authenticateTokenFromRequest,
  userController.findUserApartment
);
router.get("/findById", authenticateTokenFromRequest, userController.findById);
router
  .route("/findByEmail")
  .get(authenticateTokenFromRequest, userController.findByEmail); //TODO: check roll admin

module.exports = router;
