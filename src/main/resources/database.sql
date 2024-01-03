-- Add the created_timestamp column with a default value
ALTER TABLE employee ADD COLUMN created_timestamp TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- Update existing rows to have a specific timestamp (e.g., current timestamp)
UPDATE employee SET created_timestamp = CURRENT_TIMESTAMP;

-- Now alter the column to be non-nullable
ALTER TABLE employee ALTER COLUMN created_timestamp SET NOT NULL;

-- Add the is_processed column with a default value
ALTER TABLE employee ADD COLUMN is_processed BOOLEAN DEFAULT FALSE;

-- Existing rows will automatically get 'false' as the default value
-- Now alter the column to be non-nullable
ALTER TABLE employee ALTER COLUMN is_processed SET NOT NULL;
