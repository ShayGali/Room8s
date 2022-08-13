const express = require("express");
const router = express.Router();

const expensesController = require("./expensesController");

const { authenticateTokenFromRequest } = require("../../middleware/auth");
const { matchUserToApartment } = require("../../middleware/validate");

// middleware
router.use(authenticateTokenFromRequest);
router.use(matchUserToApartment);

// requests
router.get("/", expensesController.getAll);

router.post("/add", expensesController.addExpenses);

router
  .route("/:expenseId")
  .get(expensesController.findById)
  .put(expensesController.update)
  .delete(expensesController.delete);

module.exports = router;
