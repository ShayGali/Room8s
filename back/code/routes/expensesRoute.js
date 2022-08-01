const express = require("express");
const router = express.Router();

const expensesController = require("../controllers/expensesController");

const { authenticateTokenFromRequest } = require("../middleware/auth");
const { matchUserToApartment } = require("../middleware/validate");

// middleware
router.use(authenticateTokenFromRequest);
router.use(matchUserToApartment);

// requests
router.get("/", expensesController.getAll);

router.post("/add", expensesController.addExpenses);

module.exports = router;
