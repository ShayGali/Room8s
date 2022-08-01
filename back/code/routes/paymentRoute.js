const express = require("express");
const router = express.Router();
const paymentController = require("../controllers/paymentController");

const { authenticateTokenFromRequest } = require("../middleware/auth");
const { matchUserToApartment } = require("../middleware/validate");

router.use(authenticateTokenFromRequest);
router.use(matchUserToApartment);

module.exports = router;