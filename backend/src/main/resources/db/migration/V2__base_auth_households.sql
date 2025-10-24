-- Enable UUID generator
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Users
CREATE TABLE IF NOT EXISTS users (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email          TEXT NOT NULL UNIQUE,
  password_hash  TEXT NOT NULL,
  full_name      TEXT,
  enabled        BOOLEAN NOT NULL DEFAULT TRUE,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Households
CREATE TABLE IF NOT EXISTS households (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name        TEXT NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Memberships (OWNER | MEMBER)
CREATE TABLE IF NOT EXISTS household_members (
  user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  household_id  UUID NOT NULL REFERENCES households(id) ON DELETE CASCADE,
  role          TEXT  NOT NULL CHECK (role IN ('OWNER','MEMBER')),
  joined_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, household_id)
);

-- Helpful indexes
CREATE INDEX IF NOT EXISTS idx_household_members_household ON household_members (household_id);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users (created_at);
