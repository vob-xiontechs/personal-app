CREATE TABLE IF NOT EXISTS tbl_users (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Optional: insert user default
INSERT INTO tbl_users (username, password, email)
VALUES ('admin', 'admin123', 'admin@example.com')
    ON DUPLICATE KEY UPDATE username=username;
