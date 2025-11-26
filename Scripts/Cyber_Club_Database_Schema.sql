-- STUDENTS ---------------------------------------------------------
CREATE TABLE Students (
    Student_ID   INT PRIMARY KEY,
    Name         VARCHAR(100) NOT NULL,
    Major        VARCHAR(100)
);

-- COMPETITIONS -----------------------------------------------------
CREATE TABLE Competitions (
    Competition_ID    INT PRIMARY KEY,
    Competition_Name  VARCHAR(100) NOT NULL,
    Competition_Role  VARCHAR(100),
    Competition_Date  DATE,
    Location          VARCHAR(100)
);

-- CLUB MEETINGS (Meeting_Date is PK) -------------------------------
CREATE TABLE Club_Meetings (
    Meeting_Date  DATE PRIMARY KEY,
    Meeting_Topic VARCHAR(200)
);

-- STUDENT LEADERSHIP -----------------------------------------------
CREATE TABLE Student_Leadership (
    Student_ID  INT,
    Start_Date  DATE,
    End_Date    DATE,
    Role        VARCHAR(100),
    PRIMARY KEY (Student_ID, Start_Date),
    FOREIGN KEY (Student_ID) REFERENCES Students(Student_ID)
);

-- EVENTS ------------------------------------------------------------
CREATE TABLE Events (
    Event_ID    INT PRIMARY KEY,
    Event_Date  DATE,
    Event_Topic VARCHAR(200)
);

-- PARTICIPATE IN (Students ↔ Competitions) -------------------------
CREATE TABLE Participate_In (
    Student_ID      INT,
    Competition_ID  INT,
    PRIMARY KEY (Student_ID, Competition_ID),
    FOREIGN KEY (Student_ID)     REFERENCES Students(Student_ID),
    FOREIGN KEY (Competition_ID) REFERENCES Competitions(Competition_ID)
);

-- ATTEND (Students ↔ Club Meetings) --------------------------------
CREATE TABLE Attend (
    Student_ID    INT,
    Meeting_Date  DATE,
    PRIMARY KEY (Student_ID, Meeting_Date),
    FOREIGN KEY (Student_ID)   REFERENCES Students(Student_ID),
    FOREIGN KEY (Meeting_Date) REFERENCES Club_Meetings(Meeting_Date)
);

-- GO_TO (Students ↔ Events) ----------------------------------------
CREATE TABLE Go_To (
    Student_ID  INT,
    Event_ID    INT,
    PRIMARY KEY (Student_ID, Event_ID),
    FOREIGN KEY (Student_ID) REFERENCES Students(Student_ID),
    FOREIGN KEY (Event_ID)   REFERENCES Events(Event_ID)
);

-- INSTRUCT (Student Leadership ↔ Club Meetings) --------------------
CREATE TABLE Instruct (
    Student_ID    INT,
    Start_Date    DATE,
    Meeting_Date  DATE,
    PRIMARY KEY (Student_ID, Start_Date, Meeting_Date),
    FOREIGN KEY (Student_ID, Start_Date)
        REFERENCES Student_Leadership(Student_ID, Start_Date),
    FOREIGN KEY (Meeting_Date)
        REFERENCES Club_Meetings(Meeting_Date)
);
