DROP TABLE IF EXISTS capability_technology;
DROP TABLE IF EXISTS bootcamp;
DROP TABLE IF EXISTS technologies;

CREATE SCHEMA IF NOT EXISTS bootcamp;

CREATE TABLE IF NOT EXISTS bootcamp.technologies (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(90) NOT NULL
);

CREATE TABLE IF NOT EXISTS bootcamp.capabilities (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS bootcamp.capability_technology (
    capability_id VARCHAR(36) NOT NULL,
    technology_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (capability_id, technology_id),
    FOREIGN KEY (capability_id) REFERENCES bootcamp.capabilities(id) ON DELETE CASCADE,
    FOREIGN KEY (technology_id) REFERENCES bootcamp.technologies(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bootcamp.bootcamps (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    launch_date DATE NOT NULL,
    duration_weeks INT NOT NULL
);

CREATE TABLE IF NOT EXISTS bootcamp.bootcamp_capability (
    bootcamp_id VARCHAR(36) NOT NULL,
    capability_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (bootcamp_id, capability_id),
    FOREIGN KEY (bootcamp_id) REFERENCES bootcamp.bootcamps(id) ON DELETE CASCADE,
    FOREIGN KEY (capability_id) REFERENCES bootcamp.capabilities(id) ON DELETE CASCADE
);
