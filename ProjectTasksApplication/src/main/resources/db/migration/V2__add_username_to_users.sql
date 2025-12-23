-- Add username column (nullable first to handle existing data)
ALTER TABLE users ADD COLUMN username VARCHAR(255);

-- Update existing users with a default username based on email if needed
UPDATE users SET username = 'user_' || id WHERE username IS NULL;

-- Now make it NOT NULL
ALTER TABLE users ALTER COLUMN username SET NOT NULL;

-- Add UNIQUE constraint
ALTER TABLE users ADD CONSTRAINT uk_users_username UNIQUE (username);

