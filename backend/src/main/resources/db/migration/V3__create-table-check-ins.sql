CREATE TABLE check_ins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_event_id VARCHAR(100) UNIQUE,
    agent_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    source VARCHAR(20) DEFAULT 'MANUAL',
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    address VARCHAR(255),
    accuracy DECIMAL(8, 2),
    speed DECIMAL(8, 2),
    notes VARCHAR(255),
    distance_from_previous DECIMAL(10, 2),
    occurred_at TIMESTAMP NOT NULL,
    synced_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (agent_id) REFERENCES agents(id)
);