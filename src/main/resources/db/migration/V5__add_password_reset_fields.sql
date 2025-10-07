-- Add password reset token fields to users table
ALTER TABLE Users
ADD COLUMN reset_token VARCHAR(255),
ADD COLUMN reset_token_expiry TIMESTAMP;
