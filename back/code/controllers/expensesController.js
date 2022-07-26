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
  const { apartmentId, userId } = req.tokenData;

  try {
    const expenses = expensesService.findAllOfApartment(apartmentId);
    return res.status(200).send({ msg: "success", data: expenses });
  } catch (error) {
    next(error);
  }
};
