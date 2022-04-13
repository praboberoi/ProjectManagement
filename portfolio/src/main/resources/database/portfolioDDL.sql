-- Create the Project entity table, where all projects are stored.
DROP TABLE IF EXISTS PROJECT;
CREATE TABLE PROJECT (
                         ID INT PRIMARY KEY,
                         PROJECT_NAME VARCHAR(32) UNIQUE NOT NULL,
                         PROJECT_DESCRIPTION BLOB(300),
                         START_DATE TIMESTAMP NOT NULL,
                         END_DATE TIMESTAMP NOT NULL
) COMMENT 'Store project details';

-- Create the Sprint entity table, where all the sprints are stored.
DROP TABLE IF EXISTS SPRINT;
CREATE TABLE SPRINT (
                        ID INT PRIMARY KEY,
                        SPRINT_LABEL VARCHAR(16),
                        SPRINT_NAME VARCHAR(32),
                        SPRINT_DESCRIPTION BLOB(300),
                        START_DATE TIMESTAMP NOT NULL,
                        END_DATE TIMESTAMP NOT NULL,
                        PROJECT_ID INT NOT NULL,
                        FOREIGN KEY (PROJECT_ID) REFERENCES PROJECT (ID)
) COMMENT 'Store sprint details, a sprint must be a part of a project.';