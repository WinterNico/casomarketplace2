-- Insertamos el rol por defecto si no existe (la tabla se llama 'roles')
INSERT IGNORE INTO roles (id, nombre) VALUES (1, 'ROLE_CLIENTE');
INSERT IGNORE INTO roles (id, nombre) VALUES (2, 'ROLE_VENDEDOR');
INSERT IGNORE INTO roles (id, nombre) VALUES (3, 'ROLE_ADMIN');s