const nodemailer = require("nodemailer");
const transporter = nodemailer.createTransport({
    service: "Zoho",
    auth: {
      user: process.env.ADMIN_MAIL,
      pass: process.env.ADMIN_PASS,
    },
    tls: {
      rejectUnauthorized: false,
    },
  });
  module.exports = transporter;