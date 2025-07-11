-- Create id_sequences table for flexible ID generation
CREATE TABLE id_sequences (
    id BIGSERIAL PRIMARY KEY,
    prefix VARCHAR(10) NOT NULL UNIQUE,
    current_number BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add indexes for better performance
CREATE UNIQUE INDEX idx_id_sequences_prefix ON id_sequences(prefix);
CREATE INDEX idx_id_sequences_current_number ON id_sequences(current_number);

-- Insert default prefix from application.properties
INSERT INTO id_sequences (prefix, current_number) VALUES ('MRTFY', 0);

-- Add some example prefixes for testing
INSERT INTO id_sequences (prefix, current_number) VALUES ('MKTY', 0);
INSERT INTO id_sequences (prefix, current_number) VALUES ('TEST', 0);