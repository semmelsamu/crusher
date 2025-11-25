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
    ('1', 'V0', '4', 'Einfache Slab-Moves auf Reibung.', 1),
    ('2', 'V1', '5', 'Balance-Moves mit kleinen footholds.', 1),
    ('3', 'V2', '5+', 'Technische Kanten und Corners.', 1),
    ('4', 'V3', '6a', 'Erste Dynos und Swing-Moves.', 1),
    ('5', 'V4', '6b', 'Traverse mit Core-Spannung und Hooks.', 1),
    ('6', 'V5', '6c', 'Steile Sloper mit viel Core.', 1),
    ('7', 'V6', '7a', 'Crimp-Power über mehrere Moves.', 1),
    ('8', 'V7', '7b+', 'Lange Powerausdauer im Overhang.', 1),
    ('1', 'V0', '4', 'Slab zum Aufwärmen mit Reibung.', 2),
    ('2', 'V1', '5', 'Clean Smearing und Antreten.', 2),
    ('3', 'V2', '5+', 'Dynamische Starts mit Coordination.', 2),
    ('4', 'V3', '6a', 'Volume-Compression und Squeezes.', 2),
    ('5', 'V4', '6b', 'Crimp-Reihen für Fingerkraft.', 2),
    ('6', 'V5', '6c+', 'Roof mit Toe- und Heel-Hooks.', 2),
    ('7', 'V6', '7a+', 'Schulterlastige Compression-Moves.', 2),
    ('8', 'V7', '7b+', 'Testpiece mit Jump und Tension.', 2),
    ('1', 'V0', '4', 'Henkel-Moves zum Einsteigen.', 3),
    ('2', 'V1', '5', 'Nur Footwork und Balance.', 3),
    ('3', 'V2', '5+', 'Technische Corners und Reibung.', 3),
    ('4', 'V3', '6a+', 'Tufa-Compression mit Kneebars.', 3),
    ('5', 'V4', '6b+', 'Overhang mit Heelhooks und Slopern.', 3),
    ('6', 'V5', '6c+', 'Coordination-Jump auf Volumes.', 3),
    ('7', 'V6', '7a+', 'Crimp-Ladder für Ausdauer.', 3),
    ('8', 'V7', '7b', 'Harte Compression-Squeezes.', 3);

INSERT INTO sectors (name, description, image_path, gym_id) VALUES
    ('30er', 'Klassische Züge mit leichtem Überhang.', '/images/default-sector.svg', 1),
    ('Volldach', 'Steile Wand mit großen Zügen.', '/images/default-sector.svg', 1),
    ('Slab-City', 'Technische Platten und Balance-Probleme.', '/images/default-sector.svg', 2),
    ('45er', 'Starker Überhang und Campus-Style Moves.', '/images/default-sector.svg', 2),
    ('Tech-Deck', 'Schmale Leisten und Fußarbeitstraining.', '/images/default-sector.svg', 3),
    ('Höhle', 'Kompressionslastige Boulder in der Grotte.', '/images/default-sector.svg', 3);
