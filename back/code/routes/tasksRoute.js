const express = require("express");
const router = express.Router();
const tasksController = require("../controllers/tasksController");

const { authenticateTokenFromRequest } = require("../middleware/auth");

router.get(
  "/all",
  authenticateTokenFromRequest,
  tasksController.findAllTasksOfApartment
);

router
  .route("/userTasks")
  .get(authenticateTokenFromRequest, tasksController.findUserTasks);

router.post("/add", authenticateTokenFromRequest, tasksController.addTask);

router.put(
  "/associate",
  authenticateTokenFromRequest,
  tasksController.associateTaskToUser
);

// need to be last
router
  .route("/:taskId")
  .get(authenticateTokenFromRequest, tasksController.findById)
  .put(authenticateTokenFromRequest)
  .delete(authenticateTokenFromRequest, tasksController.deleteById);
module.exports = router;
