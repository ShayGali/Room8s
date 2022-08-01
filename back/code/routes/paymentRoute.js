const express = require("express");
const router = express.Router();
const paymentController = require("../controllers/paymentController");

const { authenticateTokenFromRequest } = require("../middleware/auth");
const { matchUserToApartment } = require("../middleware/validate");

router.use(authenticateTokenFromRequest);
router.use(matchUserToApartment);

router
  .route("/")
  .get(paymentController.findAllPaymentsByFieldName) // TODO: check user
  .post(paymentController.add);

module.exports = router;
