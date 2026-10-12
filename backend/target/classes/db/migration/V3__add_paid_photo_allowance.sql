-- Add paid_photo_allowance column to businesses
ALTER TABLE businesses ADD COLUMN IF NOT EXISTS paid_photo_allowance INT NOT NULL DEFAULT 0;
