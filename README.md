# EasySplit

EasySplit is a JavaFX desktop app for tracking shared group expenses and showing who owes what.

## Overview

The app provides:
- User signup and login
- Group creation with members
- Expense entry with multiple split methods:
  - Equal
  - By percentage
  - Custom amounts
- Group details view with:
  - Expense list
  - Member balances
  - Settlement summary text

Tech stack:
- Java (modular project, Eclipse style)
- JavaFX (FXML + controllers)
- Microsoft SQL Server (JDBC)

## Project Structure

```text
src/
  application/   # JavaFX app entry point + controllers
  models/        # Domain models (Group, Member, Expense, etc.)
  resources/     # FXML screens + CSS
  utils/         # DB connection helper
  module-info.java
bin/             # Compiled output/resources (generated)
```

Main entry point:
- `src/application/Main.java` -> launches `EasySplitApp`

## Prerequisites

1. Java 21+ (project currently references JavaFX 21 jars and JavaSE-22 in Eclipse config)
2. JavaFX SDK (matching your Java version)
3. Microsoft SQL Server
4. Microsoft SQL Server JDBC Driver (`mssql-jdbc`)
5. (Windows only for current default config) SQL Server integrated authentication support

## Database Setup

The app expects a SQL Server database named `EasySplitDB`.

Current connection string in `src/utils/SQLConnection.java`:

```java
jdbc:sqlserver://<HOST>:1433;databaseName=EasySplitDB;integratedSecurity=true;encrypt=false;
```

Update this host/auth config for your machine before running.

### Minimal schema

Use this as a starting point:

```sql
CREATE DATABASE EasySplitDB;
GO
USE EasySplitDB;
GO

CREATE TABLE Users (
    userID INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password VARBINARY(32) NOT NULL,
    email NVARCHAR(255) NOT NULL UNIQUE,
    createdAt DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE TABLE Groups (
    GroupID INT IDENTITY(1,1) PRIMARY KEY,
    GroupName NVARCHAR(150) NOT NULL
);

CREATE TABLE Members (
    MemberID INT IDENTITY(1,1) PRIMARY KEY,
    GroupID INT NOT NULL,
    Name NVARCHAR(120) NOT NULL,
    Email NVARCHAR(255) NOT NULL,
    CONSTRAINT FK_Members_Groups FOREIGN KEY (GroupID) REFERENCES Groups(GroupID)
);

CREATE TABLE Expenses (
    ExpenseID INT IDENTITY(1,1) PRIMARY KEY,
    GroupID INT NOT NULL,
    ExpenseName NVARCHAR(150) NOT NULL,
    Amount DECIMAL(12,2) NOT NULL,
    PayerID INT NOT NULL,
    ExpenseDate DATE NOT NULL,
    CONSTRAINT FK_Expenses_Groups FOREIGN KEY (GroupID) REFERENCES Groups(GroupID),
    CONSTRAINT FK_Expenses_Members FOREIGN KEY (PayerID) REFERENCES Members(MemberID)
);
```

## Run Instructions

### Option 1: Eclipse (recommended for this project layout)

1. Import project as an existing Eclipse Java project.
2. Configure JRE/JDK (21+).
3. Configure JavaFX libraries on build path/module path.
4. Add SQL Server JDBC driver jar to module path/classpath.
5. Ensure DB connection in `SQLConnection.java` is valid.
6. Run `application.Main`.

### Option 2: Command line (manual module-path setup)

Compile and run with JavaFX + JDBC modules/jars available. Example shape:

```bash
javac --module-path /path/to/javafx/lib \
      --add-modules javafx.controls,javafx.fxml \
      -cp /path/to/mssql-jdbc.jar \
      -d out $(find src -name "*.java")

java --module-path /path/to/javafx/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp out:/path/to/mssql-jdbc.jar \
     application.Main
```

Adjust paths for your OS/shell.

## Current Behavior Notes

Implemented and wired:
- Login/signup DB operations (`Users`)
- Create group and members DB insert (`Groups`, `Members`)
- Add/edit/delete groups in UI
- Add expense popup with split validation rules
- Group details popup with expense table, balance table, settlement text

Known gaps in current codebase:
- Dashboard groups are not loaded from DB on startup.
- Group edit/delete actions are UI-only (not persisted to DB).
- Expense popup adds expenses to in-memory group model only (not persisted to DB).
- Signup duplicate email check uses a hardcoded in-memory list, not DB uniqueness query.
- `models/Expense` shadows fields from `AbstractExpense`, which can cause expense values shown in tables/balance calculations to be empty or zero.
- `AddExpenseController`/`add_expense.fxml` appear to be legacy and are not the main add-expense flow.

## Key Screens (FXML)

- `src/resources/login.fxml`
- `src/resources/signup.fxml`
- `src/resources/dashboard.fxml`
- `src/resources/addGroup.fxml`
- `src/resources/editGroup.fxml`
- `src/resources/expenseForm.fxml`
- `src/resources/groupDetailsPopup.fxml`

## License

No license file is currently included in this repository.
