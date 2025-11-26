-- Comment out any DROP TABLE line you want to keep.
-- Order matters because of foreign-key constraints.

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS Instruct;
DROP TABLE IF EXISTS Participate_In;
DROP TABLE IF EXISTS Attend;
DROP TABLE IF EXISTS Go_To;

DROP TABLE IF EXISTS Student_Leadership;
DROP TABLE IF EXISTS Club_Meetings;
DROP TABLE IF EXISTS Competitions;
DROP TABLE IF EXISTS Events;
DROP TABLE IF EXISTS Students;

SET FOREIGN_KEY_CHECKS = 1;