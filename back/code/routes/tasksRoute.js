const express = require("express");
const router = express.Router();
const tasksController = require("../controllers/tasksController");

const { authenticateTokenFromRequest } = require("../middleware/auth");
const { matchUserToApartment } = require("../middleware/validate");

router.get(
  "/all",
  authenticateTokenFromRequest,
  matchUserToApartment,
  tasksController.findAllTasksOfApartment
);

router
  .route("/userTasks")
  .get(authenticateTokenFromRequest, tasksController.findUserTasks);

router.post(
  "/add",
  authenticateTokenFromRequest,
  matchUserToApartment,
  tasksController.addTask
);

router
  .route("/associate")
  .post(
    authenticateTokenFromRequest,
    matchUserToApartment,
    tasksController.associateTaskToUser
  )
  .delete(
    authenticateTokenFromRequest,
    matchUserToApartment,
    tasksController.removeAssociateFromUser
  );

// need to be last
router
  .route("/:taskId")
  .get(
    authenticateTokenFromRequest,
    matchUserToApartment,
    tasksController.findById
  )
  .put(authenticateTokenFromRequest)
  .delete(authenticateTokenFromRequest, tasksController.deleteById);
module.exports = router;
