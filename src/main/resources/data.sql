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
