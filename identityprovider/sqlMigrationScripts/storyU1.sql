--Adds auto incrementing to the sprint column to fix issue caused by primary key being null
-- Note: This only works on mariadb
LOCK TABLES 
    `sprint` WRITE,
    `project` WRITE,
    `event` WRITE; --If this table doesn't exist, remove this line and add the ; to the end of the previous one
SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE `sprint` CHANGE `sprint_id` `sprint_id` INT(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `project` CHANGE `project_id` `project_id` INT(11) NOT NULL AUTO_INCREMENT;
SET FOREIGN_KEY_CHECKS = 1;
UNLOCK TABLES;

/*
H2 commands
ALTER TABLE sprint ALTER COLUMN sprint_id INT(11) NOT NULL AUTO_INCREMENT
ALTER TABLE project ALTER COLUMN project_id INT(11) NOT NULL AUTO_INCREMENT;
*/