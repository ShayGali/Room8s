const db = require("../../config/db");
const apartmentsTable = "apartments";
const apartmentsUserTable = "user_in_apartment";

exports.getData = async (apartmentsId) => {
  const query = `
    SELECT * FROM ${apartmentsTable}
    WHERE ID = ?;
    `;

  const [apartment, _] = await db.execute(query, [apartmentsId]);

  return apartment[0];
};

// create a new apartment
/**
 * crete new apartment and return the id of the apartment
 * it change two tables:
 *    apartments -> add the new apartment
 *    user_in_apartment -> create the relationship between the user and his apartment
 * @param {number} userId
 * @param {string} apartmentName
 * @returns new apartment id
 */
exports.createApartment = async (userId, apartmentName) => {
  const createApartmentQuery = `
    INSERT INTO ${apartmentsTable} (
            apartment_name
        )
        VALUE (?);
    `;
  let result = await db.execute(createApartmentQuery, [apartmentName]);
  const apartmentsId = result[0].insertId;

  const createRelationQuery = `
  INSERT INTO ${apartmentsUserTable} (
    apartment_ID, user_ID
  )
  VALUE (?, ?);
  `;

  result = await db.execute(createRelationQuery, [apartmentsId, userId]);
  return apartmentsId;
};

/**
 * add new user to exists apartment
 * it change two tables:
 *   apartments -> add the new apartment
 *   user_in_apartment -> create the relationship between the user and his apartment
 * @param {number} apartmentsId
 * @param {number} newUserId
 * @returns
 */
exports.addUserToApartment = async (apartmentsId, newUserId) => {
  const createRelationQuery = `
INSERT INTO ${apartmentsUserTable} (
    apartment_ID, user_ID
  )
  VALUE (?, ?);
`;
  let result = await db.execute(createRelationQuery, [apartmentsId, newUserId]);
  if (!result) return undefined;

  const updateNumberOfPeopleQuery = `
    UPDATE ${apartmentsTable}
    SET number_of_people = number_of_people + 1
    WHERE ID = ?;
    `;
  result = await db.execute(updateNumberOfPeopleQuery, [apartmentsId]);
  return result;
};

/**
 *
 * @param {number} apartmentId
 * @param {number} userId
 * @returns
 */
exports.removeUserFromApartment = async (apartmentId, userId) => {
  const deleteRelationQuery = `
  DELETE FROM ${apartmentsUserTable}
  WHERE user_ID = ? AND apartment_ID = ?;
  `;

  let result = await db.execute(deleteRelationQuery, [userId, apartmentId]);
  if (!result || result[0]?.affectedRows === 0) return;

  const updateNumberOfPeopleQuery = `
    UPDATE ${apartmentsTable}
    SET number_of_people = number_of_people - 1
    WHERE ID = ?;
    `;
  result = await db.execute(updateNumberOfPeopleQuery, [apartmentId]);

  const checkNumOfPeopleQuery = `
  SELECT number_of_people FROM ${apartmentsTable}
  WHERE ID = ? ;
  `;

  const [numberOfPeople, _] = await db.execute(checkNumOfPeopleQuery, [
    apartmentId,
  ]);
  if (numberOfPeople[0].number_of_people < 0) {
    this.deleteApartment(apartmentId);
  }
};

/**
 * @param {number} apartmentId
 * @returns {Promise<Array<number>>}
 */
exports.getRoom8Ids = async (apartmentId) => {
  const query = `SELECT user_ID
   FROM ${apartmentsUserTable}
   WHERE apartment_ID = ?;`;
  const [result, _] = await db.execute(query, [apartmentId]);
  return result.map((obj) => obj.user_ID);
};

exports.deleteApartment = (apartmentId) => {
  const query = `DELETE FROM ${apartmentsTable} WHERE ID = ?`;
  db.execute(query, [apartmentId]);
};
