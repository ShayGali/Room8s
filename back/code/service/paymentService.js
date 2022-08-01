const db = require("../config/db");
const paymentTable = "payments";

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
