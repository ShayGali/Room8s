require("dotenv").config();

const express = require("express");

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.use("/users", require("./routes/userRoutes"));

// Global Error handler
app.use((err, req, res, next) => {
  console.log(err.stack);
  console.log(err.name);
  console.log(err.code);

  res.status(500).json({ message: "Something went wrong" });
});

app.get("/", (req, res) => {
  res.send("Server Side");
});

app.all("/*", (req, res) => {
  res.status(404).json({ message: "Page not found" });
});

app.listen(PORT, () => console.log(`Listening in port ${PORT}`));
