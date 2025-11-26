-- =========================================================
-- Sultan-Themed Dummy Data for jfletc6db
-- =========================================================

------------------------------------------------------------
-- STUDENTS
------------------------------------------------------------
INSERT INTO Students (Student_ID, Name, Major) VALUES
  (1001, 'Farid ibn Rashid',     'Royal Strategy'),
  (1002, 'Layla al-Zahra',       'Arcane Cipherworks'),
  (1003, 'Omar ibn Tariq',       'Fortress Engineering'),
  (1004, 'Zahra bint Selim',     'Desert Intelligence'),
  (1005, 'Yusuf al-Hakim',       'Scholarly Archives');

------------------------------------------------------------
-- COMPETITIONS
------------------------------------------------------------
INSERT INTO Competitions
(Competition_ID, Competition_Name, Competition_Role, Competition_Date, Location) VALUES
  (1, 'Tournament of Blades',      'Champion',      '1425-03-15', 'Grand Arena of Qamur'),
  (2, 'Siegecraft Trials',         'Tactician',     '1425-04-02', 'Outer Fortress of Dahrim'),
  (3, 'Whispered Cipher Contest',  'Codebreaker',   '1425-05-10', 'Hall of Silent Scripts'),
  (4, 'Guardians of the Gate',     'Sentinel',      '1425-06-01', 'Bronze Gate Bastion'),
  (5, 'Desert Riders Rally',       'Scout Captain', '1425-07-20', 'Crescent Moon Dunes');

------------------------------------------------------------
-- CLUB MEETINGS
------------------------------------------------------------
INSERT INTO Club_Meetings (Meeting_Date, Meeting_Topic) VALUES
  ('1425-01-05', 'Council of Viziers: Defense of the Eastern Dunes'),
  ('1425-01-19', 'Mapping the Caravan Routes of the Sapphire Road'),
  ('1425-02-02', 'Secrets of the Obsidian Cipher Tablets'),
  ('1425-02-16', 'Designing Traps for the Palace Treasury'),
  ('1425-03-02', 'Night Watch Rotations on the Moonlit Walls');

------------------------------------------------------------
-- STUDENT LEADERSHIP
------------------------------------------------------------
INSERT INTO Student_Leadership
(Student_ID, Start_Date, End_Date, Role) VALUES
  (1001, '1425-01-01', '1425-12-31', 'Grand Warden of the Sultan''s Guard'),
  (1002, '1425-01-01', '1425-12-31', 'Mistress of Secret Ciphers'),
  (1003, '1425-01-01', '1425-12-31', 'Master of Siege Engines'),
  (1004, '1425-01-01',  NULL,        'Vizier of Desert Scouts'),
  (1005, '1425-01-01',  NULL,        'Keeper of the Royal Archives');

------------------------------------------------------------
-- EVENTS
------------------------------------------------------------
INSERT INTO Events (Event_ID, Event_Date, Event_Topic) VALUES
  (201, '1425-02-08', 'Festival of Lanterns in the Inner Courtyard'),
  (202, '1425-02-25', 'Audience with the Sultan: Tales from the Frontier'),
  (203, '1425-03-12', 'Great Bazaar of Artifacts and Relics'),
  (204, '1425-03-28', 'Moonlit Vigil on the Palace Walls'),
  (205, '1425-04-10', 'Council of Realms: Treaties and Alliances');

------------------------------------------------------------
-- PARTICIPATE_IN
-- (Students ↔ Competitions)
------------------------------------------------------------
INSERT INTO Participate_In (Student_ID, Competition_ID) VALUES
  (1001, 1),
  (1001, 4),
  (1002, 3),
  (1002, 5),
  (1003, 2),
  (1003, 4),
  (1004, 1),
  (1004, 5),
  (1005, 3);

------------------------------------------------------------
-- ATTEND
-- (Students ↔ Club Meetings)
------------------------------------------------------------
INSERT INTO Attend (Student_ID, Meeting_Date) VALUES
  (1001, '1425-01-05'),
  (1001, '1425-02-02'),
  (1002, '1425-01-05'),
  (1002, '1425-02-16'),
  (1003, '1425-01-19'),
  (1003, '1425-03-02'),
  (1004, '1425-01-19'),
  (1004, '1425-02-16'),
  (1005, '1425-02-02'),
  (1005, '1425-03-02');

------------------------------------------------------------
-- GO_TO
-- (Students ↔ Events)
------------------------------------------------------------
INSERT INTO Go_To (Student_ID, Event_ID) VALUES
  (1001, 201),
  (1001, 204),
  (1002, 202),
  (1002, 203),
  (1003, 203),
  (1003, 205),
  (1004, 201),
  (1004, 205),
  (1005, 202),
  (1005, 204);

------------------------------------------------------------
-- INSTRUCT
-- (Leadership ↔ Club Meetings)
------------------------------------------------------------
INSERT INTO Instruct
(Student_ID, Start_Date, Meeting_Date) VALUES
  -- Grand Warden leads defense councils
  (1001, '1425-01-01', '1425-01-05'),
  (1001, '1425-01-01', '1425-03-02'),

  -- Mistress of Secret Ciphers leads cipher tablet session
  (1002, '1425-01-01', '1425-02-02'),

  -- Master of Siege Engines leads trap and siege design
  (1003, '1425-01-01', '1425-02-16'),
  (1003, '1425-01-01', '1425-03-02');

