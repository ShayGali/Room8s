const db = require("../config/db");
const expensesTable = "expenses";
const expensesTypeTable = "expense_type";

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
  const query = `
  INSERT INTO ${expensesTable}(
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

exports.findAllOfApartment = async (apartmentId) => {
  const query = `
  SELECT ${expensesTable}.*, ${expensesTypeTable}.expense_type
  FROM ${expensesTable}
  INNER JOIN ${expensesTypeTable}
  ON ${expensesTable}.expense_type = ${expensesTypeTable}.ID
  WHERE apartment_ID = ?;
  ;
  `;
  const [expenses, _] = await db.execute(query, [apartmentID]);
  return expenses;
};