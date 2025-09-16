DELETE FROM resumes;
INSERT INTO resumes (id, file_path, user_id, file_data, file_name, file_type)
VALUES
    (1, '/resumes/java_backend.pdf', 1, NULL, 'Java Backend Developer', 'pdf'),
    (2, '/resumes/frontend_dev.pdf', 2, NULL, 'Frontend Developer', 'pdf');
