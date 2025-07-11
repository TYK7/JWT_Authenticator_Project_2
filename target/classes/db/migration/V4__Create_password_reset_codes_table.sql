-- Create password_reset_codes table
CREATE TABLE password_reset_codes (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE
);

-- Add indexes for better performance
CREATE INDEX idx_password_reset_codes_email_tenant ON password_reset_codes(email, tenant_id);
CREATE INDEX idx_password_reset_codes_code ON password_reset_codes(code);
CREATE INDEX idx_password_reset_codes_expires_at ON password_reset_codes(expires_at);
CREATE INDEX idx_password_reset_codes_used ON password_reset_codes(used);