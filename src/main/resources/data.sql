INSERT INTO users (name, role, password) VALUES
    ('alice', 'USER', 'test'),
    ('bob', 'SETTER', 'test'),
    ('klaus', 'OWNER', 'test'),
    ('crusher', 'ADMIN', 'test');

INSERT INTO gyms (name, street, city, email) VALUES
    ('Boulderwelt München Ost', 'Friedenstraße 22', 'München', 'muc-ost@boulderwelt.de'),
    ('Einstein Boulderhalle', 'Landsberger Straße 185', 'München', 'info@einstein-boulder.de'),
    ('BlocHütte Nürnberg', 'Fürther Straße 80', 'Nürnberg', 'nuernberg@blochuette.de');

INSERT INTO grades (name, v_scale, font_scale, description, gym_id) VALUES
    ('1', 'V0', '4', NULL, 1),
    ('2', 'V1', '5', NULL, 1),
    ('3', 'V2', '5+', NULL, 1),
    ('4', 'V3', '6a', 'Erste Hooks und Swing-Moves.', 1),
    ('5', 'V4', '6b', NULL, 1),
    ('6', 'V5', '6c', NULL, 1),
    ('7', 'V6', '7a', 'Crimp-Power über mehrere Moves.', 1),
    ('8', 'V7', '7b+', NULL, 1),
    ('1', 'V0', '4', NULL, 2),
    ('2', 'V1', '5', NULL, 2),
    ('3', 'V2', '5+', 'Erste weite Züge.', 2),
    ('4', 'V3', '6a', NULL, 2),
    ('5', 'V4', '6b', 'Crimps und kleinere Footholds.', 2),
    ('6', 'V5', '6c+', NULL, 2),
    ('7', 'V6', '7a+', NULL, 2),
    ('8', 'V7', '7b+', 'Power-Moves und Sloper mit viel Tention.', 2),
    ('1', 'V0', '4', 'Henkel-Moves zum Einsteigen.', 3),
    ('2', 'V1', '5', NULL, 3),
    ('3', 'V2', '5+', NULL, 3),
    ('4', 'V3', '6a+', 'Einführung in Toe- und Heelhooks.', 3),
    ('5', 'V4', '6b+', NULL, 3),
    ('6', 'V5', '6c+', 'Längere Boulder und schwierigere Dynos.', 3),
    ('7', 'V6', '7a+', NULL, 3),
    ('8', 'V7', '7b', 'Power-Moves mit maximaler Compression.', 3);

INSERT INTO sectors (name, description, image_path, gym_id) VALUES
    ('30er', 'Klassische Züge mit leichtem Überhang.', '/images/default-sector.svg', 1),
    ('Volldach', 'Steile Wand mit großen Zügen.', '/images/default-sector.svg', 1),
    ('Slab-City', 'Technische Platten und Balance-Probleme.', '/images/default-sector.svg', 2),
    ('45er', 'Starker Überhang und Campus-Style Moves.', '/images/default-sector.svg', 2),
    ('Tech-Deck', 'Schmale Leisten und Fußarbeitstraining.', '/images/default-sector.svg', 3),
    ('Höhle', 'Kompressionslastige Boulder in der Grotte.', '/images/default-sector.svg', 3);

INSERT INTO boulders (description, color, grade_id, sector_id) VALUES
    -- Boulders for sector 1 (30er, gym 1)
    ('Links hinten', 'YELLOW', 1, 1),
    ('Rechts vorne, steil', 'BLUE', 3, 1),
    ('Mittig, dynamisch', 'RED', 4, 1),
    ('Kante rechts', 'PINK', 2, 1),
    
    -- Boulders for sector 2 (Volldach, gym 1)
    ('Dach links, Power', 'BLACK', 6, 2),
    ('Campus rechts', 'BLUE', 7, 2),
    ('Überhang mittig', 'DARK_GREEN', 5, 2),
    ('Traversierung komplett', 'WHITE', 4, 2),
    
    -- Boulders for sector 3 (Slab-City, gym 2)
    ('Balance links', 'YELLOW', 9, 3),
    ('Platte rechts', 'WHITE', 10, 3),
    ('Technik pur', 'PINK', 11, 3),
    
    -- Boulders for sector 4 (45er, gym 2)
    ('Überhang extrem', 'BLACK', 14, 4),
    ('Power-Dynos', 'RED', 15, 4),
    ('Sloper Challenge', 'BLUE', 16, 4),
    ('Campus Board', 'DARK_GREEN', 13, 4),
    
    -- Boulders for sector 5 (Tech-Deck, gym 3)
    ('Crimp Heaven', 'YELLOW', 17, 5),
    ('Fußarbeit Spezial', 'PINK', 18, 5),
    ('Leisten links', 'WHITE', 19, 5),
    
    -- Boulders for sector 6 (Höhle, gym 3)
    ('Compression Crack', 'BLACK', 22, 6),
    ('Grotten-Traverse', 'BLUE', 23, 6),
    ('Power-Kompression', 'RED', 24, 6),
    ('Höhlen-Arete', 'DARK_GREEN', 21, 6);

INSERT INTO sessions (started_at, ended_at, user_id, gym_id) VALUES
    -- Past sessions for alice (user_id = 1)
    ('2024-12-01 18:00:00', '2024-12-01 20:30:00', 1, 1),
    ('2024-12-03 17:30:00', '2024-12-03 19:45:00', 1, 1),
    ('2024-12-05 16:00:00', '2024-12-05 18:15:00', 1, 2),

    -- Past sessions for bob (user_id = 2)
    ('2024-12-02 19:00:00', '2024-12-02 21:00:00', 2, 2),
    ('2024-12-04 18:00:00', '2024-12-04 20:00:00', 2, 3);

INSERT INTO goes (session_id, boulder_id, result, timestamp) VALUES
    -- Goes for alice's first session (session_id = 1, gym 1)
    (1, 1, 'FINISHED', '2024-12-01 18:15:00'),
    (1, 2, 'CLOSE_TRY', '2024-12-01 18:30:00'),
    (1, 3, 'DID_NOT_FINISH', '2024-12-01 18:45:00'),
    (1, 2, 'FINISHED', '2024-12-01 19:00:00'),
    (1, 4, 'FINISHED', '2024-12-01 19:20:00'),
    (1, 5, 'CLOSE_TRY', '2024-12-01 19:45:00'),
    (1, 3, 'CLOSE_TRY', '2024-12-01 20:00:00'),

    -- Goes for alice's second session (session_id = 2, gym 1)
    (2, 2, 'FINISHED', '2024-12-03 17:45:00'),
    (2, 5, 'FINISHED', '2024-12-03 18:00:00'),
    (2, 6, 'DID_NOT_FINISH', '2024-12-03 18:20:00'),
    (2, 7, 'CLOSE_TRY', '2024-12-03 18:40:00'),
    (2, 6, 'CLOSE_TRY', '2024-12-03 19:00:00'),
    (2, 8, 'DID_NOT_FINISH', '2024-12-03 19:30:00'),

    -- Goes for alice's third session (session_id = 3, gym 2)
    (3, 9, 'FINISHED', '2024-12-05 16:15:00'),
    (3, 10, 'FINISHED', '2024-12-05 16:30:00'),
    (3, 11, 'CLOSE_TRY', '2024-12-05 16:50:00'),
    (3, 12, 'DID_NOT_FINISH', '2024-12-05 17:10:00'),
    (3, 11, 'FINISHED', '2024-12-05 17:30:00'),
    (3, 13, 'CLOSE_TRY', '2024-12-05 17:50:00'),

    -- Goes for bob's first session (session_id = 4, gym 2)
    (4, 9, 'FINISHED', '2024-12-02 19:15:00'),
    (4, 11, 'FINISHED', '2024-12-02 19:35:00'),
    (4, 13, 'CLOSE_TRY', '2024-12-02 19:55:00'),
    (4, 14, 'DID_NOT_FINISH', '2024-12-02 20:15:00'),
    (4, 15, 'CLOSE_TRY', '2024-12-02 20:40:00'),

    -- Goes for bob's second session (session_id = 5, gym 3)
    (5, 17, 'FINISHED', '2024-12-04 18:20:00'),
    (5, 18, 'FINISHED', '2024-12-04 18:40:00'),
    (5, 19, 'CLOSE_TRY', '2024-12-04 19:00:00'),
    (5, 20, 'DID_NOT_FINISH', '2024-12-04 19:20:00'),
    (5, 21, 'CLOSE_TRY', '2024-12-04 19:45:00');
