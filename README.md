################################################################
#                                                              #
#                       USER-MANAGEMENT                        #
#                                                              #
################################################################

{
  "topicName": "NewAccountRequest",
  "correlatorId": 12345,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "username": "johndoe123",
  "password": "superSecret1",
  "rewardPoints": 250,
  "creditCard": "4111111111111111",
  "cvc": "123"
}

{
  "topicName": "LoginRequest",
  "correlatorId": 67890,
  "email": "john.doe@example.com",
  "password": "superSecret1"
}

{
  "topicName": "LoginRequest",
  "correlatorId": 67890,
  "email": "bryzntest@gmail.com",
  "password": "pass123"
}

// WORKING POSTGRES JSON TEST WITH DATABASE
{
  "topicName": "AccountInfoRequest",
  "correlatorId": 12345,
  "email": "bryzntest@gmail.com"
}

{
  "topicName": "AccountInfoRequest",
  "correlatorId": 12345,
  "username": "bryznnguyen"
}

{
  "topicName": "LoginRequest",
  "correlatorId": 67890,
  "email": "bryzntest@gmail.com",
  "password": "pass123"
}



POSTGRES NOTES
Flyway has scripts that can create tables and insert data. 
Files have to follow specific format for naming

Mapping tables to schemas to avoid versioning issue with Flyway

V<VERSION>__<NAME>.sql
V: Uppercase 'V' indicating a versioned migration.
<VERSION>: The version number (e.g., 1, 1_1, 202409221530).
__: Two underscores separating the version from the name.
<NAME>: A descriptive name for the migration (e.g., create_person_table).
.sql: The file extension.


MAP Entity USING @Column with fields such as name, nullable, or unique