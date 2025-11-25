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
    ('1', 'V0', '4', 'Warm-up slab lines.', 1),
    ('2', 'V1', '5', 'Beginner balance climbs.', 1),
    ('3', 'V2', '5+', 'Technical aretes and corners.', 1),
    ('4', 'V3', '6a', 'Intro to power moves.', 1),
    ('5', 'V4', '6b', 'Core tension traverses.', 1),
    ('6', 'V5', '6c', 'Steep tension problems.', 1),
    ('7', 'V6', '7a', 'Crimpy endurance lines.', 1),
    ('8', 'V7', '7b+', 'Power endurance challenge.', 1),
    ('1', 'V0', '4', 'Smooth slab warm-ups.', 2),
    ('2', 'V1', '5', 'Delicate smearing practice.', 2),
    ('3', 'V2', '5+', 'Dynamic starts and light coordination.', 2),
    ('4', 'V3', '6a', 'Compression on volumes.', 2),
    ('5', 'V4', '6b', 'Finger strength ladders.', 2),
    ('6', 'V5', '6c+', 'Roof toe hooks.', 2),
    ('7', 'V6', '7a+', 'Shoulder-intensive problems.', 2),
    ('8', 'V7', '7b+', 'Test-piece in the cave.', 2),
    ('1', 'V0', '4', 'Warm-up jug circuits.', 3),
    ('2', 'V1', '5', 'Footwork-only boulders.', 3),
    ('3', 'V2', '5+', 'Technical corners.', 3),
    ('4', 'V3', '6a+', 'Compression on tufas.', 3),
    ('5', 'V4', '6b+', 'Overhang with heel hooks.', 3),
    ('6', 'V5', '6c+', 'Coordination jumps.', 3),
    ('7', 'V6', '7a+', 'Crimp ladders.', 3),
    ('8', 'V7', '7b', 'Powerful compression tests.', 3);

INSERT INTO sectors (name, description, image_path, gym_id) VALUES
    ('30er', 'Klassische Züge mit leichtem Überhang.', '/images/default-sector.svg', 1),
    ('Volldach', 'Steile Wand mit großen Zügen.', '/images/default-sector.svg', 1),
    ('Slab-City', 'Technische Platten und Balance-Probleme.', '/images/default-sector.svg', 2),
    ('45er', 'Starker Überhang und Campus-Style Moves.', '/images/default-sector.svg', 2),
    ('Tech-Deck', 'Schmale Leisten und Fußarbeitstraining.', '/images/default-sector.svg', 3),
    ('Höhle', 'Kompressionslastige Boulder in der Grotte.', '/images/default-sector.svg', 3);
