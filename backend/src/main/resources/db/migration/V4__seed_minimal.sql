-- demo user
INSERT INTO users (id, email, password_hash, full_name)
VALUES (
  gen_random_uuid(),
  'demo@local',
  crypt('changeme', gen_salt('bf')),
  'Demo User'
)
ON CONFLICT (email) DO NOTHING;

-- вземи id на demo user
WITH u AS (
  SELECT id FROM users WHERE email = 'demo@local'
),
h AS (
  INSERT INTO households (id, name)
  VALUES (gen_random_uuid(), 'My Household')
  RETURNING id
)
INSERT INTO household_members (user_id, household_id, role)
SELECT u.id, h.id, 'OWNER' FROM u, h;

-- създай account + базови категории
WITH hh AS (SELECT hm.household_id id FROM household_members hm JOIN users u ON hm.user_id = u.id WHERE u.email='demo@local' LIMIT 1)
INSERT INTO accounts (household_id, name, currency)
SELECT hh.id, 'Main Account', 'BGN' FROM hh;

WITH hh AS (SELECT hm.household_id id FROM household_members hm JOIN users u ON hm.user_id = u.id WHERE u.email='demo@local' LIMIT 1)
INSERT INTO categories (household_id, name, type)
SELECT hh.id, n, t
FROM hh,
     (VALUES ('Храна','EXPENSE'),
             ('Транспорт','EXPENSE'),
             ('Сметки','EXPENSE'),
             ('Заплата','INCOME')) AS c(n,t)
ON CONFLICT DO NOTHING;
