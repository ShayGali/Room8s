const tasksService = require("../service/tasksService");
const userService = require("../service/userService");

exports.findAllTasksOfApartment = async (req, res, next) => {
  const { apartmentId } = req.tokenData;

  try {
    const result = await tasksService.findAllTasksOfApartment(apartmentId);
    res.status(200).send({ msg: "success", data: result });
  } catch (error) {
    next(error);
  }
};

exports.addTask = async (req, res, next) => {
  const { apartmentId, userId } = req.tokenData;
  const { taskType, expirationDate, title, note } = req.body;

  try {
    insertedID = await tasksService.addTask(
      apartmentId,
      userId,
      taskType,
      expirationDate,
      title,
      note
    );
    res.status(201).send({ msg: "success", data: { insertedID } });
  } catch (error) {
    next(error);
  }
};

exports.findById = async (req, res, next) => {
  const { apartmentId } = req.tokenData;
  const { taskId } = req.params;
  if (!taskId) return res.status(400).send({ msg: "send taskId" });
  try {
    const task = await tasksService.findById(taskId);
    if (!task) return res.status(404).send({ msg: "task not found" });

    if (apartmentId !== task.apartment_ID) {
      return res.status(403).send({ msg: "You cant get this task" });
    }

    return res.send({ msg: "success", data: task });
  } catch (error) {
    next(error);
  }
};

exports.associateTaskToUser = async (req, res, next) => {
  const { apartmentId: senderApartmentId } = req.tokenData;
  const { taskId, userId } = req.body;

  if (taskId === undefined)
    return res.status(400).send({ msg: "send taskId " });

  try {
    const task = await tasksService.findById(taskId);

    if (task == undefined)
      return res.status(404).send({ msg: "task not found" });

    if (senderApartmentId !== task.ID)
      return res.status(403).send({
        msg: "Cannot associate a task that does not belong to your apartment",
      });

    const { apartmentId: userToAssociateApartmentId } =
      await await userService.findUserApartmentId(userId);

    if (senderApartmentId !== userToAssociateApartmentId)
      return res.status(403)({
        msg: "Cannot associate a task to a user who is not with you in the same apartment",
      });

    if (
      (await tasksService.findUserTasks(userId)).find(
        (task) => task.task_ID === taskId
      ) !== undefined
    ) {
      return res
        .status(200)
        .send({ msg: "user is already associated with this task" });
    }

    const result = await tasksService.associateTaskToUser(taskId, userId);
    if (!result)
      return res.status(500).send({ msg: "associate task to user failed" });
    return res.status(200).send({ msg: "success" });
  } catch (error) {
    next(error);
  }
};

exports.removeAssociateFromUser = async (req, res, next) => {
  const { apartmentId: senderApartmentId } = req.tokenData;
  const { taskId, userId } = req.body;

  if (taskId === undefined) return res.status(400).send({ msg: "send taskId" });

  try {
    const task = await tasksService.findById(taskId);

    if (task == undefined)
      return res.status(404).send({ msg: "task not found" });

    if (senderApartmentId !== task.ID)
      return res.status(403).send({
        msg: "Cannot associate a task that does not belong to your apartment",
      });

    const { apartmentId: userToAssociateApartmentId } =
      await await userService.findUserApartmentId(userId);

    if (senderApartmentId !== userToAssociateApartmentId)
      return res.status(403)({
        msg: "Cannot associate a task to a user who is not with you in the same apartment",
      });

    await tasksService.removeAssociateFromUser(taskId, userId);
    return res.status(200).send({ msg: "success" });
  } catch (error) {
    next(error);
  }
};

exports.findUserTasks = async (req, res, next) => {
  const { userId } = req.tokenData;
  if (!userId) return res.status(403).send({ msg: "user id not valid" });

  try {
    const result = await tasksService.findUserTasks(userId);
    res.send({ msg: "success", data: result });
  } catch (error) {
    next(error);
  }
};

exports.deleteById = async (req, res, next) => {
  const { userId } = req.tokenData;
  const { taskId } = req.params;
  if (!userId) return res.status(403).send({ msg: "user id not valid" });
  if (!taskId) return res.status(400).send({ msg: "send taskId" });

  try {
    const task = await tasksService.findById(taskId);
    if (!task) return res.status(404).send({ msg: "tasks not found" });
    const { apartmentId } = await userService.findUserApartmentId(userId);

    if (apartmentId !== task.apartment_ID)
      return res.status(403).send({
        msg: "Cannot delete a task that does not belong to your apartment",
      });

    await tasksService.deleteById(taskId);
    return res.status(200).send({ msg: "success" });
  } catch (error) {
    next(error);
  }
};
