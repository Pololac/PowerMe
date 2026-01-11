-- ============================================
-- DEV SEED - POWERME
-- Charging Locations & Stations
-- ============================================

DELETE FROM charging_station;
DELETE FROM charging_location;
DELETE FROM address;
DELETE FROM users;

-- =========================
-- USER OWNER DEV
-- =========================

INSERT INTO users (id, email, password, firstname, lastname, is_activated)
VALUES (1,
        'owner@powerme.dev',
        '$2a$10$devpasswordhashxxxxxxxxxxxxxxxxxxxxxxxxxxxx',
        'Dev',
        'Owner',
        true);

INSERT INTO user_roles (user_id, role)
VALUES (1, 'ROLE_USER'),
       (1, 'ROLE_OWNER');

-- =========================
-- ADDRESSES
-- =========================

INSERT INTO address (id, street_address, city, postal_code, country)
VALUES (1, '10 rue de la République', 'Lyon', '69001', 'France'),
       (2, '5 place Bellecour', 'Lyon', '69002', 'France'),
       (3, '25 avenue Jean Jaurès', 'Lyon', '69007', 'France'),
       (4, '12 rue Garibaldi', 'Lyon', '69003', 'France'),
       (5, '8 cours Lafayette', 'Lyon', '69003', 'France'),
       (6, '3 quai Claude Bernard', 'Lyon', '69007', 'France'),
       (7, '20 rue de Marseille', 'Lyon', '69007', 'France'),
       (8, '15 avenue des Frères Lumière', 'Lyon', '69008', 'France');

-- =========================
-- CHARGING LOCATIONS
-- =========================

INSERT INTO charging_location (id, name, description,
                               latitude, longitude, location,
                               owner_id, address_id)
VALUES (1, 'Parking République', 'Parking public centre-ville',
        45.764043, 4.835659,
        ST_SetSRID(ST_MakePoint(4.835659, 45.764043), 4326)::geography,
        1, 1),

       (2, 'Borne Bellecour', 'Recharge rapide centre',
        45.757933, 4.832324,
        ST_SetSRID(ST_MakePoint(4.832324, 45.757933), 4326)::geography,
        1, 2),

       (3, 'Station Jean Jaurès', 'Station résidentielle',
        45.747425, 4.846122,
        ST_SetSRID(ST_MakePoint(4.846122, 45.747425), 4326)::geography,
        1, 3),

       (4, 'Parking Garibaldi', 'Recharge longue durée',
        45.760055, 4.861921,
        ST_SetSRID(ST_MakePoint(4.861921, 45.760055), 4326)::geography,
        1, 4),

       (5, 'Station Lafayette', 'Borne publique rapide',
        45.764811, 4.847204,
        ST_SetSRID(ST_MakePoint(4.847204, 45.764811), 4326)::geography,
        1, 5),

       (6, 'Quai Rhône Sud', 'Recharge vue sur Rhône',
        45.748901, 4.842558,
        ST_SetSRID(ST_MakePoint(4.842558, 45.748901), 4326)::geography,
        1, 6),

       (7, 'Station Guillotière', 'Recharge urbaine',
        45.750982, 4.851231,
        ST_SetSRID(ST_MakePoint(4.851231, 45.750982), 4326)::geography,
        1, 7),

       (8, 'Station Monplaisir', 'Recharge quartier résidentiel',
        45.746221, 4.871994,
        ST_SetSRID(ST_MakePoint(4.871994, 45.746221), 4326)::geography,
        1, 8);

-- =========================
-- CHARGING STATIONS
-- =========================

INSERT INTO charging_station (id, name, socket_type, power,
                              hourly_rate, active,
                              charging_location_id)
VALUES
-- =========================
-- ChargingLocation 1 (3 bornes)
-- =========================
(1,  'Borne A1', 'TYPE_2',  'AC_11',  3.50, true, 1),
(2,  'Borne A2', 'TYPE_2S', 'AC_7_4', 2.90, true, 1),
(3,  'Borne A3', 'TYPE_2',  'AC_22',  4.20, true, 1),

-- =========================
-- ChargingLocation 2 (2 bornes)
-- =========================
(4,  'Borne B1', 'TYPE_2',  'AC_11',  3.10, true, 2),
(5,  'Borne B2', 'TYPE_2S', 'AC_3_7', 1.80, true, 2),

-- =========================
-- ChargingLocation 3 (1 borne)
-- =========================
(6,  'Borne C1', 'TYPE_2',  'AC_7_4', 2.60, true, 3),

-- =========================
-- ChargingLocation 4 (1 borne) — CCS
-- =========================
(7,  'Borne D1', 'CCS',     'DC_50',  6.90, true, 4),

-- =========================
-- ChargingLocation 5 (1 borne) — CHAdeMO
-- =========================
(8,  'Borne E1', 'CHADEMO', 'DC_50',  6.70, true, 5),

-- =========================
-- ChargingLocation 6 (1 borne)
-- =========================
(9,  'Borne F1', 'TYPE_2',  'AC_11',  3.30, true, 6),

-- =========================
-- ChargingLocation 7 (1 borne)
-- =========================
(10, 'Borne G1', 'TYPE_2S', 'AC_7_4', 2.80, true, 7),

-- =========================
-- ChargingLocation 8 (1 borne)
-- =========================
(11, 'Borne H1', 'TYPE_2',  'AC_22',  4.50, true, 8);
