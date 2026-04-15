CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP,
                       dark_mode BOOLEAN DEFAULT FALSE,
                       email_notifications BOOLEAN DEFAULT TRUE,
                       task_reminders BOOLEAN DEFAULT TRUE
);

CREATE TABLE task (
                      id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      description VARCHAR(255),
                      priority VARCHAR(255),
                      due_time TIMESTAMP,
                      completed BOOLEAN DEFAULT FALSE,
                      user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                      completed_time TIMESTAMP
);