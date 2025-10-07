CREATE TABLE IF NOT EXISTS bootcamp (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(90) NOT NULL,
    technologies TEXT NOT NULL
);
