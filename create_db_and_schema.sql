-- TODO: ну хотелось бы всё же как-то с проверками создавать БД и схему
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
-- id - является уникальным только в рамках подсистемы, а в разных подсистемах они могут повторяться.
-- Приходящие order_info также приходят из подсистем и ссылаются именно на order_handbook_info.id из своей подсистемы
CREATE TABLE test.order_handbook_info
(
    id              BIGINT,
    subsystem_name  VARCHAR(50),
    code            VARCHAR(254),
    sub_code        VARCHAR(254),
    marker          VARCHAR(254),
    description     VARCHAR(3000),
    order_type      VARCHAR(254),
    due_date_policy VARCHAR(500),
    constraint pk_order_handbook_info_id_subsystem_name primary key (id, subsystem_name)
);

CREATE TABLE test.order_info
(
    id                BIGINT,
    problem_desc      VARCHAR(3000),
    creation_date     TIMESTAMPTZ,
    due_date          TIMESTAMPTZ,
    subsystem_name    VARCHAR(50),
    order_handbook_id BIGINT,
    constraint pk_order_info_id_subsystem_name primary key (id, subsystem_name)
)