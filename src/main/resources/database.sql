-- Grant all privileges on the database
GRANT ALL PRIVILEGES ON DATABASE mydb TO myuser;

-- Connect to the specific database
\c mydb

-- Grant all privileges on all tables in the public schema
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO myuser;

-- Grant privileges on future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO myuser;

-- Grant usage on schema
GRANT USAGE ON SCHEMA public TO myuser;

-- Grant create permission on schema
GRANT CREATE ON SCHEMA public TO myuser;