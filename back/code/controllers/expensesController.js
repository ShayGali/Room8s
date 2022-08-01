const expensesService = require("../service/expensesService");

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
    console.log(expense);
    if (expense === undefined) {
      return res.status(404).send({ msg: "expense not found", success: false });
    }
    if (expense.apartment_ID !== apartmentId) {
      return res.status(403).send({
        msg: "you cant get expense that not belong to your apartment",
        success: false,
      });
    }

    return res
      .status(200)
      .send({ msg: "success", success: true, data: expense });
  } catch (error) {
    next(error);
  }
};

exports.update = async (req, res, next) => {
  const { apartmentId } = req.tokenData;
  const { taskId } = req.params;

  try {
  } catch (error) {
    next(error);
  }
};
