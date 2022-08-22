DELETE FROM sprint WHERE project_id IN (SELECT project_id FROM project WHERE project_name LIKE '%Cypress%');
DELETE FROM event WHERE project_id IN (SELECT project_id FROM project WHERE project_name LIKE '%Cypress%');
DELETE FROM project WHERE project_name LIKE '%Cypress%';

-- Create the cypress test project
INSERT IGNORE INTO project (project_id, project_name, description, start_date, end_date) VALUES (1000, 'CypressProject', 'Automated Cypress project', '2022-07-15', '2023-07-20');
