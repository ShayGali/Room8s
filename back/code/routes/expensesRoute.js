const express = require("express");
const router = express.Router();

const expensesController = require("../controllers/expensesController");

const { authenticateTokenFromRequest } = require("../middleware/auth");
const { matchUserToApartment } = require("../middleware/validate");

router.get(
  "/",
  authenticateTokenFromRequest,
  matchUserToApartment,
  expensesController.getAll
);

router.post(
  "/add",
  authenticateTokenFromRequest,
  matchUserToApartment,
  expensesController.addExpenses
);

module.exports = router;
