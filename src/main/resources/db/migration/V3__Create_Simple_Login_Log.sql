-- Create simple login log table
CREATE TABLE IF NOT EXISTS login_log (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID,
    username VARCHAR(50) NOT NULL,
    login_method VARCHAR(20) NOT NULL, -- 'PASSWORD', 'GOOGLE'
    login_status VARCHAR(20) NOT NULL, -- 'SUCCESS', 'FAILURE'
    login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details TEXT,
    
    -- Foreign key to users table
    CONSTRAINT fk_login_log_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_login_log_user_id ON login_log(user_id);
CREATE INDEX IF NOT EXISTS idx_login_log_username ON login_log(username);
CREATE INDEX IF NOT EXISTS idx_login_log_login_time ON login_log(login_time);