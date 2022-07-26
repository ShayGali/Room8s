const expensesService = require("../service/expensesService");
const userService = require("../service/userService");

exports.addExpenses = async (req, res, next) => {
  const { apartmentId, userId } = req.tokenData;

  if (apartmentId === undefined)
    return res.status(403).send({ msg: "use not in apartment" });

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
