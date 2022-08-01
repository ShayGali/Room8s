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
