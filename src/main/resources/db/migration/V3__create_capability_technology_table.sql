CREATE TABLE IF NOT EXISTS capability_technology (
    capability_id VARCHAR(36) NOT NULL,
    technology_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (capability_id, technology_id),
    FOREIGN KEY (capability_id) REFERENCES bootcamp(id) ON DELETE CASCADE,
    FOREIGN KEY (technology_id) REFERENCES technologies(id) ON DELETE CASCADE
);
