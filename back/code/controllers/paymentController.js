const paymentService = require("../service/paymentService");

const {
  isInDateFormat,
  isInDateTimeFormat,
} = require("../utilities/dateValidate");

exports.findAllPaymentsByFieldName = async (req, res, next) => {
  const { field, id } = req.body;
  if (field === undefined || id === undefined) {
    return res.status(400).send({ success: false, msg: "send field and id" });
  }

  const validFields = ["expense_ID", "user_ID"];

  if (!validFields.includes(field)) {
    return res.status(400).send({
      success: false,
      msg: `field need to be one of {${validFields}}`,
    });
  }

  try {
    const payments = await paymentService.findAllPaymentsByFieldName(field, id);
    return res
      .status(200)
      .send({ success: true, msg: "success", data: payments });
  } catch (error) {
    next(error);
  }
};

exports.add = async (req, res, next) => {
  const { userId } = req.tokenData;
  const { expenseId, amount, payDate } = req.body;

  if ((expenseId === undefined, amount === undefined, payDate === undefined)) {
    return res
      .status(400)
      .send({ success: false, msg: "send {expenseId, amount, payDate}" });
  }

  if (!(isInDateFormat(payDate) || isInDateTimeFormat(payDate))) {
    return res.status(400).send({
      success: false,
      msg: "payDate need to be in YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS  format",
    });
  }

  try {
    const insertId = await paymentService.add(
      expenseId,
      userId,
      amount,
      payDate
    );

    return res.status(201).send({ success: true, msg: "success", insertId });
  } catch (error) {
    next(error);
  }
};
