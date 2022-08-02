const db = require("../config/db");
const tasksTable = "tasks";
const tasksTypeTable = "task_type";
const tasksPerUserTable = "tasks_per_user";

exports.findAllTasksOfApartment = async (apartmentID) => {
  if (apartmentID === undefined) return;

  const query = `
  SELECT ${tasksTable}.*, ${tasksTypeTable}.task_type, ${tasksTypeTable}.icon_path
  FROM ${tasksTable}
  INNER JOIN ${tasksTypeTable}
  ON ${tasksTable}.task_type = ${tasksTypeTable}.ID
  WHERE apartment_ID = ?;`;
  const [tasks, _] = await db.execute(query, [apartmentID]);
  return tasks;
};

exports.addTask = async (
  apartmentID,
  creatorID,
  taskType = null,
  expirationDate = null,
  title = null,
  note = null
) => {
  let query = `
  INSERT INTO ${tasksTable}(
    apartment_ID, creator_ID, task_type,create_time, expiration_date, title, note
  ) VALUES(?,?,?,?,?,?,?);
  `;
  let result = await db.execute(query, [
    apartmentID,
    creatorID,
    taskType,
    new Date().toISOString().slice(0, 19).replace("T", " "),
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
  return result[0];
};

exports.associateTaskToUser = async (taskId, userId) => {
  const query = `
  INSERT INTO ${tasksPerUserTable}(
    task_ID, user_ID
  )VALUES(?,?);
  `;
  let result = await db.execute(query, [taskId, userId]);
  return result[0].insertId;
};

exports.removeAssociateFromUser = async (taskId, userId) => {
  const query = `
  DELETE FROM ${tasksPerUserTable}
  WHERE task_ID = ? AND user_ID = ?;
  `;
  await db.execute(query, [taskId, userId]);
};

exports.findUserTasks = async (userId) => {
  const query = `
    select * from ${tasksPerUserTable}
    where user_ID = ?;
  `;

  const [result, _] = await db.execute(query, [userId]);

  return result;
};

exports.deleteById = async (taskId) => {
  const query = `
    DELETE FROM ${tasksTable}
    WHERE ID = ?;
  `;
  await db.execute(query, [taskId]);
};

/**
 *
 * @param {number | string} taskType
 * @param {Date | string} expirationDate
 * @param {string} title
 * @param {string | null} note
 */
exports.updateTask = async (taskId, taskType, expirationDate, title, note) => {
  if (isNaN(taskType)) {
    let [res, _] = await db.execute(
      `SELECT ID FROM ${tasksTypeTable} WHERE task_type = ?`,
      [taskType]
    );
    taskType = res[0].ID;
  }
  const query = `
    UPDATE ${tasksTable}
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
