-- Insertamos el rol por defecto si no existe
INSERT IGNORE INTO rol (id, nombre) VALUES (1, 'ROLE_CLIENTE');
INSERT IGNORE INTO rol (id, nombre) VALUES (2, 'ROLE_ADMIN');

-- (Opcional) Puedes insertar un usuario administrador por defecto aquí si quieres