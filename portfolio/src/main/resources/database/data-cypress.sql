DELETE FROM sprint WHERE project_id IN (SELECT project_id FROM project WHERE project_name LIKE '%Cypress%');
DELETE FROM event WHERE project_id IN (SELECT project_id FROM project WHERE project_name LIKE '%Cypress%');
DELETE FROM deadline_colors where deadline_deadline_id IN (SELECT deadline_id FROM deadline WHERE project_id IN (SELECT project_id FROM project WHERE project_name LIKE '%Cypress%'));
DELETE FROM deadline WHERE project_id IN (SELECT project_id FROM project WHERE project_name LIKE '%Cypress%');
DELETE FROM evidence WHERE project_id IN (SELECT project_id FROM project WHERE project_name LIKE '%Cypress%');
DELETE FROM project WHERE project_name LIKE '%Cypress%';

-- Create the cypress test project
INSERT IGNORE INTO project (project_id, project_name, description, start_date, end_date) VALUES (1000, 'CypressProject', 'Automated Cypress project', '2022-07-15', '2023-07-20');
INSERT IGNORE INTO event (event_id, event_name, start_date, end_date, project_id) VALUES (1000,'Cypress Websocket Delete Event', '2022-07-17', '2022-07-20', 1000);
INSERT IGNORE INTO event (event_id, event_name, start_date, end_date, project_id) VALUES (999,'Cypress Websocket Edit Event', '2022-07-17', '2022-07-20', 1000);
INSERT IGNORE INTO evidence (evidence_id, date_occurred, description, owner_id, title, project_id) VALUES (100, CURRENT_DATE, 'Cypress description', 1285325, 'Cypress', 1000);
INSERT IGNORE INTO evidence (evidence_id, date_occurred, description, owner_id, title, project_id) VALUES (101, CURRENT_DATE, 'Cypress description 2', 1285325, 'Cypress 2', 1000);