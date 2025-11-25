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
    ('1', 'V0', '4', 'Einfache Platten mit Reibung.', 1),
    ('2', 'V1', '5', 'Balance-Boulder mit kleinen Tritten.', 1),
    ('3', 'V2', '5+', 'Technische Kanten und Verschneidungen.', 1),
    ('4', 'V3', '6a', 'Erste Dynos und Körperschwung.', 1),
    ('5', 'V4', '6b', 'Traverse mit Spannung und Hooks.', 1),
    ('6', 'V5', '6c', 'Steile Sloper mit Körperspannung.', 1),
    ('7', 'V6', '7a', 'Leistenpower über mehrere Züge.', 1),
    ('8', 'V7', '7b+', 'Lange Powerausdauer im Überhang.', 1),
    ('1', 'V0', '4', 'Platten zum Aufwärmen auf Reibung.', 2),
    ('2', 'V1', '5', 'Sauberes Smearing und Antreten.', 2),
    ('3', 'V2', '5+', 'Dynamische Starts mit Koordination.', 2),
    ('4', 'V3', '6a', 'Volumen-Kompression und Squeezes.', 2),
    ('5', 'V4', '6b', 'Leistenreihen für Fingerkraft.', 2),
    ('6', 'V5', '6c+', 'Dachboulder mit Toe- und Heel-Hooks.', 2),
    ('7', 'V6', '7a+', 'Schulterlastige Kompressionszüge.', 2),
    ('8', 'V7', '7b+', 'Prüfboulder mit Sprung und Spannung.', 2),
    ('1', 'V0', '4', 'Henkel-Züge zum Einsteigen.', 3),
    ('2', 'V1', '5', 'Nur Fußarbeit und Gleichgewicht.', 3),
    ('3', 'V2', '5+', 'Technische Ecken und Reibung.', 3),
    ('4', 'V3', '6a+', 'Tufa-Kompression mit Kneebars.', 3),
    ('5', 'V4', '6b+', 'Überhang mit Heelhooks und Slopern.', 3),
    ('6', 'V5', '6c+', 'Koordinationssprung auf Volumen.', 3),
    ('7', 'V6', '7a+', 'Leistenleiter für Ausdauer.', 3),
    ('8', 'V7', '7b', 'Harte Kompression und Squeezes.', 3);

INSERT INTO sectors (name, description, image_path, gym_id) VALUES
    ('30er', 'Klassische Züge mit leichtem Überhang.', '/images/default-sector.svg', 1),
    ('Volldach', 'Steile Wand mit großen Zügen.', '/images/default-sector.svg', 1),
    ('Slab-City', 'Technische Platten und Balance-Probleme.', '/images/default-sector.svg', 2),
    ('45er', 'Starker Überhang und Campus-Style Moves.', '/images/default-sector.svg', 2),
    ('Tech-Deck', 'Schmale Leisten und Fußarbeitstraining.', '/images/default-sector.svg', 3),
    ('Höhle', 'Kompressionslastige Boulder in der Grotte.', '/images/default-sector.svg', 3);
