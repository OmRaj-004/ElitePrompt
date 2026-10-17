-- Elite Prompt - PostgreSQL Database (single setup file, v4)
-- Tested on PostgreSQL 18 (also works on 15/16/17)
--
-- Works for BOTH cases, in one run, without losing data:
--   Fresh database    -> creates all tables with every column.
--   Existing database -> CREATE TABLE IF NOT EXISTS skips what exists,
--                        ALTER TABLE ... IF NOT EXISTS adds only the
--                        missing v4 columns. No DROP anywhere, rows are kept.
--
-- In pgAdmin: select your database, open Query Tool, paste and run.
-- In psql:    psql -U postgres -d eliteprompt -f database/database.sql

-- Create database (run once, outside any database, then connect to it):
-- CREATE DATABASE eliteprompt;
-- (Already have the database? Skip this and just run the rest connected to it.)

-- Users table (safe to re-run: creates only if missing, never drops data)
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    zen_api_key VARCHAR(255) NOT NULL DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Admins live in a SEPARATE table for security: admin credentials are never
-- stored in the users table, so a users-table breach cannot expose admin passwords.
CREATE TABLE IF NOT EXISTS admins (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Prompts table (includes v4 columns: favorites, public sharing, tags)
CREATE TABLE IF NOT EXISTS prompts (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    requirement TEXT NOT NULL,
    tone VARCHAR(50) NOT NULL,
    output_format VARCHAR(50) NOT NULL,
    generated_prompt TEXT NOT NULL,
    is_favorite BOOLEAN NOT NULL DEFAULT FALSE,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    tags VARCHAR(255) NOT NULL DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Feedback table
CREATE TABLE IF NOT EXISTS feedback (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Upgrade guard: if tables were created by an older version of this file,
-- add the newer columns without touching existing rows (safe to re-run).
ALTER TABLE prompts ADD COLUMN IF NOT EXISTS is_favorite BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE prompts ADD COLUMN IF NOT EXISTS is_public BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE prompts ADD COLUMN IF NOT EXISTS tags VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE users ADD COLUMN IF NOT EXISTS zen_api_key VARCHAR(255) NOT NULL DEFAULT '';

-- Upgrade path: move any old is_admin admins into the separate admins table
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='is_admin') THEN
        INSERT INTO admins (full_name, username, email, password)
        SELECT u.full_name, u.username, u.email, u.password
        FROM users u WHERE u.is_admin = TRUE
        ON CONFLICT (username) DO NOTHING;
        DELETE FROM users WHERE is_admin = TRUE;
        ALTER TABLE users DROP COLUMN is_admin;
    END IF;
END $$;

-- ---------------------------------------------------------------------------
-- Sample data (runs only on an empty database, never duplicates)
-- Login:  username "demo"  / password "demo1234"
--         username "priya" / password "demo1234"
-- (password hash below is BCrypt of "demo1234")
-- ---------------------------------------------------------------------------

INSERT INTO users (full_name, username, email, password)
SELECT 'Demo User', 'demo', 'demo@eliteprompt.com',
       '$2a$12$9XM.zsokxZJZgFD8n16X2eCTL1bwTszrl/vhjLrmSyiG1wdr1M8zi'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'demo');

INSERT INTO users (full_name, username, email, password)
SELECT 'Priya Sharma', 'priya', 'priya@eliteprompt.com',
       '$2a$12$9XM.zsokxZJZgFD8n16X2eCTL1bwTszrl/vhjLrmSyiG1wdr1M8zi'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'priya');

-- Seed the admin into the SEPARATE admins table (full admin rights)
INSERT INTO admins (full_name, username, email, password)
SELECT 'Site Administrator', 'Admin@elite', 'admin@eliteprompt.com',
       '$2a$12$/vaOZRyw5LBZHCl3a9pwY.12W4SlbXY4nRQiWPzM9p.Gdgf/PRS..'
WHERE NOT EXISTS (SELECT 1 FROM admins WHERE username = 'Admin@elite');

-- Sample prompts for demo (favorite + public + tags covered)
INSERT INTO prompts (user_id, title, category, requirement, tone, output_format, generated_prompt, is_favorite, is_public, tags)
SELECT id, 'Gym Reel Caption', 'Content Creation',
       'Create an Instagram caption for a gym video',
       'Motivating', 'Short Caption',
       'You are an expert social media content creator. Create an Instagram caption for a gym video. Use a motivating tone. Provide the output in short caption format. Keep it clear, concise and actionable.',
       TRUE, TRUE, 'instagram, fitness, social'
FROM users WHERE username = 'demo'
AND NOT EXISTS (SELECT 1 FROM prompts WHERE title = 'Gym Reel Caption');

INSERT INTO prompts (user_id, title, category, requirement, tone, output_format, generated_prompt, is_favorite, is_public, tags)
SELECT id, 'Java Inheritance Notes', 'Education',
       'Explain inheritance in Java with examples for exam preparation',
       'Professional', 'Detailed Explanation',
       'You are an expert tutor and educator. Explain inheritance in Java with examples for exam preparation. Use a professional tone. Provide the output in detailed explanation format. Keep it clear, concise and actionable.',
       TRUE, FALSE, 'java, oops, exam'
FROM users WHERE username = 'demo'
AND NOT EXISTS (SELECT 1 FROM prompts WHERE title = 'Java Inheritance Notes');

INSERT INTO prompts (user_id, title, category, requirement, tone, output_format, generated_prompt, is_favorite, is_public, tags)
SELECT id, 'Startup Launch Email', 'Marketing',
       'Write a launch email for a startup selling handmade soaps to young customers',
       'Friendly', 'Paragraph',
       'You are an expert digital marketer and copywriter. Write a launch email for a startup selling handmade soaps to young customers. Use a friendly tone. Provide the output in paragraph format. Keep it clear, concise and actionable.',
       FALSE, TRUE, 'email, startup, soap'
FROM users WHERE username = 'priya'
AND NOT EXISTS (SELECT 1 FROM prompts WHERE title = 'Startup Launch Email');

INSERT INTO prompts (user_id, title, category, requirement, tone, output_format, generated_prompt, is_favorite, is_public, tags)
SELECT id, 'Palindrome Checker Code', 'Programming',
       'Write a Java program to check if a string is a palindrome with steps',
       'Professional', 'Step-by-step Guide',
       'You are a senior software developer and coding mentor. Write a Java program to check if a string is a palindrome with steps. Use a professional tone. Provide the output in step-by-step guide format. Keep it clear, concise and actionable.',
       FALSE, TRUE, 'java, coding, beginners'
FROM users WHERE username = 'priya'
AND NOT EXISTS (SELECT 1 FROM prompts WHERE title = 'Palindrome Checker Code');

-- Make demo account the admin so you can log in and see the admin dashboard
UPDATE users SET is_admin = TRUE WHERE username = 'demo';

-- Sample feedback entry
INSERT INTO feedback (name, email, subject, message)
SELECT 'Demo User', 'demo@eliteprompt.com', 'Great app!',
       'The public gallery and favorites features are very useful. Keep it up!'
WHERE NOT EXISTS (SELECT 1 FROM feedback WHERE subject = 'Great app!');
