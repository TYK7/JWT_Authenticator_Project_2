-- Add firstName, lastName, and phoneNumber columns to users table
ALTER TABLE users 
ADD COLUMN first_name VARCHAR(100),
ADD COLUMN last_name VARCHAR(100),
ADD COLUMN phone_number VARCHAR(20);

-- Add indexes for better query performance (optional)
CREATE INDEX idx_users_first_name ON users(first_name);
CREATE INDEX idx_users_last_name ON users(last_name);
CREATE INDEX idx_users_phone_number ON users(phone_number);