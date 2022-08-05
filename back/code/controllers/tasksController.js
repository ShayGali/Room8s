const tasksService = require("../service/tasksService");
const userService = require("../service/userService");

const {
  isInDateFormat,
  isInDateTimeFormat,
} = require("../utilities/dateValidate");

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

  if (
    expirationDate &&
    !(isInDateFormat(expirationDate) || isInDateTimeFormat(expirationDate))
  ) {
    return res.status(400).send({
      success: false,
      msg: "expirationDate need to be in YYYY-MM-DD or YYYY-MM-DD HH:MM:SS format",
    });
  }

  try {
    insertedID = await tasksService.addTask(
      apartmentId,
      userId,
      taskType,
      expirationDate,
      title,
      note
    );
    res
      .status(201)
      .send({ success: true, msg: "success", data: { insertedID } });
  } catch (error) {
    next(error);
  }
};

exports.findById = async (req, res, next) => {
  const { apartmentId } = req.tokenData;
  const { taskId } = req.params;
  if (!taskId)
    return res.status(400).send({ success: false, msg: "send taskId" });
  try {
    const task = await tasksService.findById(taskId);
    if (!task)
      return res.status(404).send({ success: false, msg: "task not found" });

    if (apartmentId !== task.apartment_ID) {
      return res
        .status(403)
        .send({ success: false, msg: "You cant get this task" });
    }

    return res.send({ success: true, msg: "success", data: task });
  } catch (error) {
    next(error);
  }
};

exports.associateTaskToUser = async (req, res, next) => {
  const { apartmentId: senderApartmentId } = req.tokenData;
  const { taskId, userId } = req.body;

  if (taskId === undefined)
    return res.status(400).send({ success: false, msg: "send taskId " });

  try {
    const task = await tasksService.findById(taskId);

    if (task == undefined)
      return res.status(404).send({ success: false, msg: "task not found" });

    if (senderApartmentId !== task.apartment_ID)
      return res.status(403).send({
        success: false,
        msg: "Cannot associate a task that does not belong to your apartment",
      });

    const { apartmentId: userToAssociateApartmentId } =
      await await userService.findUserApartmentId(userId);

    if (senderApartmentId !== userToAssociateApartmentId)
      return res.status(403)({
        success: false,
        msg: "Cannot associate a task to a user who is not with you in the same apartment",
      });

    // check if user already associated to this task
    if (
      (await tasksService.findUserTasks(userId)).find(
        (task) => task.task_ID === taskId
      ) !== undefined
    ) {
      return res.status(200).send({
        success: true,
        msg: "user is already associated with this task",
      });
    }

    const result = await tasksService.associateTaskToUser(taskId, userId);
    if (!result)
      return res
        .status(500)
        .send({ success: false, msg: "associate task to user failed" });
    return res.status(200).send({ success: true, msg: "success" });
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
      return res.status(404).send({ success: false, msg: "task not found" });

    if (senderApartmentId !== task.apartment_ID)
      return res.status(403).send({
        success: false,
        msg: "Cannot associate a task that does not belong to your apartment",
      });

    const { apartmentId: userToAssociateApartmentId } =
      await await userService.findUserApartmentId(userId);

    if (senderApartmentId !== userToAssociateApartmentId)
      return res.status(403)({
        success: false,
        msg: "Cannot associate a task to a user who is not with you in the same apartment",
      });

    await tasksService.removeAssociateFromUser(taskId, userId);
    return res.status(200).send({ success: true, msg: "success" });
  } catch (error) {
    next(error);
  }
};

exports.findUserTasks = async (req, res, next) => {
  const { userId } = req.tokenData;
  try {
    const result = await tasksService.findUserTasks(userId);
    res.send({ success: true, msg: "success", data: result });
  } catch (error) {
    next(error);
  }
};

exports.deleteById = async (req, res, next) => {
  const { userId, apartmentId } = req.tokenData;
  const { taskId } = req.params;
  if (!taskId)
    return res.status(400).send({ success: false, msg: "send taskId" });

  try {
    const task = await tasksService.findById(taskId);
    if (!task)
      return res.status(404).send({ success: false, msg: "tasks not found" });

    if (apartmentId !== task.apartment_ID)
      return res.status(403).send({
        success: false,
        msg: "Cannot delete a task that does not belong to your apartment",
      });

    await tasksService.deleteById(taskId);
    return res
      .status(200)
      .send({
        success: true,
        msg: `task with the id ${taskId} has been delete successfully`,
      });
  } catch (error) {
    next(error);
  }
};

// TODO: change task type
exports.updateTask = async (req, res, next) => {
  const { userId, apartmentId } = req.tokenData;
  const { taskId } = req.params;

  const { taskType, expirationDate, title, note } = req.body;

  if (!taskId) {
    return res
      .status(400)
      .send({ success: false, msg: "send taskId in params" });
  }

  try {
    const task = await tasksService.findById(taskId);
    if (task === undefined) {
      return res.status(404).send({ success: false, msg: "task not found" });
    }
    if (task.apartment_ID !== apartmentId) {
      return res.status(403).send({
        success: false,
        msg: "you cant modify this task because its not belong to your apartment",
      });
    }
    await tasksService.updateTask(
      taskId,
      taskType || task.task_type,
      isInDateFormat(expirationDate) || isInDateTimeFormat(expirationDate)
        ? expirationDate
        : task.expiration_date,
      title || task.title,
      note === undefined ? task.note : note
    );

    return res.status(200).send({ success: true, msg: "success" });
  } catch (error) {
    next(error);
  }
};

exports.getTypes = async (req, res, next) => {
  try {
    const result = await tasksService.getTypes();
    return res
      .status(200)
      .send({ success: true, msg: "success", data: result });
  } catch (error) {
    next(error);
  }
};
