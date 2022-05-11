const db = require("../config/db");
const apartmentsTable = "apartments";
const apartmentsUserTable = "user_in_apartment";

exports.getData = async (apartmentsId) => {
  const query = `
    SELECT * FROM ${apartmentsTable}
    WHERE ID = ?
    `;

  const [apartment, _] = await db.execute(query, [apartmentsId]);

  return apartment[0];
};

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
    SET number_of_people = number_of_people+1
    WHERE ID = ?;
    `;
  result = await db.execute(updateNumberOfPeopleQuery, [apartmentsId]);
  return result;
};
