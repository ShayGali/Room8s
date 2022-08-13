const db = require("../../config/db");
const paymentTable = "payments";

exports.findById = async (paymentId) => {
  const query = `
    SELECT * FROM ${paymentTable}
    WHERE ID = ? ;
    `;

  const [payments, _] = await db.execute(query, [paymentId]);
  return payments[0];
};

exports.findAllPaymentsByFieldName = async (filed, id) => {
  const query = `
    SELECT * FROM ${paymentTable}
    WHERE ${filed} = ? ;
    `;

  const [payments, _] = await db.execute(query, [id]);
  return payments;
};

exports.add = async (expenseId, userId, amount, payDate) => {
  const query = `
  INSERT INTO ${paymentTable}(
    expense_ID, user_ID, amount, pay_date
  ) VALUES(?,?,?,?);
  `;
  const result = await db.execute(query, [expenseId, userId, amount, payDate]);

  return result[0].insertId;
};

exports.delete = async (expenseId) => {
  const query = `
  DELETE FROM ${paymentTable}
  WHERE ID = ?;
  `;
  await db.execute(query, [expenseId]);
};

exports.getUserMonthlyPayments = async (userId) => {
  const now = new Date();

  const query = `
  SELECT * FROM ${paymentTable}
  WHERE 
    user_ID = ? 
    AND
    YEAR(pay_date) = ${now.getFullYear()} 
    AND
    MONTH(pay_date) = ${now.getMonth() + 1}
  `;

  const [result, _] = await db.execute(query, [userId]);
  return result;
};

exports.getUserSumOFMonthlyPayments = async (userId) => {
  const now = new Date();

  const query = `
  SELECT SUM(amount) FROM ${paymentTable}
  WHERE 
    user_ID = ? 
    AND
    YEAR(pay_date) = ${now.getFullYear()} 
    AND
    MONTH(pay_date) = ${now.getMonth() + 1}
  `;

  const [result, _] = await db.execute(query, [userId]);
  return result[0]["SUM(amount)"];
};
