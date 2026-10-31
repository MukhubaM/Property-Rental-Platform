-- V3__update_roles_id_to_bigint.sql
-- Change roles.id from SMALLINT to BIGINT to prevent overflow and align with Java Long mapping.

ALTER TABLE user_roles
DROP CONSTRAINT fk_user_roles_role;

ALTER TABLE roles
ALTER COLUMN id TYPE BIGINT;

ALTER TABLE user_roles
ALTER COLUMN role_id TYPE BIGINT;

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT;