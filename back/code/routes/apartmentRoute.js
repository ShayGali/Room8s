const express = require("express");
const router = express.Router();

const apartmentController = require("../controllers/apartmentController");

const { authenticateTokenFromRequest } = require("../middleware/auth");

router.use(authenticateTokenFromRequest);

router.post("/create", apartmentController.createApartment);

router.route("/data").get(apartmentController.getApartmentData);

//TODO: add middleware to check the user role to be an apartment_owner
router.post("/addUser", apartmentController.addUserToApartment);

router.delete(
  "/removeUserFromApartment",
  apartmentController.removeUserFromApartment
);

module.exports = router;
