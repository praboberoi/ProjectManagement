-- Create a table user to store the details of each LENSfolio user.
DROP TABLE IF EXISTS USER;
CREATE TABLE USER (
                      ID INT PRIMARY KEY ,
                      USERNAME VARCHAR(35) NOT NULL UNIQUE,
                      FIRST_NAME VARCHAR(35) NOT NULL,
                      LAST_NAME VARCHAR(35) NOT NULL,
                      NICKNAME VARCHAR(35),
                      BIO BLOB(300),
                      EMAIL VARCHAR(255) NOT NULL UNIQUE,
                      PASSWORD VARCHAR(128) NOT NULL,
                      SALT VARCHAR(128) NOT NULL ,
                      DATE_CREATED TIMESTAMP NOT NULL,
                      USER_IMAGE VARCHAR(128)
) COMMENT 'Store the User details.';

-- Create a table Role to store the different roles within LENSfolio.
DROP TABLE IF EXISTS ROLE;
CREATE TABLE ROLE (
                      ID INT PRIMARY KEY ,
                      CODE VARCHAR(24) NOT NULL UNIQUE ,
                      NAME VARCHAR(24) NOT NULL UNIQUE
) COMMENT 'Store the different roles within LENSfolio.';

-- Create a table UserRole which assigns roles to users.
DROP TABLE IF EXISTS USER_ROLE;
CREATE TABLE USER_ROLE (
                           ID INT PRIMARY KEY ,
                           USER_ID INT NOT NULL,
                           ROLE_ID INT NOT NULL ,
                           FOREIGN KEY (USER_ID) REFERENCES USER (ID),
                           FOREIGN KEY (ROLE_ID) REFERENCES ROLE (ID)
) COMMENT 'Stores the role assignments for a user.';
