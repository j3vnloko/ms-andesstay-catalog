-- data.sql - Datos de ejemplo idempotentes (ON CONFLICT DO NOTHING)
-- Se ejecuta despues de schema.sql cuando spring.sql.init.mode=always

INSERT INTO units (id, name, type, capacity, price_per_night, available)
VALUES (1, 'Habitacion Estandar 101', 'HABITACION', 2, 35000.00, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO units (id, name, type, capacity, price_per_night, available)
VALUES (2, 'Habitacion Doble 202', 'HABITACION', 4, 55000.00, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO units (id, name, type, capacity, price_per_night, available)
VALUES (3, 'Cabana Lago Azul', 'CABANA', 6, 85000.00, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO units (id, name, type, capacity, price_per_night, available)
VALUES (4, 'Lodge Montaña Norte', 'LODGE', 8, 120000.00, false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO units (id, name, type, capacity, price_per_night, available)
VALUES (5, 'Cabana Bosque Sur', 'CABANA', 4, 72000.00, true)
ON CONFLICT (id) DO NOTHING;

