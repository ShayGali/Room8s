const expensesService = require("../service/expensesService");

const dateValidate = require("../utilities/dateValidate");

exports.addExpenses = async (req, res, next) => {
  const { apartmentId, userId } = req.tokenData;

  const { title, expensesType, paymentDate, amount, uploadDate, note } =
    req.body;

  if (title === undefined) {
    return res.status(400).send({
      msg: `you need to send: 'title' field`,
    });
  }
  insertedID = await expensesService.addExpenses(
    apartmentId,
    userId,
    title,
    expensesType,
    paymentDate,
    amount,
    uploadDate ? uploadDate : new Date(),
    note
  );

  res.status(201).send({ msg: "success", data: { insertedID } });
  try {
  } catch (error) {
    next(error);
  }
};

exports.getAll = async (req, res, next) => {
  const { apartmentId } = req.tokenData;
  try {
    const expenses = await expensesService.findAllOfApartment(apartmentId);
    return res.status(200).send({ msg: "success", data: expenses });
  } catch (error) {
    next(error);
  }
};

exports.findById = async (req, res, next) => {
  const { apartmentId } = req.tokenData;
  const { taskId } = req.params;
  try {
    const expense = await expensesService.findById(taskId);
    if (expense === undefined) {
      return res.status(404).send({ success: false, msg: "expense not found" });
    }
    if (expense.apartment_ID !== apartmentId) {
      return res.status(403).send({
        msg: "you cant get expense that not belong to your apartment",
        success: false,
      });
    }

    return res
      .status(200)
      .send({ success: true, msg: "success", data: expense });
  } catch (error) {
    next(error);
  }
};

exports.update = async (req, res, next) => {
  const { apartmentId } = req.tokenData;
  const { taskId } = req.params;
  const { title, expensesType, paymentDate, amount, note } = req.body;

  try {
    const expense = await expensesService.findById(taskId);

    if (expense === undefined) {
      return res.status(404).send({ success: false, msg: "expense not found" });
    }

    if (expense.apartment_ID !== apartmentId) {
      return res.status(403).send({
        success: false,
        msg: "you cant update expense that not belong to your apartment",
      });
    }

    if (
      paymentDate !== undefined &&
      !dateValidate.isInDateFormat(paymentDate)
    ) {
      return res.status(400).send({
        success: false,
        msg: "paymentDate need to be is the format of YYYY-MM-DD",
      });
    }

    expense.title = title || expense.title;
    expense.expense_type = expensesType || expense.expense_type;
    expense.payment_date = paymentDate || expense.payment_date;
    expense.amount = amount || expense.amount;
    expense.note = note || expense.note;

    const result = await expensesService.update(expense);
    return res
      .status(200)
      .send({ msg: "success", success: true, data: result });
  } catch (error) {
    next(error);
  }
};
