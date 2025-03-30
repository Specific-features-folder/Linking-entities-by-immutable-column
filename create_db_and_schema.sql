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
-- У таблиц не случайно 2 разных id. generated_id - синтетически сгенерированный id просто для сохранения в БД и удобной работы с записью через hibernate
-- id - является уникальным только в рамках подсистемы, а в разных подсистемах они могут повторяться.
-- Приходящие order_info также приходят из подсистем и ссылаются именно на order_handbook_info.id из своей подсистемы
CREATE TABLE test.order_handbook_info
(
    generated_id    BIGINT PRIMARY KEY,
    id              BIGINT,
    subsystem_name  VARCHAR(50),
    code            VARCHAR(254),
    sub_code        VARCHAR(254),
    marker          VARCHAR(254),
    description     VARCHAR(3000),
    order_type      VARCHAR(254),
    due_date_policy VARCHAR(500),
    constraint unique_order_handbook_info_id_subsystem_name unique (id, subsystem_name)
);

CREATE TABLE test.order_info
(
    generated_id      BIGINT PRIMARY KEY,
    id                BIGINT,
    problem_desc      VARCHAR(3000),
    creation_date     TIMESTAMPTZ,
    due_date          TIMESTAMPTZ,
    subsystem_name    VARCHAR(50),
    order_handbook_id BIGINT,
    constraint unique_order_info_id_subsystem_name unique (id, subsystem_name)
)