const express = require("express");

const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

//Routes
app.use("/auth", require("./routes/authRoutes"));
app.use("/users", require("./routes/userRoutes"));
app.use("/apartments", require("./routes/apartmentRoute"));
app.use("/tasks", require("./routes/tasksRoute"));
app.use("/expenses", require("./routes/expensesRoute"));

// Global Error handler
app.use(errorHandler);
app.all("/isAlive", (req, res) => {
  res.status(200).send({ success: true, msg: "Server Alive" });
});

// Global 404 Error handler
app.all("/*", (req, res) => {
  res
    .status(404)
    .json({ msg: `${req.method} on ${req.originalUrl} not found` });
});

module.exports = app;

/**
 * global Error handler
 * @param {Error} err
 * @param {Request} req
 * @param {Response} res
 * @param {NextFunction} next
 */
function errorHandler(err, req, res, next) {
  console.log(err.stack);
  console.log(err.name);

  console.log(err.code);
  console.log(err.msg);

  res
    .status(err.code || 500)
    .json({ success: false, msg: "Something went wrong", err: err.msg });
}
