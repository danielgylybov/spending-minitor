-- Accounts
CREATE TABLE IF NOT EXISTS accounts (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  household_id  UUID NOT NULL REFERENCES households(id) ON DELETE CASCADE,
  name          TEXT NOT NULL,
  currency      CHAR(3) NOT NULL DEFAULT 'BGN',
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_accounts_household ON accounts(household_id);

-- Categories (EXPENSE | INCOME)
CREATE TABLE IF NOT EXISTS categories (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  household_id  UUID NOT NULL REFERENCES households(id) ON DELETE CASCADE,
  name          TEXT NOT NULL,
  type          TEXT NOT NULL CHECK (type IN ('EXPENSE','INCOME')),
  UNIQUE (household_id, name, type)
);
CREATE INDEX IF NOT EXISTS idx_categories_household ON categories(household_id);

-- Merchants
CREATE TABLE IF NOT EXISTS merchants (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  household_id  UUID NOT NULL REFERENCES households(id) ON DELETE CASCADE,
  display_name  TEXT NOT NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (household_id, display_name)
);
CREATE INDEX IF NOT EXISTS idx_merchants_household ON merchants(household_id);

-- Merchant aliases (EXACT | CONTAINS | REGEX | IBAN | MCC)
CREATE TABLE IF NOT EXISTS merchant_aliases (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  household_id  UUID NOT NULL REFERENCES households(id) ON DELETE CASCADE,
  merchant_id   UUID NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
  match_type    TEXT NOT NULL CHECK (match_type IN ('EXACT','CONTAINS','REGEX','IBAN','MCC')),
  pattern       TEXT NOT NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_aliases_household ON merchant_aliases(household_id);
CREATE INDEX IF NOT EXISTS idx_aliases_merchant  ON merchant_aliases(merchant_id);
CREATE INDEX IF NOT EXISTS idx_aliases_type      ON merchant_aliases(match_type);

-- Category rules (MERCHANT | ALIAS | REGEX | IBAN | MCC)
CREATE TABLE IF NOT EXISTS category_rules (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  household_id  UUID NOT NULL REFERENCES households(id) ON DELETE CASCADE,
  merchant_id   UUID REFERENCES merchants(id) ON DELETE CASCADE,
  match_type    TEXT NOT NULL CHECK (match_type IN ('MERCHANT','ALIAS','REGEX','IBAN','MCC')),
  pattern       TEXT,
  category_id   UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
  priority      INT NOT NULL DEFAULT 100,
  is_active     BOOLEAN NOT NULL DEFAULT TRUE,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_category_rules_household ON category_rules(household_id);
CREATE INDEX IF NOT EXISTS idx_category_rules_merchant  ON category_rules(merchant_id);
CREATE INDEX IF NOT EXISTS idx_category_rules_category  ON category_rules(category_id);
CREATE INDEX IF NOT EXISTS idx_category_rules_priority  ON category_rules(priority);

-- Import batches
CREATE TABLE IF NOT EXISTS import_batches (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  household_id  UUID NOT NULL REFERENCES households(id) ON DELETE CASCADE,
  source        TEXT NOT NULL,
  uploaded_by   UUID NOT NULL REFERENCES users(id),
  uploaded_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
  status        TEXT NOT NULL CHECK (status IN ('UPLOADED','PARSED','APPLIED','DISCARDED')) DEFAULT 'UPLOADED',
  file_name     TEXT,
  total_rows    INT DEFAULT 0,
  applied_rows  INT DEFAULT 0,
  skipped_rows  INT DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_import_batches_household ON import_batches(household_id);
CREATE INDEX IF NOT EXISTS idx_import_batches_status    ON import_batches(status);

-- Import rows
CREATE TABLE IF NOT EXISTS import_rows (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  batch_id         UUID NOT NULL REFERENCES import_batches(id) ON DELETE CASCADE,
  raw_date         TEXT,
  raw_amount       TEXT,
  raw_currency     TEXT,
  raw_counterparty TEXT,
  raw_reference    TEXT,
  raw_mcc          TEXT,
  raw_iban         TEXT,
  parsed_at        TIMESTAMPTZ,
  parse_ok         BOOLEAN DEFAULT FALSE,
  parse_error      TEXT,
  occurred_at      DATE,
  amount           NUMERIC(14,2),
  currency         CHAR(3),
  counterparty     TEXT,
  reference        TEXT,
  merchant_id      UUID REFERENCES merchants(id),
  category_id      UUID REFERENCES categories(id),
  dedupe_hash      TEXT,
  decided_by       TEXT -- 'RULE','MANUAL','NONE'
);
CREATE INDEX IF NOT EXISTS idx_import_rows_batch     ON import_rows(batch_id);
CREATE INDEX IF NOT EXISTS idx_import_rows_parse_ok  ON import_rows(parse_ok);
CREATE INDEX IF NOT EXISTS idx_import_rows_merchant  ON import_rows(merchant_id);
CREATE INDEX IF NOT EXISTS idx_import_rows_category  ON import_rows(category_id);

-- Transactions
CREATE TABLE IF NOT EXISTS transactions (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  account_id    UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  category_id   UUID REFERENCES categories(id),
  merchant_id   UUID REFERENCES merchants(id),
  amount        NUMERIC(14,2) NOT NULL, -- + income, - expense
  occurred_at   DATE NOT NULL,
  currency      CHAR(3) NOT NULL DEFAULT 'BGN',
  description   TEXT,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_transactions_account    ON transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_category   ON transactions(category_id);
CREATE INDEX IF NOT EXISTS idx_transactions_merchant   ON transactions(merchant_id);
CREATE INDEX IF NOT EXISTS idx_transactions_occurred   ON transactions(occurred_at);
CREATE INDEX IF NOT EXISTS idx_transactions_created    ON transactions(created_at);
