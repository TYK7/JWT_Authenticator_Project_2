-- Add Google OAuth2 support columns to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS auth_provider VARCHAR(20) DEFAULT 'LOCAL';
ALTER TABLE users ADD COLUMN IF NOT EXISTS profile_picture_url VARCHAR(500);

-- Update existing users to have LOCAL auth provider
UPDATE users SET auth_provider = 'LOCAL' WHERE auth_provider IS NULL;

-- Add index for better performance
CREATE INDEX IF NOT EXISTS idx_users_auth_provider ON users(auth_provider);
CREATE INDEX IF NOT EXISTS idx_users_email_auth_provider ON users(email, auth_provider);