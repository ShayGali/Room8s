const paymentService = require("../service/paymentService");
const userService = require("../service/userService");

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

exports.delete = async (req, res, next) => {
  const { apartmentId } = req.tokenData;
  const { paymentId } = req.params;

  if (paymentId === undefined) {
    return res
      .status(404)
      .send({ success: false, msg: "send paymentId in params" });
  }

  try {
    const payment = await paymentService.findById(paymentId);

    if (payment === undefined) {
      return res.status(404).send({
        success: false,
        msg: `payment with the id ${paymentId} not found`,
      });
    }

    if (
      (await userService.findUserApartmentId(payment.user_ID)) !== apartmentId
    ) {
      return res.status(403).send({
        success: false,
        msg: "you cant delete this payment because its not belong to your apartment",
      });
    }

    await paymentService.delete(paymentId);
    return res.status(200).send({ success: true, msg: "success" });
  } catch (error) {
    next(error);
  }
};

exports.getUserMonthlyPayments = async (req, res, next) => {
  const { userId: senderId } = req.tokenData;
  let { userId, onlySum } = req.query;

  try {
    if (!userId) userId = senderId;
    else {
      const { apartmentId } = req.tokenData;
      if ((await userService.findUserApartmentId(userId)) !== apartmentId) {
        return res.status(403).send({
          success: false,
          msg: "you cant delete this payment because its not belong to your apartment",
        });
      }
    }
    let payments;
    if (onlySum === "true") {
      payments = await paymentService.getUserSumOFMonthlyPayments(userId);
    } else {
      payments = await paymentService.getUserMonthlyPayments(userId);
    }
    res.status(200).send({ success: true, msg: "success", data: payments });
  } catch (error) {
    next(error);
  }
};
