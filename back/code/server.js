require("dotenv").config();

module.exports = {
  authenticateToken,
};

const express = require("express");

const app = express();
const PORT = process.env.PORT || 3000;

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

  res.status(500).json({ message: "Something went wrong" });
});

app.get("/", (req, res) => {
  res.status(404).send({ msg: "Server Side" });
});

app.all("/*", (req, res) => {
  res.status(404).json({ message: "Path not found" });
});

app.listen(PORT, () => console.log(`Listening in port ${PORT}`));

function authenticateToken(req, res, next) {
  const JWT = require("jsonwebtoken");
  const token = req.header("x-auth-token");
  if (!token) return res.status(401).send({ msg: "Send token" }); // TODO: make better error message
  JWT.verify(token, process.env.ACCESS_TOKEN_SECRET, (err, tokenData) => {
    if (err) return res.status(403).send({ msg: "Invalid token" });
    req.tokenData = tokenData;
    next(); // move to the request
  });
}
