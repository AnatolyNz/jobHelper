-- Clear tables before inserting (order matters due to FK constraints)
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE users_roles;
TRUNCATE TABLE users;
TRUNCATE TABLE roles;
TRUNCATE TABLE jobs;
TRUNCATE TABLE skills;
TRUNCATE TABLE job_skills;
TRUNCATE TABLE resumes;

SET FOREIGN_KEY_CHECKS = 1;

-- Insert roles
INSERT INTO roles (role_name) VALUES
('ADMIN'),
('USER');

-- Insert users
INSERT INTO users (email, password, first_name, last_name, is_deleted) VALUES
('admin@example.com', 'adminpass', 'Admin', 'Adminov', FALSE),
('user@example.com', 'userpass', 'User', 'Userov', FALSE);

-- Link users to roles (adjust role_id if needed)
-- Assuming AUTO_INCREMENT assigned 1 = ADMIN, 2 = USER
INSERT INTO users_roles (user_id, role_id) VALUES
(1, 1), -- admin
(2, 2); -- user

-- Insert jobs
INSERT INTO jobs (title, description) VALUES
('Backend Developer', 'Responsible for Java/Spring development'),
('Frontend Developer', 'Responsible for React/JS development');

-- Insert skills
INSERT INTO skills (name) VALUES
('Java'),
('Spring'),
('React'),
('JavaScript');

-- Link jobs to skills
INSERT INTO job_skills (job_id, skill_id) VALUES
(1, 1), -- Backend Developer needs Java
(1, 2), -- Backend Developer needs Spring
(2, 3), -- Frontend Developer needs React
(2, 4); -- Frontend Developer needs JavaScript

-- Insert resumes (example)
INSERT INTO resumes (file_path, user_id, file_name, file_type) VALUES
('/resumes/java_backend.pdf', 2, 'Java Backend Developer', 'pdf'),
('/resumes/frontend.pdf', 2, 'Frontend Developer', 'pdf');
