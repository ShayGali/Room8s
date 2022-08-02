const express = require("express");
const router = express.Router();
const tasksController = require("../controllers/tasksController");

const { authenticateTokenFromRequest } = require("../middleware/auth");
const { matchUserToApartment } = require("../middleware/validate");

router.use(authenticateTokenFromRequest);
router.use(matchUserToApartment);

router.get("/all", tasksController.findAllTasksOfApartment);

router.route("/userTasks").get(tasksController.findUserTasks);

router.post("/add", tasksController.addTask);

router
  .route("/associate")
  .post(tasksController.associateTaskToUser)
  .delete(tasksController.removeAssociateFromUser);

// need to be last
router
  .route("/:taskId")
  .get(tasksController.findById)
  .put(tasksController.updateTask)
  .delete(tasksController.deleteById);
module.exports = router;
