INSERT INTO users (name, role, password) VALUES
    ('alice', 'USER', 'test'),
    ('bob', 'SETTER', 'test'),
    ('klaus', 'OWNER', 'test'),
    ('crusher', 'ADMIN', 'test');

INSERT INTO gyms (name, street, city, email) VALUES
    ('Boulderwelt München Ost', 'Friedenstraße 22', 'München', 'muc-ost@boulderwelt.de'),
    ('Einstein Boulderhalle', 'Landsberger Straße 185', 'München', 'info@einstein-boulder.de'),
    ('BlocHütte Nürnberg', 'Fürther Straße 80', 'Nürnberg', 'nuernberg@blochuette.de');

INSERT INTO sectors (name, description, image_path, gym_id) VALUES
    ('Main Wall', 'Classic lines with varied angles.', '/images/default-sector.svg', 1),
    ('Roof Garden', 'Steep roofs with big moves.', '/images/default-sector.svg', 1),
    ('Slab City', 'Technical slabs and balance problems.', '/images/default-sector.svg', 2),
    ('Power Alley', 'Powerful overhangs and campus-style climbs.', '/images/default-sector.svg', 2),
    ('Tech Deck', 'Thin holds and footwork practice.', '/images/default-sector.svg', 3),
    ('Cave', 'Compression-heavy cave problems.', '/images/default-sector.svg', 3);
