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

router.get("/monthlyPayments", paymentController.getUserMonthlyPayments);

router.route("/:paymentId").delete(paymentController.delete);

module.exports = router;
