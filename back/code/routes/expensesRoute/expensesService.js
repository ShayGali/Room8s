const db = require("../../config/db");
const expensesTable = "expenses";
const expensesTypeTable = "expense_type";

const { formatDateTime } = require("../../utilities/dateValidate");

/**
 *
 * @param {number} apartmentId
 * @param {number} UserThatUploadId
 * @param {string} title
 * @param {string | number | null} expensesType
 * @param {string | null} paymentDate
 * @param {number | null} amount
 * @param {string | null} uploadDate
 * @param {string | null} note
 * @returns
 */
exports.addExpenses = async (
  apartmentId,
  UserThatUploadId,
  title,
  expensesType = null,
  paymentDate = null,
  amount = null,
  uploadDate = null,
  note = null
) => {
  if (isNaN(expensesType)) {
    // if the expensesType is not number , and its the name of the type, I convert it to the id by the type name
    let [res, _] = await db.execute(
      `SELECT ID FROM ${expensesTypeTable} WHERE expense_type = ?`,
      [expensesType]
    );
    if (res[0] === undefined) return;
    expensesType = res[0]?.ID;
  }
  const query = `INSERT INTO ${expensesTable}(
    apartment_ID, UserThatUploadID, title, expense_type, payment_date, amount, upload_date, note
  )VALUES( ?, ?, ?, ?, ?, ?, ?, ?)
  `;
  let result = await db.execute(query, [
    apartmentId,
    UserThatUploadId,
    title,
    expensesType,
    paymentDate,
    amount,
    uploadDate,
    note,
  ]);

  return result[0].insertId;
};

/**
 * find all the expenses of the apartemnt
 * @param {number} apartmentId
 */
exports.findAllOfApartment = async (apartmentId) => {
  const query = `
  SELECT ${expensesTable}.*, ${expensesTypeTable}.expense_type
  FROM ${expensesTable}
  INNER JOIN ${expensesTypeTable}
  ON ${expensesTable}.expense_type = ${expensesTypeTable}.ID
  WHERE apartment_ID = ?;
  ;
  `;
  const [expenses, _] = await db.execute(query, [apartmentId]);
  return expenses.map((expense) => {
    //format the dates
    expense.upload_date = formatDateTime(expense.upload_date);
    expense.payment_date = formatDateTime(expense.payment_date);
    return expense;
  });
};

/**
 * find the expense by his ID
 * @param {number} expenseId
 * @returns {Promise}
 */
exports.findById = async (expenseId) => {
  const query = `
  SELECT ${expensesTable}.*, ${expensesTypeTable}.expense_type
  FROM ${expensesTable}
  INNER JOIN ${expensesTypeTable}
  ON ${expensesTable}.expense_type = ${expensesTypeTable}.ID
  WHERE ${expensesTable}.ID = ?;
  ;
  `;

  const [expense, _] = await db.execute(query, [expenseId]);

  if (expense[0] !== undefined) {
    // if the expense was found, we format the dates
    expense[0].upload_date = formatDateTime(expense[0].upload_date);
    expense[0].payment_date = formatDateTime(expense[0].payment_date);
  }

  return expense[0];
};

/**
 * get an expense and update it in the DB
 * @param {{}} expense //TODO
 * @returns {Promise}
 */

exports.update = async (expense) => {
  if (isNaN(expense.expense_type)) {
    // if the expensesType is not number , and its the name of the type, I convert it to the id by the type name
    let [res, _] = await db.execute(
      `SELECT ID FROM ${expensesTypeTable} WHERE expense_type = ?`,
      [expense.expense_type]
    );
    if (res[0] === undefined) return;
    expense.expense_type = res[0].ID;
  }

  const query = `
  UPDATE ${expensesTable}
  SET title = ?, expense_type = ?, payment_date = ?, amount = ?, note = ?
  WHERE ${expensesTable}.ID = ?
  `;

  const result = await db.execute(query, [
    expense.title,
    expense.expense_type,
    expense.payment_date || null,
    expense.amount,
    expense.note,
    expense.ID,
  ]);
  return result;
};

/**
 * delete the expense by the ID
 * @param {number} expenseId
 * @returns {Promise}
 */
exports.delete = async (expenseId) => {
  const query = `DELETE FROM ${expensesTable} WHERE ID = ?`;
  const result = await db.execute(query, [expenseId]);
  return result;
};
