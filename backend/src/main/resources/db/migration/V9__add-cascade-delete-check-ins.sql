ALTER TABLE check_ins DROP FOREIGN KEY check_ins_ibfk_1;
ALTER TABLE check_ins ADD CONSTRAINT check_ins_ibfk_1
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE;