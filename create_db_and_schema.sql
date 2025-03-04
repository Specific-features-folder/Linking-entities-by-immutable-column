-- TODO: ну хотелось бы всё же как-то с проверками создавать БД и схему или может быть с разных скриптах
-- DO $$ BEGIN
--     IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'linking_entities_by_immutable_column') THEN
--         CREATE DATABASE my_database;
--     END IF;
-- END $$;

CREATE DATABASE linking_entities_by_immutable_column;

-- Подключаемся к базе данных
\c linking_entities_by_immutable_column;

CREATE SCHEMA test;

-- DO $$
-- BEGIN
--     IF NOT EXISTS (SELECT FROM pg_namespace WHERE nspname = 'test') THEN
--         CREATE SCHEMA test;
--     END IF;
-- END $$