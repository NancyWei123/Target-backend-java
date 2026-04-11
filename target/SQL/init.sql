-- Create task table
CREATE TABLE IF NOT EXISTS task (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20),
    due_time TIMESTAMP,
    completed BOOLEAN DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS users (
   id BIGSERIAL PRIMARY KEY,
   username VARCHAR(100) NOT NULL,
   email VARCHAR(150) NOT NULL UNIQUE,
   password VARCHAR(255) NOT NULL,
   is_active BOOLEAN DEFAULT TRUE,
   created_at TIMESTAMP,
   updated_at TIMESTAMP
);

