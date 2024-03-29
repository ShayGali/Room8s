const db = require("../../config/db");
const tasksTable = "tasks";
const tasksTypeTable = "task_type";
const tasksPerUserTable = "tasks_per_user";

const { formatDateTime } = require("../../utilities/dateValidate");

/**
 *
 * @param {number} apartmentID
 * @returns {Promise<Array<Object>>}
 */
exports.findAllTasksOfApartment = async (apartmentID) => {
  if (apartmentID === undefined) return;

  const query = `SELECT ${tasksTable}.*, ${tasksTypeTable}.task_type, ${tasksTypeTable}.icon_path
  FROM ${tasksTable}
  INNER JOIN ${tasksTypeTable}
  ON ${tasksTable}.task_type = ${tasksTypeTable}.ID
  WHERE apartment_ID = ?;`;

  const [tasks, _] = await db.execute(query, [apartmentID]);

  return tasks.map((task) => {
    task.create_time = formatDateTime(task.create_time);
    task.expiration_date = formatDateTime(task.expiration_date);
    return task;
  });
};

exports.addTask = async (
  apartmentID,
  creatorID,
  taskType = null,
  expirationDate = null,
  title = null,
  note = null
) => {
  if (isNaN(taskType) && taskType !== null) {
    // if the user send the task type na eand not the ID, we convert it to the ID
    let [res, _] = await db.execute(
      `SELECT ID FROM ${tasksTypeTable} WHERE task_type = ?`,
      [taskType]
    );

    if (res[0] === undefined) return; // if it's not in the tasksTypeTable we end the function

    taskType = res[0].ID;
  }

  let query = `
  INSERT INTO ${tasksTable}(
    apartment_ID, creator_ID, task_type, create_time, expiration_date, title, note
  ) VALUES(?,?,?,?,?,?,?);
  `;
  let result = await db.execute(query, [
    apartmentID,
    creatorID,
    taskType,
    new Date().toISOString().slice(0, 19).replace("T", " "), // create_time
    expirationDate,
    title,
    note,
  ]);

  return result[0].insertId;
};

exports.findById = async (taskId) => {
  const query = `
  SELECT * FROM ${tasksTable}
  WHERE ID = ?;
  `;
  const [result, _] = await db.execute(query, [taskId]);
  if (result[0] !== undefined) { // format the dates
    result[0].create_time = formatDateTime(result[0].create_time);
    result[0].expiration_date = formatDateTime(result[0].expiration_date);
    return result[0];
  }
  return undefined;
};

/**
 * creare the relationship between the user and the task
 * @param {number} taskId 
 * @param {number} userId 
 * @returns {Promise<number>} the id of the relationship
 */
exports.associateTaskToUser = async (taskId, userId) => {
  const query = `
  INSERT INTO ${tasksPerUserTable}(
    task_ID, user_ID
  )VALUES(?,?);
  `;
  let result = await db.execute(query, [taskId, userId]);
  return result[0].insertId;
};

/**
 * remove the relationship between the user and the task
 * @param {number} taskId 
 * @param {number} userId 
 */
exports.removeAssociateFromUser = async (taskId, userId) => {
  const query = `
  DELETE FROM ${tasksPerUserTable}
  WHERE task_ID = ? AND user_ID = ?;
  `;
  await db.execute(query, [taskId, userId]);
};

exports.findUserTasksIds = async (userId) => {
  const query = `
    select task_ID from ${tasksPerUserTable}
    where user_ID = ?;
  `;

  const [result, _] = await db.execute(query, [userId]);

  return result.map((task) => task.task_ID);
};

exports.findTaskExecutors = async (taskId) => {
  const query = `SELECT user_ID FROM ${tasksPerUserTable}
  WHERE task_ID = ?;
`;
  const [result, _] = await db.execute(query, [taskId]);

  return result.map((task) => task.user_ID);
};

exports.deleteById = async (taskId) => {
  const query = `
    DELETE FROM ${tasksTable}
    WHERE ID = ?;
  `;
  await db.execute(query, [taskId]);
};

/**
 * @param {number | string} taskType
 * @param {Date | string} expirationDate
 * @param {string} title
 * @param {string | null} note
 */
exports.updateTask = async (
  taskId,
  taskType = null,
  expirationDate = null,
  title = null,
  note = null
) => {
  if (taskType != undefined && isNaN(taskType)) {
    let [res, _] = await db.execute(
      `SELECT ID FROM ${tasksTypeTable} WHERE task_type = ?`,
      [taskType]
    );
    taskType = res[0].ID;
  }
  const query = `UPDATE ${tasksTable}
    SET task_type = ?, expiration_date = ?, title = ?,  note = ?
    WHERE ID = ?
    `;
  const result = await db.execute(query, [
    taskType,
    expirationDate,
    title,
    note,
    taskId,
  ]);
  return result;
};

/**
 * return the taks types that save in the DB
 * @returns {Promise<{}>}
 */
exports.getTypes = async () => {
  const [result, _] = await db.execute(`SELECT * FROM ${tasksTypeTable}`);
  return result;
};
