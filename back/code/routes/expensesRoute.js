const express = require("express");
const router = express.Router();

const expensesController = require("../controllers/expensesController");

const { authenticateTokenFromRequest } = require("../middleware/auth");

router.post(
  "/add",
  authenticateTokenFromRequest,
  expensesController.addExpenses
);

module.exports = router;
