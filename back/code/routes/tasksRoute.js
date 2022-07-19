const express = require("express");
const router = express.Router();
const tasksController = require("../controllers/tasksController");

const {
  authenticateTokenFromRequest,
  authenticateToken,
} = require("../middleware/auth");

router.get(
  "/all",
  authenticateTokenFromRequest,
  tasksController.findAllTasksOfApartment
);

router
  .route("/userTasks")
  .get(authenticateTokenFromRequest, tasksController.findUserTasks);

router.post("/add", authenticateTokenFromRequest, tasksController.addTask);

router
  .route("/associate")
  .post(authenticateTokenFromRequest, tasksController.associateTaskToUser)
  .delete(
    authenticateTokenFromRequest,
    tasksController.removeAssociateFromUser
  );

// need to be last
router
  .route("/:taskId")
  .get(authenticateTokenFromRequest, tasksController.findById)
  .put(authenticateTokenFromRequest)
  .delete(authenticateTokenFromRequest, tasksController.deleteById);
module.exports = router;
