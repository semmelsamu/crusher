INSERT INTO users (name, role, password) VALUES
    ('alice', 'USER', 'test'),
    ('bob', 'SETTER', 'test'),
    ('klaus', 'OWNER', 'test'),
    ('crusher', 'ADMIN', 'test');

INSERT INTO gyms (name, street, city, email, crowd_level_url, deleted) VALUES
    ('Boulderwelt München Ost', 'Friedenstraße 22', 'München', 'muc-ost@boulderwelt.de', 'https://www.boulderwelt-muenchen-ost.de/', FALSE),
    ('Einstein Boulderhalle', 'Landsberger Straße 185', 'München', 'info@einstein-boulder.de', NULL, FALSE),
    ('BlocHütte Nürnberg', 'Fürther Straße 80', 'Nürnberg', 'nuernberg@blochuette.de', NULL, FALSE),
    ('Boulderwelt Regensburg', 'Isarstraße 99', 'Regensburg', 'regensburg@boulderwelt.de', 'https://www.boulderwelt-regensburg.de/', FALSE);

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
    ('30er', 'Klassische Züge mit leichtem Überhang.', '/uploads/sectors/1/f9f1d556-c811-4352-b72e-4c67095d3351.png', 1),
    ('Volldach', 'Steile Wand mit großen Zügen.', '/uploads/sectors/2/a4e72d14-9cf0-421e-8f05-1975f3c724a9.png', 1),
    ('Slab-City', 'Technische Platten und Balance-Probleme.', '/uploads/sectors/3/3d0e1dc4-9d5b-4470-ae75-fb41a9456a2d.png', 2),
    ('45er', 'Starker Überhang und Campus-Style Moves.', '/uploads/sectors/4/0b9e6cfa-873e-46d6-b42f-a958602d9ff3.png', 2),
    ('Tech-Deck', 'Schmale Leisten und Fußarbeitstraining.', '/uploads/sectors/5/c8f932c0-852a-4688-81f2-8b8e2671c2c4.png', 3),
    ('Höhle', 'Kompressionslastige Boulder in der Grotte.', '/uploads/sectors/6/5ae55a50-9088-4b06-841e-50bd76f2a027.png', 3);

INSERT INTO boulders (description, color, grade_id, sector_id, holds_count, deleted) VALUES
    -- Boulders for sector 1 (30er, gym 1)
    ('Links hinten', 'YELLOW', 1, 1, 18, FALSE),
    ('Rechts vorne, steil', 'BLUE', 3, 1, 22, FALSE),
    ('Mittig, dynamisch', 'RED', 4, 1, 20, FALSE),
    ('Kante rechts', 'PINK', 2, 1, 16, FALSE),
    
    -- Boulders for sector 2 (Volldach, gym 1)
    ('Dach links, Power', 'BLACK', 6, 2, 25, FALSE),
    ('Campus rechts', 'BLUE', 7, 2, 22, FALSE),
    ('Überhang mittig', 'DARK_GREEN', 5, 2, 21, FALSE),
    ('Traversierung komplett', 'WHITE', 4, 2, 30, FALSE),
    
    -- Boulders for sector 3 (Slab-City, gym 2)
    ('Balance links', 'YELLOW', 9, 3, 14, FALSE),
    ('Platte rechts', 'WHITE', 10, 3, 12, FALSE),
    ('Technik pur', 'PINK', 11, 3, 18, FALSE),
    
    -- Boulders for sector 4 (45er, gym 2)
    ('Überhang extrem', 'BLACK', 14, 4, 26, FALSE),
    ('Power-Dynos', 'RED', 15, 4, 20, FALSE),
    ('Sloper Challenge', 'BLUE', 16, 4, 19, FALSE),
    ('Campus Board', 'DARK_GREEN', 13, 4, 24, FALSE),
    
    -- Boulders for sector 5 (Tech-Deck, gym 3)
    ('Crimp Heaven', 'YELLOW', 17, 5, 18, FALSE),
    ('Fußarbeit Spezial', 'PINK', 18, 5, 16, FALSE),
    ('Leisten links', 'WHITE', 19, 5, 17, FALSE),
    
    -- Boulders for sector 6 (Höhle, gym 3)
    ('Compression Crack', 'BLACK', 22, 6, 23, FALSE),
    ('Grotten-Traverse', 'BLUE', 23, 6, 28, FALSE),
    ('Power-Kompression', 'RED', 24, 6, 24, FALSE),
    ('Höhlen-Arete', 'DARK_GREEN', 21, 6, 21, FALSE);

INSERT INTO projects (user_id, boulder_id, created_at) VALUES
    -- Alice's projects (2 per gym)
    (1, 2, '2024-12-03 18:10:00'),   -- Gym 1
    (1, 3, '2024-12-03 18:20:00'),   -- Gym 1
    (1, 10, '2024-12-05 16:00:00'),  -- Gym 2
    (1, 11, '2024-12-05 16:10:00'),  -- Gym 2
    (1, 19, '2024-12-04 18:10:00'),  -- Gym 3
    (1, 20, '2024-12-04 18:25:00'),  -- Gym 3
    -- Bob's projects (2 per gym)
    (2, 4, '2024-12-02 19:05:00'),   -- Gym 1
    (2, 5, '2024-12-02 19:20:00'),   -- Gym 1
    (2, 13, '2024-12-02 19:50:00'),  -- Gym 2
    (2, 14, '2024-12-02 20:05:00'),  -- Gym 2
    (2, 17, '2024-12-04 18:05:00'),  -- Gym 3
    (2, 18, '2024-12-04 18:20:00'),  -- Gym 3
    -- Klaus' projects (2 per gym)
    (3, 1, '2024-12-01 17:45:00'),   -- Gym 1
    (3, 6, '2024-12-01 18:05:00'),   -- Gym 1
    (3, 12, '2024-12-03 16:45:00'),  -- Gym 2
    (3, 15, '2024-12-03 17:00:00'),  -- Gym 2
    (3, 21, '2024-12-06 18:15:00'),  -- Gym 3
    (3, 22, '2024-12-06 18:35:00'),  -- Gym 3
    -- Crusher's projects (2 per gym)
    (4, 7, '2024-12-02 18:15:00'),   -- Gym 1
    (4, 8, '2024-12-02 18:30:00'),   -- Gym 1
    (4, 9, '2024-12-04 17:15:00'),   -- Gym 2
    (4, 16, '2024-12-04 17:35:00'),  -- Gym 2
    (4, 18, '2024-12-07 18:05:00'),  -- Gym 3
    (4, 22, '2024-12-07 18:25:00');  -- Gym 3

INSERT INTO sessions (started_at, ended_at, user_id, gym_id) VALUES
    -- Past sessions for alice (user_id = 1) - 20 sessions for pagination testing
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

    -- Past sessions for bob (user_id = 2)
    ('2024-12-02 19:00:00', '2024-12-02 21:00:00', 2, 2),
    ('2024-12-04 18:00:00', '2024-12-04 20:00:00', 2, 3);

INSERT INTO goes (session_id, boulder_id, result, timestamp) VALUES
    -- Goes for alice's december 1st session (session_id = 10, gym 1) - 50 goes for pagination testing
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

    -- Goes for alice's december 3rd session (session_id = 11, gym 1)
    (11, 2, 'FINISHED', '2024-12-03 17:45:00'),
    (11, 5, 'FINISHED', '2024-12-03 18:00:00'),
    (11, 6, 'DID_NOT_FINISH', '2024-12-03 18:20:00'),
    (11, 7, 'CLOSE_TRY', '2024-12-03 18:40:00'),
    (11, 6, 'CLOSE_TRY', '2024-12-03 19:00:00'),
    (11, 8, 'DID_NOT_FINISH', '2024-12-03 19:30:00'),

    -- Goes for alice's december 5th session (session_id = 12, gym 2)
    (12, 9, 'FINISHED', '2024-12-05 16:15:00'),
    (12, 10, 'FINISHED', '2024-12-05 16:30:00'),
    (12, 11, 'CLOSE_TRY', '2024-12-05 16:50:00'),
    (12, 12, 'DID_NOT_FINISH', '2024-12-05 17:10:00'),
    (12, 11, 'FINISHED', '2024-12-05 17:30:00'),
    (12, 13, 'CLOSE_TRY', '2024-12-05 17:50:00'),

    -- Goes for bob's first session (session_id = 21, gym 2)
    (21, 9, 'FINISHED', '2024-12-02 19:15:00'),
    (21, 11, 'FINISHED', '2024-12-02 19:35:00'),
    (21, 13, 'CLOSE_TRY', '2024-12-02 19:55:00'),
    (21, 14, 'DID_NOT_FINISH', '2024-12-02 20:15:00'),
    (21, 15, 'CLOSE_TRY', '2024-12-02 20:40:00'),

    -- Goes for bob's second session (session_id = 22, gym 3)
    (22, 17, 'FINISHED', '2024-12-04 18:20:00'),
    (22, 18, 'FINISHED', '2024-12-04 18:40:00'),
    (22, 19, 'CLOSE_TRY', '2024-12-04 19:00:00'),
    (22, 20, 'DID_NOT_FINISH', '2024-12-04 19:20:00'),
    (22, 21, 'CLOSE_TRY', '2024-12-04 19:45:00');

INSERT INTO boulder_comments (user_id, boulder_id, comment, created_at) VALUES
    -- Comments for boulder 1 (Links hinten)
    (1, 1, 'sick boulder🔥', '2024-12-01 20:35:00'),
    (2, 1, 'love this one!', '2024-12-02 18:15:00'),
    (3, 1, 'crimpy and fun 💪', '2024-12-02 19:30:00'),
    (4, 1, 'perfect warmup', '2024-12-03 17:20:00'),
    (1, 1, 'got it second try 🎉', '2024-12-03 20:00:00'),
    (2, 1, 'nice flow', '2024-12-04 18:45:00');

INSERT INTO notices (title, message, creation_date, gym_id) VALUES
    -- Notices for Boulderwelt München Ost (gym 1)
    ('New Routes Set!', 'We''ve just finished setting 15 new routes in the Volldach sector. Come check out the fresh problems ranging from V2 to V8. The setters put extra focus on technical moves and balance.', '2024-12-15 10:00:00', 1),
    ('Christmas Opening Hours', 'Please note that we will have special opening hours during the Christmas holidays. Dec 24-26: Closed. Dec 27-30: 10:00-20:00. Dec 31: 10:00-18:00. Jan 1: Closed. Regular hours resume Jan 2.', '2024-12-10 14:30:00', 1),
    ('Youth Competition Registration', 'Registration is now open for our annual youth climbing competition on January 20th! Open to climbers aged 8-16. Sign up at the front desk or email us. Limited spots available.', '2024-12-08 09:15:00', 1),
    
    -- Notices for Einstein Boulderhalle (gym 2)
    ('Maintenance Notice', 'The Slab-City sector will be closed for maintenance and resetting from Dec 18-20. All other sectors remain open during regular hours. Sorry for any inconvenience!', '2024-12-12 16:45:00', 2),
    ('Beginner Course Starting Soon', 'Our next beginner boulder course starts January 8th! Learn proper technique, safety, and fundamentals over 4 weeks. Perfect for those new to climbing. Register online or at reception.', '2024-12-11 11:20:00', 2),
    ('Holiday Party December 22nd', 'Join us for our annual holiday party on December 22nd from 18:00-22:00! Free food, drinks, climbing games, and prizes. Everyone is welcome. Bring your friends and family!', '2024-12-05 15:00:00', 2),
    
    -- Notices for BlocHütte Nürnberg (gym 3)
    ('Winter Challenge Active', 'Our winter challenge is now live! Complete 50 routes of different grades before Feb 28th to win prizes. Track your progress on the board near the entrance. Good luck!', '2024-12-14 08:30:00', 3),
    ('New Yoga Classes', 'We''re introducing climbing-specific yoga classes every Tuesday and Thursday at 19:00. Great for flexibility and injury prevention. First class is free! Sign up at the front desk.', '2024-12-09 13:00:00', 3),
    ('Parking Reminder', 'Please remember to park in the designated gym parking area only. Cars parked in neighboring businesses'' lots may be towed. Thank you for your cooperation!', '2024-12-01 10:00:00', 3),

    -- Notices for Boulderwelt Regensburg (gym 4)
    ('Live Crowd Level Available!', 'You can now see our real-time gym capacity directly in the app! Check the crowd level indicator to find the best time to climb when it''s less busy.', '2024-12-16 12:00:00', 4),
    ('Fresh Routes in Overhang Section', 'Our route setters just finished 20 new boulders in the overhang area. Featuring everything from V3 to V8 with creative movement patterns. Come try them out!', '2024-12-13 09:30:00', 4),
    ('New Year''s Eve Hours', 'On December 31st we''re open from 10:00-16:00. Start the new year with some climbing! Closed on January 1st. Regular hours resume January 2nd.', '2024-12-10 15:00:00', 4);

INSERT INTO events (title, description, periodic, weekday, event_date, time, recurrence_frequency, created_at, gym_id, deleted) VALUES
    -- Events for Boulderwelt Muenchen Ost (gym 1)
    ('Community Climb Night', 'Join our monthly community climb night with a fun scramble format and prizes. All levels welcome.', TRUE, 'THURSDAY', NULL, '18:30-21:00', 'WEEKLY', '2024-11-28 12:00:00', 1, FALSE),
    ('Technique Workshop', 'A two-hour workshop focused on footwork and balance. Limited to 16 participants. Register at the front desk.', FALSE, NULL, '2024-12-06', '17:00-19:00', NULL, '2024-11-20 09:00:00', 1, FALSE),

    -- Events for Einstein Boulderhalle (gym 2)
    ('Holiday Boulder Jam', 'Celebrate the season with music, mini games, and a team relay. Free for members; guests welcome with day pass.', FALSE, NULL, '2024-12-22', '18:00-22:00', NULL, '2024-11-25 10:00:00', 2, FALSE),
    ('Intro to Bouldering', 'A beginner-friendly session covering safety, basics, and climbing etiquette. No prior experience required.', TRUE, 'MONDAY', NULL, '19:00-20:30', 'BI_WEEKLY', '2024-11-18 08:30:00', 2, FALSE),

    -- Events for BlocHutte Nuernberg (gym 3)
    ('Women''s Climbing Meet', 'An open meetup for women climbers to connect and climb together. Casual session with a coach on site.', TRUE, 'WEDNESDAY', NULL, '19:00-21:00', 'WEEKLY', '2024-11-13 15:00:00', 3, FALSE),
    ('Youth Skills Day', 'A one-day clinic for ages 10-15 covering movement basics and warmup routines.', FALSE, NULL, '2024-12-07', '10:00-13:00', NULL, '2024-11-10 09:30:00', 3, FALSE),

    -- Events for Boulderwelt Regensburg (gym 4)
    ('Route Setter Q&A', 'Meet the setters, ask questions, and get insights on the new overhang set.', FALSE, NULL, '2024-12-14', '19:30-20:30', NULL, '2024-11-29 14:00:00', 4, FALSE),
    ('New Year Warmup Session', 'Start the year with a guided warmup and easy problem circuit. Open to all members.', FALSE, NULL, '2024-12-29', '11:00-12:00', NULL, '2024-11-30 11:00:00', 4, FALSE),
    ('Monthly Community Meetup', 'A casual monthly meetup to connect, climb, and share beta. Open to all levels.', TRUE, 'TUESDAY', NULL, '19:00-21:00', 'MONTHLY', '2024-11-05 18:00:00', 4, FALSE);
