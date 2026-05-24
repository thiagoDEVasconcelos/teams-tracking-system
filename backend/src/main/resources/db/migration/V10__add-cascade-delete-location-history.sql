ALTER TABLE location_history DROP FOREIGN KEY location_history_ibfk_1;
ALTER TABLE location_history ADD CONSTRAINT location_history_ibfk_1
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE;