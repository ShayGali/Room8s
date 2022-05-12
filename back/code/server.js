require("dotenv").config();

// in the start because we use the middleware in our routes so we want that it will be included there
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
  console.log(req);
  res.status(404).send({ msg: "Server Side" });
});

// Global 404 Error handler
app.all("/*", (req, res) => {
  res.status(404).json({ message: `${req.originalUrl} not found` });
});

app.listen(PORT, () => console.log(`Listening in port ${PORT}`));

/** Global middleware function to validate JWT token
 * will return:
 * 401 if token not send
 * 403 if the token is invalid
 * if the token is valid we add to the req the token data
 * add go to the next() function
 */
function authenticateToken(req, res, next) {
  const JWT = require("jsonwebtoken");
  const token = req.header("x-auth-token");
  if (!token) return res.status(401).send({ msg: "Send token" }); // TODO: make better error message
  JWT.verify(token, process.env.ACCESS_TOKEN_SECRET, (err, tokenData) => {
    if (err) return res.status(403).send({ msg: "Invalid token" });
    req.tokenData = tokenData;
    next(); // move to the function
  });
}
