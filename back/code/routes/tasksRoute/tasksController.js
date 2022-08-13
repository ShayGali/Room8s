const tasksService = require("./tasksService");
const userService = require("../../routes/userRoutes/userService");
const apartmentService = require("../../routes/apartmentRoute/apartmentService");

const {
  isInDateFormat,
  isInDateTimeFormat,
} = require("../../utilities/dateValidate");

exports.findAllTasksOfApartment = async (req, res, next) => {
  const { apartmentId } = req.tokenData;

  try {
    const result = await tasksService.findAllTasksOfApartment(apartmentId);

    for (task of result) {
      const executorsIds = await tasksService.findTaskExecutors(task.ID);
      task.executors_ids = executorsIds;
    }

    res.status(200).send({ msg: "success", data: result });
  } catch (error) {
    next(error);
  }
};

exports.addTask = async (req, res, next) => {
  const { apartmentId, userId } = req.tokenData;
  const { taskType, expirationDate, title, note, executorsIds } = req.body;

  if (
    expirationDate &&
    !(isInDateFormat(expirationDate) || isInDateTimeFormat(expirationDate))
  ) {
    return res.status(400).send({
      success: false,
      msg: "expirationDate need to be in YYYY-MM-DD or YYYY-MM-DD HH:MM:SS format",
    });
  }

  if (executorsIds !== undefined && !Array.isArray(JSON.parse(executorsIds))) {
    return res.status(400).send({
      success: false,
      msg: "executorsIds need to be an array",
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

    const room8Ids = await apartmentService.getRoom8Ids(apartmentId);
    if (executorsIds !== undefined) {
      JSON.parse(executorsIds).forEach((executorId) => {
        if (room8Ids.includes(executorId)) {
          tasksService.associateTaskToUser(insertedID, executorId);
        }
      });
    }
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

    const executorsIds = await tasksService.findTaskExecutors(taskId);
    task.executors_ids = executorsIds;

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

    const userToAssociateApartmentId = await userService.findUserApartmentId(
      userId
    );

    if (senderApartmentId !== userToAssociateApartmentId)
      return res.status(403).send({
        success: false,
        msg: "Cannot associate a task to a user who is not with you in the same apartment",
      });

    // check if user already associated to this task
    if (
      (await tasksService.findUserTasksIds(userId)).find(
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

exports.findUserTasksIds = async (req, res, next) => {
  const { userId } = req.tokenData;
  try {
    const result = await tasksService.findUserTasksIds(userId);
    res.send({ success: true, msg: "success", data: result });
  } catch (error) {
    next(error);
  }
};

exports.findTaskExecutors = async (req, res, next) => {
  const { taskId } = req.params;
  try {
    const result = await tasksService.findTaskExecutors(taskId);
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
    return res.status(200).send({
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

  let { taskType, expirationDate, title, note, executorsIds } = req.body;

  if (!taskId) {
    return res
      .status(400)
      .send({ success: false, msg: "send taskId in params" });
  }

  if (executorsIds !== undefined) {
    try {
      executorsIds = JSON.parse(executorsIds);
      if (!Array.isArray(executorsIds)) {
        return res.status(400).send({
          success: false,
          msg: "executorsIds need to be an array",
        });
      }
    } catch (e) {
      return res.status(400).send({
        success: false,
        msg: "executorsIds need to be an array",
      });
    }
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

    if (executorsIds !== undefined) {
      const room8Ids = await apartmentService.getRoom8Ids(apartmentId);
      const executors = await tasksService.findTaskExecutors(taskId);

      executors.forEach((executorId) => {
        if (!executorsIds.includes(executorId)) {
          tasksService.removeAssociateFromUser(taskId, executorId);
        }
      });

      executorsIds.forEach((executorId) => {
        if (room8Ids.includes(executorId) && !executors.includes(executorId)) {
          tasksService.associateTaskToUser(taskId, executorId);
        }
      });
    }

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
