-- Add image column to Activity table to store base64 encoded images
ALTER TABLE public.Activity ADD COLUMN IF NOT EXISTS image TEXT;
