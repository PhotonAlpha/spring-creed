INSERT INTO AUTHORITYS VALUES(1, 'ROLE_ADMIN')
INSERT INTO AUTHORITYS VALUES(2, 'ROLE_USER')

INSERT INTO USERS (ID, USERNAME, PASSWORD, FIRSTNAME, LASTNAME, EMAIL, ENABLED, LASTPASSWORDRESETDATE) VALUES (1, 'admin', '{bcrypt}$2a$10$LkYRNHvPM2CHReB7uBGZaOQGGl5vO.hf9UwNNOrEW/uU/eVYsmGMS', 'admin', 'admin', 'admin@admin.com', 1, '2019-10-14T21:32:29');
INSERT INTO USERS (ID, USERNAME, PASSWORD, FIRSTNAME, LASTNAME, EMAIL, ENABLED, LASTPASSWORDRESETDATE) VALUES (2, 'normal', '{bcrypt}$2a$10$tWM/R2U5FOJmZOPB5Ah8UeG4tnjx4LaIwRhSrxLM/tSKtbqEE3HVK', 'normal', 'normal', 'normal@normal.com', 1, '2019-10-14T21:32:29');

INSERT INTO USER_AUTHORITYS (USER_ID, AUTHORITY_ID) VALUES (1, 1);
INSERT INTO USER_AUTHORITYS (USER_ID, AUTHORITY_ID) VALUES (2, 2);