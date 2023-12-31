-- Delete any cypress test users
DELETE FROM user_roles WHERE user_user_id IN (SELECT user_id FROM user WHERE username LIKE '%Cypress%');
DELETE FROM users_groups WHERE group_id IN (SELECT group_id FROM groups WHERE long_name LIKE '%Cypress%' OR short_name LIKE '%Cypress%') OR group_id IN (SELECT user_id FROM user WHERE username LIKE '%Cypress%');
DELETE FROM users_groups WHERE user_id IN (SELECT user_id FROM user WHERE username LIKE '%Cypress%') OR group_id IN (SELECT user_id FROM user WHERE username LIKE '%Cypress%');
DELETE FROM groups WHERE long_name LIKE '%Cypress%' OR short_name LIKE '%Cypress%';
DELETE FROM user WHERE username LIKE '%Cypress%';

-- Create admin user, large user id to reduce change of overlap
INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1285321, 'Automated test admin','2022-07-15 21:45:57.287000000','automated@cypress.com','Cypress','Admin','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','CypressAdmin');
INSERT IGNORE INTO user_roles (user_user_id,roles,order_column) VALUES (1285321,0,0);
INSERT IGNORE INTO user_roles (user_user_id,roles,order_column) VALUES (1285321,1,1);
INSERT IGNORE INTO user_roles (user_user_id,roles,order_column) VALUES (1285321,2,2);

-- Create test users
INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1000, 'Automated test student','2022-07-15 21:45:57.287000000','automated@cypress.com','Cypress','Student','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','Cypress1');
INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1001, 'Automated test student','2022-07-15 21:45:57.287000000','automated@cypress.com','Cypress','Student','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','Cypress2');
INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1002, 'Automated test student','2022-07-15 21:45:57.287000000','automated@cypress.com','Cypress','Student','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','Cypress3');
INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1003, 'Automated test student','2022-07-15 21:45:57.287000000','automated@cypress.com','Cypress','Student','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','Cypress4');
INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1004, 'Automated test student','2022-07-15 21:45:57.287000000','automated@cypress.com','Cypress','Student','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','Cypress5');
INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1005, 'Automated test student','2022-07-15 21:45:57.287000000','automated@cypress.com','Cypress','Student','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','Cypress6');
INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1006, 'Automated test student','2022-07-15 21:45:57.287000000','automated@cypress.com','Cypress','Student','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','Cypress7');
INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1007, 'Automated test student','2022-07-15 21:45:57.287000000','automated@cypress.com','Cypress','Student','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','Cypress8');
INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1008, 'Automated test student','2022-07-15 21:45:57.287000000','automated@cypress.com','Cypress','Student','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','Cypress9');


-- Create student user
INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1285315, 'Automated test student','2022-07-15 21:45:57.287000000','automated@cypress.com','Cypress','Student','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','CypressStudent');
INSERT IGNORE INTO user_roles (user_user_id,roles,order_column) VALUES (1285315,0,0);

INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1285322, 'Automated test guinea pig','2022-07-15 21:45:57.287000000','automated@cypress.com','Guinea','pig','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','CypressGuineaPig');
INSERT IGNORE INTO user_roles (user_user_id,roles,order_column) VALUES (1285322,0,0);
INSERT IGNORE INTO user_roles (user_user_id,roles,order_column) VALUES (1285322,2,1);
-- U5 test users
INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1285323, 'Automated test guinea pig','2022-07-15 21:45:57.287000000','automated@cypress.com','Guinea','pig','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','CypressGroupGuineaPig');
INSERT IGNORE INTO user_roles (user_user_id,roles,order_column) VALUES (1285323,0,0);
INSERT IGNORE INTO user_roles (user_user_id,roles,order_column) VALUES (1285323,1,1);
INSERT IGNORE INTO user (user_id, bio,date_created,email,first_name,last_name,nickname,password,profile_image_path,pronouns,salt,username) VALUES (1285324, 'Automated test guinea pig','2022-07-15 21:45:57.287000000','automated@cypress.com','Guinea','pig','','JRqHHIu2hllQo8uswun8chNpkSnX2ERuhyhToKmSdYc=',null,'','aNAKNt2LHIfchQoLQ0Aryg==','CypressUnassignedGuineaPig');
INSERT IGNORE INTO user_roles (user_user_id,roles,order_column) VALUES (1285324,0,0);

-- Create group and add member into it
INSERT IGNORE INTO groups (group_id, short_name, long_name) VALUES (1285322, 'Cypress1', 'Cypress test group 1');
INSERT IGNORE INTO groups (group_id, short_name, long_name) VALUES (1285323, 'Cypress2', 'Cypress test group 2');
INSERT IGNORE INTO groups (group_id, short_name, long_name) VALUES (1000, 'Cypress title update', 'Cypress test group 3');
INSERT IGNORE INTO users_groups (user_id, group_id) VALUES (1285323, 1285322);
