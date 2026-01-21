INSERT INTO users (name, email, role, password, deleted) VALUES
    ('alice', 'alice@crusher-test.de', 'USER', 'test', false),
    ('bob', 'bob@crusher-test.de', 'SETTER', 'test', false),
    ('klaus', 'klaus@crusher-test.de', 'OWNER', 'test', false),
    ('crusher', 'admin@crusher-test.de', 'ADMIN', 'test', false);

INSERT INTO gyms (name, street, city, email, latitude, longitude, crowd_level_url, deleted) VALUES
    ('Boulderwelt München Ost', 'Friedenstraße 22', 'München', 'muc-ost@boulderwelt.de', 48.12599371578731, 11.611093684742901, 'https://www.boulderwelt-muenchen-ost.de/', false),
    ('Einstein Boulderhalle', 'Landsberger Straße 185', 'München', 'info@einstein-boulder.de', 48.14053574359812, 11.522861113579506, NULL, false),
    ('BlocHütte Nürnberg', 'Fürther Straße 80', 'Nürnberg', 'nuernberg@blochuette.de', 49.45265787314971, 11.051280015562101, NULL, false),
    ('Boulderwelt Regensburg', 'Isarstraße 99', 'Regensburg', 'regensburg@boulderwelt.de', 49.032275086772316, 12.128859684768997, 'https://www.boulderwelt-regensburg.de/', false);

INSERT INTO grades (name, v_scale, font_scale, description, gym_id, deleted) VALUES
    ('1', 'V0', '4', NULL, 1, false),
    ('2', 'V1', '5', NULL, 1, false),
    ('3', 'V2', '5+', NULL, 1, false),
    ('4', 'V3', '6a', 'Erste Hooks und Schwungbewegungen.', 1, false),
    ('5', 'V4', '6b', NULL, 1, false),
    ('6', 'V5', '6c', NULL, 1, false),
    ('7', 'V6', '7a', 'Leistenkraft über mehrere Züge.', 1, false),
    ('8', 'V7', '7b+', NULL, 1, false),
    ('1', 'V0', '4', NULL, 2, false),
    ('2', 'V1', '5', NULL, 2, false),
    ('3', 'V2', '5+', 'Erste weite Züge.', 2, false),
    ('4', 'V3', '6a', NULL, 2, false),
    ('5', 'V4', '6b', 'Leisten und kleinere Tritte.', 2, false),
    ('6', 'V5', '6c+', NULL, 2, false),
    ('7', 'V6', '7a+', NULL, 2, false),
    ('8', 'V7', '7b+', 'Kraftzüge und Sloper mit viel Spannung.', 2, false),
    ('1', 'V0', '4', 'Große Henkel zum Einsteigen.', 3, false),
    ('2', 'V1', '5', NULL, 3, false),
    ('3', 'V2', '5+', NULL, 3, false),
    ('4', 'V3', '6a+', 'Einführung in Zehen- und Fersenhooks.', 3, false),
    ('5', 'V4', '6b+', NULL, 3, false),
    ('6', 'V5', '6c+', 'Längere Boulder und schwierigere Sprünge.', 3, false),
    ('7', 'V6', '7a+', NULL, 3, false),
    ('8', 'V7', '7b', 'Kraftzüge mit maximaler Kompression.', 3, false),
    ('1', 'V0', '4', 'Große Griffe, kurze Boulder.', 4, false),
    ('2', 'V1', '5', NULL, 4, false),
    ('3', 'V2', '5+', 'Erste Volumen und saubere Fußarbeit.', 4, false),
    ('4', 'V3', '6a', NULL, 4, false),
    ('5', 'V4', '6b', 'Stabile Leisten und präzise Tritte.', 4, false),
    ('6', 'V5', '6c', NULL, 4, false),
    ('7', 'V6', '7a', 'Kraft und Körperspannung über viele Züge.', 4, false),
    ('8', 'V7', '7b+', 'Lange Sequenzen mit schweren Zügen.', 4, false);

INSERT INTO sectors (name, description, image_path, gym_id, deleted) VALUES
    ('30er', 'Klassische Züge mit leichtem Überhang.', '/uploads/sectors/1/f9f1d556-c811-4352-b72e-4c67095d3351.png', 1, false),
    ('Volldach', 'Steile Wand mit großen Zügen.', '/uploads/sectors/2/a4e72d14-9cf0-421e-8f05-1975f3c724a9.png', 1, false),
    ('Plattenstadt', 'Technische Platten und Balanceaufgaben.', '/uploads/sectors/3/3d0e1dc4-9d5b-4470-ae75-fb41a9456a2d.png', 2, false),
    ('45er', 'Starker Überhang und dynamische Züge.', '/uploads/sectors/4/0b9e6cfa-873e-46d6-b42f-a958602d9ff3.png', 2, false),
    ('Technik-Deck', 'Schmale Leisten und Fußarbeitstraining.', '/uploads/sectors/5/c8f932c0-852a-4688-81f2-8b8e2671c2c4.png', 3, false),
    ('Höhle', 'Kompressionslastige Boulder in der Grotte.', '/uploads/sectors/6/5ae55a50-9088-4b06-841e-50bd76f2a027.png', 3, false),
    ('Panoramawand', 'Lange Platte mit Reibungstritten.', '/uploads/sectors/7/2c1a7b2a-7f3f-4e53-9c65-5d56a4d5c1e1.png', 4, false),
    ('Kraftkeller', 'Steiles Dach mit großen Zügen.', '/uploads/sectors/8/7b1a9c34-9e47-4c6b-9e6f-5d4c3a2b1e2f.png', 4, false),
    ('Trainingszone', 'Koordination, Leisten und Volumen.', '/uploads/sectors/9/5a8b7c6d-3e2f-4a1b-9c8d-7e6f5a4b3c2d.png', 4, false);

INSERT INTO boulders (description, color, grade_id, sector_id, holds_count, published, deleted) VALUES
    -- Boulder für Sektor 1 (30er, Halle 1)
    ('Links hinten', 'YELLOW', 1, 1, 18, true, false),
    ('Rechts vorne, steil', 'BLUE', 3, 1, 22, true, false),
    ('Mittig, dynamisch', 'RED', 4, 1, 20, true, false),
    ('Kante rechts', 'PINK', 2, 1, 16, true, false),

    -- Boulder für Sektor 2 (Volldach, Halle 1)
    ('Dach links, Kraft', 'BLACK', 6, 2, 25, true, false),
    ('Campus rechts', 'BLUE', 7, 2, 22, true, false),
    ('Überhang mittig', 'DARK_GREEN', 5, 2, 21, true, false),
    ('Traversierung komplett', 'WHITE', 4, 2, 30, true, false),

    -- Boulder für Sektor 3 (Plattenstadt, Halle 2)
    ('Balance links', 'YELLOW', 9, 3, 14, true, false),
    ('Platte rechts', 'WHITE', 10, 3, 12, true, false),
    ('Technik pur', 'PINK', 11, 3, 18, true, false),

    -- Boulder für Sektor 4 (45er, Halle 2)
    ('Überhang extrem', 'BLACK', 14, 4, 26, true, false),
    ('Dynamische Züge', 'RED', 15, 4, 20, true, false),
    ('Rutschige Griffe', 'BLUE', 16, 4, 19, true, false),
    ('Campus-Brett', 'DARK_GREEN', 13, 4, 24, true, false),

    -- Boulder für Sektor 5 (Technik-Deck, Halle 3)
    ('Leistenparadies', 'YELLOW', 17, 5, 18, true, false),
    ('Fußarbeit Spezial', 'PINK', 18, 5, 16, true, false),
    ('Leisten links', 'WHITE', 19, 5, 17, true, false),

    -- Boulder für Sektor 6 (Höhle, Halle 3)
    ('Kompressionsriss', 'BLACK', 22, 6, 23, true, false),
    ('Grotten-Traverse', 'BLUE', 23, 6, 28, true, false),
    ('Kraft-Kompression', 'RED', 24, 6, 24, true, false),
    ('Höhlenkante', 'DARK_GREEN', 21, 6, 21, true, false),

    -- Boulder für Sektor 7 (Panoramawand, Halle 4)
    ('Linke Platte', 'YELLOW', 25, 7, 14, true, false),
    ('Reibungslinie rechts', 'WHITE', 26, 7, 16, true, false),
    ('Kantenlauf', 'PINK', 27, 7, 18, true, false),
    ('Schmale Trittspur', 'BLUE', 28, 7, 20, true, false),

    -- Boulder für Sektor 8 (Kraftkeller, Halle 4)
    ('Dachkante links', 'BLACK', 30, 8, 24, true, false),
    ('Kompressionskamin', 'RED', 31, 8, 22, true, false),
    ('Weite Züge', 'DARK_GREEN', 29, 8, 23, true, false),
    ('Ausstieg am Riss', 'BLUE', 32, 8, 25, true, false),

    -- Boulder für Sektor 9 (Trainingszone, Halle 4)
    ('Koordinationssprung', 'YELLOW', 28, 9, 12, true, false),
    ('Leistenleiter', 'PINK', 29, 9, 16, true, false),
    ('Volumenfahrt', 'WHITE', 30, 9, 18, true, false),
    ('Kraftkreis', 'RED', 31, 9, 20, true, false);

INSERT INTO projects (user_id, boulder_id, created_at) VALUES
    -- Projekte von Alice (2 pro Halle)
    (1, 2, '2024-12-03 18:10:00'),   -- Halle 1
    (1, 3, '2024-12-03 18:20:00'),   -- Halle 1
    (1, 10, '2024-12-05 16:00:00'),  -- Halle 2
    (1, 11, '2024-12-05 16:10:00'),  -- Halle 2
    (1, 19, '2024-12-04 18:10:00'),  -- Halle 3
    (1, 20, '2024-12-04 18:25:00'),  -- Halle 3
    (1, 23, '2025-01-05 17:20:00'),  -- Halle 4
    (1, 29, '2025-01-05 18:05:00'),  -- Halle 4
    -- Projekte von Bob (2 pro Halle)
    (2, 4, '2024-12-02 19:05:00'),   -- Halle 1
    (2, 5, '2024-12-02 19:20:00'),   -- Halle 1
    (2, 13, '2024-12-02 19:50:00'),  -- Halle 2
    (2, 14, '2024-12-02 20:05:00'),  -- Halle 2
    (2, 17, '2024-12-04 18:05:00'),  -- Halle 3
    (2, 18, '2024-12-04 18:20:00'),  -- Halle 3
    (2, 24, '2025-01-14 18:05:00'),  -- Halle 4
    (2, 28, '2025-01-14 18:20:00'),  -- Halle 4
    -- Projekte von Klaus (2 pro Halle)
    (3, 1, '2024-12-01 17:45:00'),   -- Halle 1
    (3, 6, '2024-12-01 18:05:00'),   -- Halle 1
    (3, 12, '2024-12-03 16:45:00'),  -- Halle 2
    (3, 15, '2024-12-03 17:00:00'),  -- Halle 2
    (3, 21, '2024-12-06 18:15:00'),  -- Halle 3
    (3, 22, '2024-12-06 18:35:00'),  -- Halle 3
    (3, 26, '2025-01-20 18:10:00'),  -- Halle 4
    (3, 31, '2025-01-20 18:25:00'),  -- Halle 4
    -- Projekte von Crusher (2 pro Halle)
    (4, 7, '2024-12-02 18:15:00'),   -- Halle 1
    (4, 8, '2024-12-02 18:30:00'),   -- Halle 1
    (4, 9, '2024-12-04 17:15:00'),   -- Halle 2
    (4, 16, '2024-12-04 17:35:00'),  -- Halle 2
    (4, 18, '2024-12-07 18:05:00'),  -- Halle 3
    (4, 22, '2024-12-07 18:25:00'),  -- Halle 3
    (4, 30, '2025-01-22 18:05:00'),  -- Halle 4
    (4, 33, '2025-01-22 18:30:00');  -- Halle 4

INSERT INTO sessions (started_at, ended_at, user_id, gym_id) VALUES
    -- Vergangene Einheiten von Alice (user_id = 1) - 20 Einheiten für Seitennavigation
    ('2024-11-10 17:00:00', '2024-11-10 19:30:00', 1, 1),
    ('2024-11-12 18:00:00', '2024-11-12 20:15:00', 1, 2),
    ('2024-11-15 16:30:00', '2024-11-15 18:45:00', 1, 1),
    ('2024-11-18 17:30:00', '2024-11-18 19:30:00', 1, 3),
    ('2024-11-20 18:00:00', '2024-11-20 20:30:00', 1, 1),
    ('2024-11-22 16:00:00', '2024-11-22 18:00:00', 1, 2),
    ('2024-11-25 17:00:00', '2024-11-25 19:15:00', 1, 1),
    ('2024-11-27 18:30:00', '2024-11-27 20:45:00', 1, 3),
    ('2024-11-29 16:30:00', '2024-11-29 18:30:00', 1, 2),
    ('2024-12-01 18:00:00', '2024-12-01 20:30:00', 1, 1),
    ('2024-12-03 17:30:00', '2024-12-03 19:45:00', 1, 1),
    ('2024-12-05 16:00:00', '2024-12-05 18:15:00', 1, 2),
    ('2024-12-07 17:00:00', '2024-12-07 19:00:00', 1, 3),
    ('2024-12-09 18:00:00', '2024-12-09 20:30:00', 1, 1),
    ('2024-12-11 16:30:00', '2024-12-11 18:45:00', 1, 2),
    ('2024-12-13 17:30:00', '2024-12-13 19:30:00', 1, 1),
    ('2024-12-15 18:00:00', '2024-12-15 20:15:00', 1, 3),
    ('2024-12-17 16:00:00', '2024-12-17 18:30:00', 1, 2),
    ('2024-12-19 17:30:00', '2024-12-19 19:45:00', 1, 1),
    ('2024-12-21 18:00:00', '2024-12-21 20:00:00', 1, 3),

    -- Vergangene Einheiten von Bob (user_id = 2)
    ('2024-12-02 19:00:00', '2024-12-02 21:00:00', 2, 2),
    ('2024-12-04 18:00:00', '2024-12-04 20:00:00', 2, 3),

    -- Weitere Einheiten für alle Testnutzer
    ('2025-01-05 17:30:00', '2025-01-05 19:45:00', 1, 4),
    ('2025-01-12 18:00:00', '2025-01-12 20:10:00', 1, 2),
    ('2025-02-02 18:15:00', NULL, 1, 1),
    ('2025-01-06 19:00:00', '2025-01-06 21:00:00', 2, 1),
    ('2025-01-14 18:10:00', '2025-01-14 20:10:00', 2, 4),
    ('2025-02-05 18:40:00', '2025-02-05 20:00:00', 2, 2),
    ('2025-01-07 17:20:00', '2025-01-07 19:05:00', 3, 3),
    ('2025-01-20 18:00:00', '2025-01-20 20:30:00', 3, 4),
    ('2025-02-01 17:45:00', '2025-02-01 19:15:00', 3, 1),
    ('2025-01-08 18:30:00', '2025-01-08 20:15:00', 4, 2),
    ('2025-01-22 18:10:00', '2025-01-22 20:20:00', 4, 4),
    ('2025-02-03 19:00:00', '2025-02-03 21:00:00', 4, 3);

INSERT INTO goes (session_id, boulder_id, result, timestamp) VALUES
    -- Versuche für Alice am 01.12. (session_id = 10, Halle 1) - 50 Versuche für Seitennavigation
    (10, 1, 'FINISHED', '2024-12-01 18:15:00'),
    (10, 2, 'CLOSE_TRY', '2024-12-01 18:17:00'),
    (10, 3, 'DID_NOT_FINISH', '2024-12-01 18:19:00'),
    (10, 2, 'FINISHED', '2024-12-01 18:21:00'),
    (10, 4, 'FINISHED', '2024-12-01 18:23:00'),
    (10, 5, 'CLOSE_TRY', '2024-12-01 18:25:00'),
    (10, 3, 'CLOSE_TRY', '2024-12-01 18:27:00'),
    (10, 1, 'FINISHED', '2024-12-01 18:29:00'),
    (10, 6, 'DID_NOT_FINISH', '2024-12-01 18:31:00'),
    (10, 4, 'FINISHED', '2024-12-01 18:33:00'),
    (10, 7, 'CLOSE_TRY', '2024-12-01 18:35:00'),
    (10, 2, 'FINISHED', '2024-12-01 18:37:00'),
    (10, 8, 'DID_NOT_FINISH', '2024-12-01 18:39:00'),
    (10, 5, 'CLOSE_TRY', '2024-12-01 18:41:00'),
    (10, 3, 'FINISHED', '2024-12-01 18:43:00'),
    (10, 6, 'CLOSE_TRY', '2024-12-01 18:45:00'),
    (10, 1, 'FINISHED', '2024-12-01 18:47:00'),
    (10, 7, 'DID_NOT_FINISH', '2024-12-01 18:49:00'),
    (10, 4, 'FINISHED', '2024-12-01 18:51:00'),
    (10, 8, 'CLOSE_TRY', '2024-12-01 18:53:00'),
    (10, 2, 'FINISHED', '2024-12-01 18:55:00'),
    (10, 5, 'DID_NOT_FINISH', '2024-12-01 18:57:00'),
    (10, 6, 'FINISHED', '2024-12-01 18:59:00'),
    (10, 3, 'CLOSE_TRY', '2024-12-01 19:01:00'),
    (10, 7, 'FINISHED', '2024-12-01 19:03:00'),
    (10, 1, 'FINISHED', '2024-12-01 19:05:00'),
    (10, 8, 'CLOSE_TRY', '2024-12-01 19:07:00'),
    (10, 4, 'DID_NOT_FINISH', '2024-12-01 19:09:00'),
    (10, 2, 'FINISHED', '2024-12-01 19:11:00'),
    (10, 5, 'CLOSE_TRY', '2024-12-01 19:13:00'),
    (10, 6, 'FINISHED', '2024-12-01 19:15:00'),
    (10, 7, 'DID_NOT_FINISH', '2024-12-01 19:17:00'),
    (10, 3, 'FINISHED', '2024-12-01 19:19:00'),
    (10, 8, 'CLOSE_TRY', '2024-12-01 19:21:00'),
    (10, 1, 'FINISHED', '2024-12-01 19:23:00'),
    (10, 4, 'CLOSE_TRY', '2024-12-01 19:25:00'),
    (10, 2, 'FINISHED', '2024-12-01 19:27:00'),
    (10, 5, 'DID_NOT_FINISH', '2024-12-01 19:29:00'),
    (10, 6, 'FINISHED', '2024-12-01 19:31:00'),
    (10, 7, 'CLOSE_TRY', '2024-12-01 19:33:00'),
    (10, 3, 'FINISHED', '2024-12-01 19:35:00'),
    (10, 8, 'DID_NOT_FINISH', '2024-12-01 19:37:00'),
    (10, 1, 'FINISHED', '2024-12-01 19:39:00'),
    (10, 4, 'CLOSE_TRY', '2024-12-01 19:41:00'),
    (10, 2, 'FINISHED', '2024-12-01 19:43:00'),
    (10, 5, 'CLOSE_TRY', '2024-12-01 19:45:00'),
    (10, 6, 'FINISHED', '2024-12-01 19:47:00'),
    (10, 7, 'DID_NOT_FINISH', '2024-12-01 19:49:00'),
    (10, 3, 'FINISHED', '2024-12-01 19:51:00'),
    (10, 8, 'CLOSE_TRY', '2024-12-01 19:53:00'),

    -- Versuche für Alice am 03.12. (session_id = 11, Halle 1)
    (11, 2, 'FINISHED', '2024-12-03 17:45:00'),
    (11, 5, 'FINISHED', '2024-12-03 18:00:00'),
    (11, 6, 'DID_NOT_FINISH', '2024-12-03 18:20:00'),
    (11, 7, 'CLOSE_TRY', '2024-12-03 18:40:00'),
    (11, 6, 'CLOSE_TRY', '2024-12-03 19:00:00'),
    (11, 8, 'DID_NOT_FINISH', '2024-12-03 19:30:00'),

    -- Versuche für Alice am 05.12. (session_id = 12, Halle 2)
    (12, 9, 'FINISHED', '2024-12-05 16:15:00'),
    (12, 10, 'FINISHED', '2024-12-05 16:30:00'),
    (12, 11, 'CLOSE_TRY', '2024-12-05 16:50:00'),
    (12, 12, 'DID_NOT_FINISH', '2024-12-05 17:10:00'),
    (12, 11, 'FINISHED', '2024-12-05 17:30:00'),
    (12, 13, 'CLOSE_TRY', '2024-12-05 17:50:00'),

    -- Versuche für Bob am 02.12. (session_id = 21, Halle 2)
    (21, 9, 'FINISHED', '2024-12-02 19:15:00'),
    (21, 11, 'FINISHED', '2024-12-02 19:35:00'),
    (21, 13, 'CLOSE_TRY', '2024-12-02 19:55:00'),
    (21, 14, 'DID_NOT_FINISH', '2024-12-02 20:15:00'),
    (21, 15, 'CLOSE_TRY', '2024-12-02 20:40:00'),

    -- Versuche für Bob am 04.12. (session_id = 22, Halle 3)
    (22, 17, 'FINISHED', '2024-12-04 18:20:00'),
    (22, 18, 'FINISHED', '2024-12-04 18:40:00'),
    (22, 19, 'CLOSE_TRY', '2024-12-04 19:00:00'),
    (22, 20, 'DID_NOT_FINISH', '2024-12-04 19:20:00'),
    (22, 21, 'CLOSE_TRY', '2024-12-04 19:45:00');

INSERT INTO goes (session_id, boulder_id, result, timestamp, progressed_hold) VALUES
    -- Weitere Versuche mit Fortschrittsangabe
    (23, 23, 'FINISHED', '2025-01-05 17:45:00', NULL),
    (23, 24, 'CLOSE_TRY', '2025-01-05 18:00:00', 14),
    (23, 27, 'DID_NOT_FINISH', '2025-01-05 18:15:00', 12),
    (23, 27, 'CLOSE_TRY', '2025-01-05 18:30:00', 18),
    (23, 31, 'FINISHED', '2025-01-05 18:50:00', NULL),
    (23, 29, 'CLOSE_TRY', '2025-01-05 19:10:00', 15),
    (24, 10, 'FINISHED', '2025-01-12 18:20:00', NULL),
    (24, 12, 'DID_NOT_FINISH', '2025-01-12 18:45:00', 10),
    (24, 13, 'CLOSE_TRY', '2025-01-12 19:05:00', 15),
    (24, 14, 'FINISHED', '2025-01-12 19:25:00', NULL),
    (24, 15, 'CLOSE_TRY', '2025-01-12 19:45:00', 18),
    (25, 1, 'FINISHED', '2025-02-02 18:25:00', NULL),
    (25, 2, 'CLOSE_TRY', '2025-02-02 18:40:00', 15),
    (25, 5, 'DID_NOT_FINISH', '2025-02-02 18:55:00', 12),
    (25, 7, 'CLOSE_TRY', '2025-02-02 19:10:00', 16),
    (25, 3, 'FINISHED', '2025-02-02 19:30:00', NULL),
    (25, 8, 'DID_NOT_FINISH', '2025-02-02 19:45:00', 20),
    (26, 4, 'FINISHED', '2025-01-06 19:20:00', NULL),
    (26, 6, 'CLOSE_TRY', '2025-01-06 19:40:00', 14),
    (26, 5, 'DID_NOT_FINISH', '2025-01-06 20:05:00', 11),
    (26, 7, 'FINISHED', '2025-01-06 20:30:00', NULL),
    (27, 23, 'FINISHED', '2025-01-14 18:25:00', NULL),
    (27, 28, 'CLOSE_TRY', '2025-01-14 18:50:00', 16),
    (27, 29, 'DID_NOT_FINISH', '2025-01-14 19:10:00', 13),
    (27, 30, 'CLOSE_TRY', '2025-01-14 19:30:00', 17),
    (27, 33, 'FINISHED', '2025-01-14 19:55:00', NULL),
    (28, 9, 'FINISHED', '2025-02-05 18:50:00', NULL),
    (28, 12, 'CLOSE_TRY', '2025-02-05 19:10:00', 12),
    (28, 14, 'FINISHED', '2025-02-05 19:35:00', NULL),
    (28, 15, 'DID_NOT_FINISH', '2025-02-05 19:55:00', 16),
    (29, 16, 'FINISHED', '2025-01-07 17:35:00', NULL),
    (29, 19, 'CLOSE_TRY', '2025-01-07 18:00:00', 14),
    (29, 21, 'DID_NOT_FINISH', '2025-01-07 18:20:00', 12),
    (29, 22, 'FINISHED', '2025-01-07 18:50:00', NULL),
    (30, 24, 'FINISHED', '2025-01-20 18:25:00', NULL),
    (30, 27, 'CLOSE_TRY', '2025-01-20 18:50:00', 16),
    (30, 28, 'CLOSE_TRY', '2025-01-20 19:10:00', 14),
    (30, 31, 'FINISHED', '2025-01-20 19:30:00', NULL),
    (30, 34, 'DID_NOT_FINISH', '2025-01-20 19:55:00', 15),
    (31, 2, 'FINISHED', '2025-02-01 18:05:00', NULL),
    (31, 5, 'CLOSE_TRY', '2025-02-01 18:30:00', 17),
    (31, 6, 'DID_NOT_FINISH', '2025-02-01 18:55:00', 12),
    (31, 8, 'FINISHED', '2025-02-01 19:20:00', NULL),
    (32, 10, 'FINISHED', '2025-01-08 18:50:00', NULL),
    (32, 11, 'CLOSE_TRY', '2025-01-08 19:10:00', 12),
    (32, 13, 'DID_NOT_FINISH', '2025-01-08 19:30:00', 11),
    (32, 14, 'FINISHED', '2025-01-08 19:55:00', NULL),
    (33, 23, 'FINISHED', '2025-01-22 18:30:00', NULL),
    (33, 26, 'CLOSE_TRY', '2025-01-22 18:55:00', 14),
    (33, 27, 'DID_NOT_FINISH', '2025-01-22 19:15:00', 15),
    (33, 30, 'CLOSE_TRY', '2025-01-22 19:40:00', 18),
    (33, 34, 'FINISHED', '2025-01-22 20:05:00', NULL),
    (34, 18, 'FINISHED', '2025-02-03 19:20:00', NULL),
    (34, 19, 'CLOSE_TRY', '2025-02-03 19:40:00', 16),
    (34, 20, 'DID_NOT_FINISH', '2025-02-03 20:00:00', 18),
    (34, 22, 'FINISHED', '2025-02-03 20:30:00', NULL);

INSERT INTO boulder_comments (user_id, boulder_id, comment, created_at) VALUES
    -- Kommentare zu Boulder 1 (Links hinten)
    (1, 1, 'Starker Boulder, guter Einstieg.', '2024-12-01 20:35:00'),
    (2, 1, 'Mag die Linie sehr.', '2024-12-02 18:15:00'),
    (3, 1, 'Kleine Leisten, macht Spaß.', '2024-12-02 19:30:00'),
    (4, 1, 'Perfekt zum Aufwärmen.', '2024-12-03 17:20:00'),
    (1, 1, 'Im zweiten Versuch geschafft.', '2024-12-03 20:00:00'),
    (2, 1, 'Schöner Bewegungsfluss.', '2024-12-04 18:45:00');

INSERT INTO boulder_comments (user_id, boulder_id, comment, created_at, updated_at) VALUES
    (1, 5, 'Dachzug fühlt sich schwerer an als der Grad.', '2025-01-05 20:10:00', '2025-01-06 09:15:00'),
    (2, 12, 'Sehr kräftig, gute Griffe am Ausstieg.', '2025-01-06 21:05:00', NULL),
    (3, 19, 'Kompression ist knackig, lohnt sich.', '2025-01-07 19:50:00', NULL),
    (4, 27, 'Starker Dachboulder, guter Bewegungsfluss.', '2025-01-22 20:40:00', NULL),
    (1, 23, 'Sauberer Plattenboulder, macht Spass.', '2025-01-05 18:55:00', NULL),
    (2, 30, 'Der Ausstieg ist fies, aber fair.', '2025-01-14 20:10:00', NULL),
    (3, 33, 'Volumen sind spannend gesetzt.', '2025-01-20 20:05:00', NULL),
    (4, 16, 'Leistenparadies trifft es gut.', '2025-01-08 21:10:00', NULL);

INSERT INTO notices (title, message, creation_date, gym_id, deleted) VALUES
    -- Hinweise für Boulderwelt München Ost (Halle 1)
    ('Neue Boulder gesetzt', 'Wir haben 15 neue Boulder im Sektor Volldach geschraubt. Es gibt Probleme von V2 bis V8 mit Fokus auf Technik und Balance.', '2024-12-15 10:00:00', 1, FALSE),
    ('Weihnachtsöffnungszeiten', 'Bitte beachtet unsere Sonderöffnungszeiten über die Feiertage. 24.-26.12.: geschlossen. 27.-30.12.: 10:00-20:00. 31.12.: 10:00-18:00. 01.01.: geschlossen. Ab 02.01. wieder regulär.', '2024-12-10 14:30:00', 1, FALSE),
    ('Anmeldung Jugendwettkampf', 'Die Anmeldung für den Jugendwettkampf am 20.01. ist geöffnet. Für Kletternde von 8-16 Jahren. Anmeldung an der Theke oder per Mail, Plätze sind begrenzt.', '2024-12-08 09:15:00', 1, FALSE),
    ('Januar-Neuschrauben abgeschlossen', 'Alle Sektoren wurden neu geschraubt. Über 80 neue Boulder von V0 bis V9, diesmal mit Fokus auf dynamische Züge und Technik.', '2026-01-28 11:00:00', 1, FALSE),
    ('Wartungstag Ausrüstung', 'Am 30.01. führen wir Wartungen an Matten und Griffen durch. Die Halle bleibt geöffnet, einzelne Bereiche können kurzzeitig gesperrt sein.', '2026-01-27 14:20:00', 1, FALSE),
    ('Neumitglieder-Aktion', 'Im Januar gibt es 20 Prozent Rabatt auf den ersten Monat. Gültig bis 31.01. Ideal für den Einstieg oder Wiedereinstieg.', '2026-01-25 09:45:00', 1, FALSE),
    ('Hohe Auslastung am Wochenende', 'An den Wochenenden im Januar rechnen wir mit viel Andrang. Wer es ruhiger mag, kommt am besten unter der Woche nach 20:00.', '2026-01-26 16:30:00', 1, FALSE),
    ('Rückmeldung Setter-Kurs', 'Danke für eure Rückmeldung zum Setter-Kurs. Wir planen für Februar weitere Techniktermine. Infos folgen.', '2026-01-29 10:15:00', 1, FALSE),

    -- Hinweise für Einstein Boulderhalle (Halle 2)
    ('Wartungshinweis', 'Der Sektor Plattenstadt ist vom 18.-20.12. wegen Wartung und Neuschrauben geschlossen. Alle anderen Bereiche bleiben regulär geöffnet.', '2024-12-12 16:45:00', 2, FALSE),
    ('Anfängerkurs startet', 'Der nächste Anfängerkurs startet am 08.01. In vier Wochen lernt ihr Technik, Sicherheit und Grundlagen. Anmeldung online oder an der Theke.', '2024-12-11 11:20:00', 2, FALSE),
    ('Weihnachtsfeier am 22.12.', 'Unsere Weihnachtsfeier findet am 22.12. von 18:00-22:00 statt. Mit Snacks, Getränken, Spielen und kleinen Preisen. Gäste mit Tageskarte willkommen.', '2024-12-05 15:00:00', 2, FALSE),

    -- Hinweise für BlocHütte Nürnberg (Halle 3)
    ('Winter-Challenge gestartet', 'Unsere Winter-Challenge läuft. Schafft 50 Boulder in verschiedenen Graden bis 28.02. und gewinnt Preise. Fortschritt am Board im Eingangsbereich.', '2024-12-14 08:30:00', 3, FALSE),
    ('Neue Yoga-Kurse', 'Dienstag und Donnerstag um 19:00 gibt es kletterspezifisches Yoga. Fokus auf Beweglichkeit und Verletzungsprävention. Erste Stunde gratis.', '2024-12-09 13:00:00', 3, FALSE),
    ('Parkhinweis', 'Bitte nur auf den ausgewiesenen Parkflächen der Halle parken. Fahrzeuge auf Nachbarflächen können abgeschleppt werden.', '2024-12-01 10:00:00', 3, FALSE),

    -- Hinweise für Boulderwelt Regensburg (Halle 4)
    ('Live-Auslastung verfügbar', 'Die aktuelle Auslastung ist jetzt in der App sichtbar. So findet ihr leichter ruhige Zeiten.', '2024-12-16 12:00:00', 4, FALSE),
    ('Frische Boulder im Überhang', 'Im Überhangbereich sind 20 neue Boulder geschraubt. Von V3 bis V8 mit kreativen Bewegungen.', '2024-12-13 09:30:00', 4, FALSE),
    ('Silvester-Öffnungszeiten', 'Am 31.12. sind wir von 10:00-16:00 geöffnet. Am 01.01. geschlossen, ab 02.01. wieder regulär.', '2024-12-10 15:00:00', 4, FALSE);

INSERT INTO events (title, description, periodic, weekday, event_date, time, recurrence_frequency, created_at, gym_id, deleted) VALUES
    -- Events für Boulderwelt München Ost (Halle 1)
    ('Gemeinschafts-Kletterabend', 'Wöchentlicher Gemeinschaftsabend mit lockerem Format und kleinen Preisen. Alle Niveaus willkommen.', TRUE, 'THURSDAY', NULL, '18:30-21:00', 'WEEKLY', '2024-11-28 12:00:00', 1, FALSE),
    ('Technik-Kurs', 'Zwei Stunden Fokus auf Fussarbeit und Balance. Maximal 16 Plätze, Anmeldung an der Theke.', FALSE, NULL, '2026-01-25', '17:00-19:00', NULL, '2024-11-20 09:00:00', 1, FALSE),
    ('Freitagabend-Treff', 'Entspannter Kletterabend mit Snacks und Getränken. Für alle Mitglieder.', TRUE, 'FRIDAY', NULL, '19:00-22:00', 'WEEKLY', '2026-01-24 14:00:00', 1, FALSE),
    ('Fortgeschrittenen-Trainingscamp', 'Dreitägiges Camp für Fortgeschrittene mit Kraft, Technik und Mentaltraining.', FALSE, NULL, '2026-02-15', '10:00-16:00', NULL, '2024-11-22 10:30:00', 1, FALSE),
    ('Einsteiger-Morgensession', 'Für Neulinge: Grundlagen, Sicherheit und erste Züge. Jede zweite Woche am Samstag.', TRUE, 'SATURDAY', NULL, '10:00-12:00', 'BI_WEEKLY', '2026-11-15 08:00:00', 1, FALSE),

    -- Events für Einstein Boulderhalle (Halle 2)
    ('Winter-Boulderabend', 'Feiert die Saison mit Musik, Spielen und Mannschafts-Staffel. Für Mitglieder kostenlos, Gäste mit Tageskarte.', FALSE, NULL, '2026-12-22', '18:00-22:00', NULL, '2024-11-25 10:00:00', 2, FALSE),
    ('Bouldern für Einsteiger', 'Einsteigerfreundliche Einheit zu Sicherheit, Basics und Hallenregeln. Keine Vorkenntnisse nötig.', TRUE, 'MONDAY', NULL, '19:00-20:30', 'BI_WEEKLY', '2024-11-18 08:30:00', 2, FALSE),
    ('Griffkraft-Kurs', 'Kurztraining zu Griffkraft, Griffbrett und Regeneration.', FALSE, NULL, '2026-02-05', '19:00-21:00', NULL, '2025-01-03 09:00:00', 2, FALSE),

    -- Events für BlocHütte Nürnberg (Halle 3)
    ('Frauen-Klettertreff', 'Offener Treff für Frauen zum gemeinsamen Klettern. Lockeres Format mit Trainer vor Ort.', TRUE, 'WEDNESDAY', NULL, '19:00-21:00', 'WEEKLY', '2024-11-13 15:00:00', 3, FALSE),
    ('Jugend-Trainingstag', 'Tageskurs für 10-15 Jahre mit Bewegungsschulung und Aufwärmprogramm.', FALSE, NULL, '2026-12-07', '10:00-13:00', NULL, '2024-11-10 09:30:00', 3, FALSE),
    ('Familienvormittag', 'Monatlicher Vormittag für Familien mit leichten Boulderstationen.', TRUE, 'SUNDAY', NULL, '10:00-12:00', 'MONTHLY', '2024-11-12 10:00:00', 3, FALSE),

    -- Events für Boulderwelt Regensburg (Halle 4)
    ('Setter-Fragerunde', 'Trefft die Setter, stellt Fragen und bekommt Einblicke in den neuen Überhangset.', FALSE, NULL, '2026-01-14', '19:30-20:30', NULL, '2024-11-29 14:00:00', 4, FALSE),
    ('Neujahrs-Aufwärmen', 'Geführtes Aufwärmen und leichter Zirkel zum Jahresstart.', FALSE, NULL, '2026-01-04', '11:00-12:00', NULL, '2024-11-30 11:00:00', 4, FALSE),
    ('Monatlicher Gemeinschaftstreff', 'Monatlicher Treff zum Austausch, Klettern und Tipps teilen.', TRUE, 'TUESDAY', NULL, '19:00-21:00', 'MONTHLY', '2024-11-05 18:00:00', 4, FALSE),
    ('Einsteigerkurs kompakt', 'Kompakter Einsteigerkurs mit Technik, Sicherheit und Hallenablauf.', FALSE, NULL, '2026-02-10', '18:00-20:30', NULL, '2025-01-08 12:00:00', 4, FALSE);

INSERT INTO gym_comments (user_id, gym_id, comment, created_at, updated_at) VALUES
    (1, 1, 'Viel Platz und gute Lüftung, auch abends in Ordnung.', '2025-01-03 09:10:00', NULL),
    (2, 1, 'Der Überhang ist super gesetzt, viel Abwechslung.', '2025-01-04 10:30:00', '2025-01-04 12:00:00'),
    (3, 2, 'Plattenbereich macht Spass, aber abends recht voll.', '2025-01-07 18:10:00', NULL),
    (4, 2, 'Guter Mix aus Technik und Kraftbouldern.', '2025-01-08 20:00:00', NULL),
    (1, 3, 'Die Höhle ist mein Highlight, starke Kompressionen.', '2025-01-09 19:20:00', NULL),
    (2, 3, 'Freundliche Mitarbeitende und saubere Halle.', '2025-01-10 17:45:00', NULL),
    (3, 4, 'Neue Sektoren gefallen mir, gute Platten.', '2025-01-11 08:45:00', NULL),
    (4, 4, 'Trainingszone ist top für Leisten.', '2025-01-12 17:05:00', '2025-01-13 08:20:00');

INSERT INTO event_comments (user_id, event_id, comment, created_at, updated_at) VALUES
    (1, 1, 'Gute Stimmung und faire Gruppen.', '2025-01-02 20:00:00', NULL),
    (2, 1, 'Die Runde war voll, aber gut organisiert.', '2025-01-03 19:10:00', '2025-01-04 09:00:00'),
    (3, 6, 'Schöne Musik und lockere Atmosphäre.', '2025-01-05 22:15:00', NULL),
    (4, 7, 'Für Einsteiger super erklärt.', '2025-01-06 20:45:00', NULL),
    (1, 9, 'Gute Anleitung, gerne wieder.', '2025-01-07 21:05:00', NULL),
    (2, 12, 'Spannende Einblicke in den Setprozess.', '2025-01-08 20:20:00', NULL),
    (3, 14, 'Gute Mischung aus Austausch und Klettern.', '2025-01-09 20:30:00', NULL),
    (4, 15, 'Kompakter Kurs, genau richtig.', '2025-01-10 12:15:00', NULL);

INSERT INTO gym_ratings (user_id, gym_id, rating) VALUES
    (1, 1, 5),
    (1, 2, 4),
    (1, 3, 4),
    (1, 4, 5),
    (2, 1, 4),
    (2, 2, 5),
    (2, 3, 3),
    (2, 4, 4),
    (3, 1, 5),
    (3, 2, 4),
    (3, 3, 4),
    (3, 4, 4),
    (4, 1, 4),
    (4, 2, 4),
    (4, 3, 5),
    (4, 4, 5);

INSERT INTO boulder_ratings (user_id, boulder_id, rating) VALUES
    (1, 1, 5),
    (1, 5, 4),
    (1, 10, 4),
    (1, 14, 3),
    (1, 19, 5),
    (1, 23, 4),
    (1, 31, 3),
    (2, 2, 4),
    (2, 6, 5),
    (2, 11, 3),
    (2, 12, 4),
    (2, 20, 4),
    (2, 27, 5),
    (2, 32, 3),
    (3, 3, 4),
    (3, 7, 3),
    (3, 9, 4),
    (3, 15, 5),
    (3, 18, 3),
    (3, 24, 4),
    (3, 33, 5),
    (4, 4, 4),
    (4, 8, 3),
    (4, 13, 5),
    (4, 16, 4),
    (4, 21, 4),
    (4, 30, 5),
    (4, 34, 4);

INSERT INTO event_ratings (user_id, event_id, rating) VALUES
    (1, 1, 5),
    (1, 2, 4),
    (1, 12, 5),
    (2, 6, 4),
    (2, 7, 3),
    (2, 8, 4),
    (3, 9, 5),
    (3, 11, 4),
    (4, 14, 4),
    (4, 15, 5);
