const dateRegex = /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$/;

const dateTimeRegex =
  /\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d(?:\.\d+)?Z?/;
/**
 * check if date is in the format of 'YYYY-MM-DD'
 * example: 2022-01-08
 *
 * @param {string} date
 * @returns boolean
 */
exports.isInDateFormat = (date) => {
  return dateRegex.test(date);
};

/**
 * check if date is in the format of 'YYYY-MM-DDTHH:MM:SS'.
 * example: 2022-01-08T14:07:34
 *
 * @param {string} date
 * @returns boolean
 */
exports.isInDateTimeFormat = (date) => {
  return dateTimeRegex.test(date);
};
