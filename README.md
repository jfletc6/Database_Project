# Cyber Club Database Portal

A Java Swing desktop application for managing a university cyber club's data — students, competitions, club meetings, and events — backed by a MySQL database hosted on Towson University's Triton server.

## Features

- **Students** — view, add, and manage club members
- **Competitions** — track competitions, roles, dates, and locations
- **Club Meetings** — log meeting dates and topics; record attendance and leadership
- **Events** — manage club events and student attendance
- **Delete Records** — remove entries across all tables
- **Dashboard** — branded welcome panel with club logo and message of the day

## Project Structure

```
Database_Project/
├── Scripts/
│   ├── Cyber_Club_Database_Schema.sql   # Table definitions
│   ├── Dummy_data.sql                   # Sample data
│   └── Drop_tables.sql                  # Teardown script
├── DatabaseProject/
│   ├── src/databaseproject/             # Java source files
│   │   ├── Project.java                 # Entry point / main UI
│   │   ├── DBManager.java               # JDBC connection & queries
│   │   ├── StudentTab.java
│   │   ├── CompetitionTab.java
│   │   ├── ClubMeetingTab.java
│   │   ├── EventTab.java
│   │   ├── DeleteTab.java
│   │   └── Theme.java
│   ├── mysql-connector-java-8.0.16.jar  # JDBC driver
│   └── dist/DatabaseProject.jar         # Pre-built JAR
└── Setup_Guide (1).txt                  # Detailed setup instructions
```

## Database Schema

| Table | Description |
|---|---|
| `Students` | Club members (ID, name, major) |
| `Competitions` | Competition details |
| `Club_Meetings` | Meeting dates and topics |
| `Events` | Club events |
| `Student_Leadership` | Leadership roles with date ranges |
| `Participate_In` | Students ↔ Competitions |
| `Attend` | Students ↔ Club Meetings |
| `Go_To` | Students ↔ Events |
| `Instruct` | Student leaders ↔ Club Meetings |

## Prerequisites

- Java 8+
- MySQL access on `triton.towson.edu` (Towson University account required)
- PuTTY (or any SSH client) for database setup

## Setup

### 1. Load the database on Triton

SSH into `triton.towson.edu` and run:

```bash
mysql -u <MySQL username> -p
```

Inside MySQL:

```sql
USE <your_database>;
SOURCE schema;
SOURCE dummy_data;
```

To wipe and start fresh:

```sql
SOURCE drop_tables;
```

### 2. Configure the application

Update the connection settings in `DBManager.java` with your Triton MySQL credentials before building.

### 3. Run the application

**Using the pre-built JAR:**

```bash
java -jar DatabaseProject/dist/DatabaseProject.jar
```

**Compiling from source:**

```bash
cd DatabaseProject/src/databaseproject
javac -cp ../../mysql-connector-java-8.0.16.jar *.java
java -cp ../../mysql-connector-java-8.0.16.jar:. Project
```

> On Windows replace `:` with `;` in the classpath.

See `Setup_Guide (1).txt` for the full step-by-step walkthrough.
