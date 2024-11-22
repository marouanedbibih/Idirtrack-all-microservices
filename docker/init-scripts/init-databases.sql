-- Create databases
CREATE DATABASE IF NOT EXISTS stock_db;
CREATE DATABASE IF NOT EXISTS user_db;
CREATE DATABASE IF NOT EXISTS vehicle_db;

-- Create user if not exists

-- Grant permissions for each database
GRANT ALL PRIVILEGES ON stock_db.* TO 'marouane'@'%';
GRANT ALL PRIVILEGES ON user_db.* TO 'marouane'@'%';
GRANT ALL PRIVILEGES ON vehicle_db.* TO 'marouane'@'%';

-- Optional: Grant global privileges (uncomment if needed)
-- GRANT ALL PRIVILEGES ON *.* TO 'marouane'@'%' WITH GRANT OPTION;

-- Apply changes
FLUSH PRIVILEGES;
