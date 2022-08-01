const paymentService = require("../service/paymentService");

const { isInDateTimeFormat } = require("../utilities/dateValidate");

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
