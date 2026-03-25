-- Create task table
CREATE TABLE IF NOT EXISTS task (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20),
    due_time TIMESTAMP,
    completed BOOLEAN DEFAULT FALSE
    );