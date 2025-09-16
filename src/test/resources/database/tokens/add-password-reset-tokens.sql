DELETE FROM password_reset_tokens;
INSERT INTO password_reset_tokens (id, token, user_id, expiry_date)
VALUES
    (1, 'token123', 1, NOW() + INTERVAL 1 DAY),
    (2, 'token456', 2, NOW() + INTERVAL 1 DAY);
