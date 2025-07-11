-- Rename tenant_id to brand_id in users table
ALTER TABLE users RENAME COLUMN tenant_id TO brand_id;

-- Update indexes if any exist
DROP INDEX IF EXISTS idx_users_tenant_id;
CREATE INDEX idx_users_brand_id ON users(brand_id);

-- Rename tenant_id to brand_id in password_reset_codes table
ALTER TABLE password_reset_codes RENAME COLUMN tenant_id TO brand_id;

-- Update indexes for password_reset_codes
DROP INDEX IF EXISTS idx_password_reset_codes_email_tenant;
CREATE INDEX idx_password_reset_codes_email_brand ON password_reset_codes(email, brand_id);