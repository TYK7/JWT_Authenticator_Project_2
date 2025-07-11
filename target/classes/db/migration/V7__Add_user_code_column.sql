-- Add user_code column to users table
ALTER TABLE users ADD COLUMN user_code VARCHAR(20) UNIQUE;

-- Create index for better performance
CREATE UNIQUE INDEX idx_users_user_code ON users(user_code);

-- Add USER prefix to id_sequences table for user code generation
INSERT INTO id_sequences (prefix, current_number) VALUES ('USER', 0) ON CONFLICT (prefix) DO NOTHING;