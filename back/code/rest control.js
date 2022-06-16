const express = require("express");

const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.use("/auth", require("./routes/authRoutes"));
app.use("/users", require("./routes/userRoutes"));
app.use("/apartments", require("./routes/apartmentRoute"));

// Global Error handler
app.use((err, req, res, next) => {
  console.log(err.stack);
  console.log(err.name);
  console.log(err.code);

  res.status(500).json({ msg: "Something went wrong" });
});

app.get("/", async (req, res) => {
  res.status(200).send({ msg: "server side" });
});

app.all("/isAlive", (req, res) => {
  res.status(200).send({ msg: "Server Alive" });
});

// Global 404 Error handler
app.all("/*", (req, res) => {
  res
    .status(404)
    .json({ msg: `${req.method} on ${req.originalUrl} not found` });
});

module.exports = app;
