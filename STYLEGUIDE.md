# Project Style Guide

## 1. Introduction

This style guide outlines the conventions and best practices for database table design, naming conventions, and class structure within our project.

## 2. Table Design

### 2.1 Naming Conventions
- **Tables**: Use camelCase for table names and ensure the names are descriptive and singular (e.g., `staffTable`, `patientQueueTable`).
- **Columns**: Use camelCase for column names and ensure they are meaningful and descriptive (e.g., `firstName`, `phoneNumber`).

### 2.2 Data Types
- **INTEGER**: For whole numbers, such as IDs or counts.
- **TEXT** or **VARCHAR**: For variable-length strings.
- **CHAR**: For fixed-length strings, typically used for predefined sets of characters.
- **CHECK Constraints**: To enforce domain integrity by restricting the values that can be entered into a column.

### 2.3 Primary Keys
- Each table should have a primary key column to uniquely identify each record.
- Use the `AUTOINCREMENT` attribute for primary key columns to automatically generate unique values.

### 2.4 Not Null Constraints
- Apply the `NOT NULL` constraint to columns that must have a value, ensuring that no null values are entered.

### 2.5 Unique Constraints
- Use the `UNIQUE` constraint to ensure that the values in a column or a set of columns are unique across the table.

### 2.6 Foreign Keys
- Define foreign keys to establish relationships between tables and ensure referential integrity.

### 2.7 Example Table Definitions

**Staff Table**

```sql
CREATE TABLE staffTable (
  id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  firstName TEXT NOT NULL,
  lastName TEXT NOT NULL,
  username TEXT NOT NULL,
  password TEXT NOT NULL,
  role TEXT NOT NULL CHECK(role IN ('Reception', 'CheckUpStation', 'Doctor', 'Pharmacy')),
  UNIQUE (username)
);
```
**Patient Queue Table**
```sql
CREATE TABLE patientQueueTable (
  queueNumber INTEGER PRIMARY KEY AUTOINCREMENT,
  name VARCHAR(100) NOT NULL,
  age INT NOT NULL,
  sex CHAR(1) NOT NULL CHECK(sex IN ('M', 'F')),
  phoneNumber VARCHAR(20)
);
```

## 3. Naming Conventions

### 3.1 Tables and Databases
- **Database**: Lowercase and ends with 'db' (e.g., `clinicdb.db`).
- **Tables**: Lowercase and ends with 'Table' (e.g., `staffTable`).

### 3.2 GUI
- **.fxml files**: Lowercase and each word separated by '-' (e.g., `login-page.fxml`).
- **FX components**: Variable name + component name in camelCase (e.g., `loginMessageLabel`, `patientTableView`).

### 3.3 Java Classes
- **Main.java**: Should be the only entry point (in config file, do not change).
- **Application files**: End with `Application` (e.g., `HelloApplication.java`).
- **Controller files**: Same name as .fxml but without '-' and in camelCase, must end with `Controller` (e.g., `LoginPageController`).
- **Model files**: Used for TableView object creation. Named according to object stored, follow PropertyValueFactory conventions for getters and setters.

### 3.4 Methods
- **To go to different view**: Prefix with `open` + fxml file name in camelCase (e.g., `openLoginPage()` for `login-page.fxml`).

## 4. Controller Class

### 4.1 Class structure

```java
// Package Name and class imports
package your.package.name;

// JavaFX Imports
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
// Add other necessary JavaFX imports here

// SQL Imports
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
// Add other necessary SQL imports here

// Class Declaration
public class YourControllerClassName {

    // General Class Variables
    private Connection connection;
    private Statement statement;
    // Add other general variables here

    // GUI Variables
    @FXML
    private Label yourLabel;
    @FXML
    private Button yourButton;
    @FXML
    private TextField yourTextField;
    @FXML
    private TableView<YourDataType> yourTableView;
    // Add other GUI variables here

    // Initialise Method (if any)
    @FXML
    public void initialize() {
        // Initialization code
    }

    // Button OnAction Methods (if any)
    @FXML
    private void handleYourButtonAction() {
        // Button action code
    }
    // Add other button action methods here

    // Other Methods
    private void yourHelperMethod() {
        // Other methods
    }
    // Add additional methods here
}
```

### 4.2 Other Controller Conventions
- Every controller must inherit from the `DatabaseConnection` class to access the protected `Connection connection` class variable.
- Every controller must have the `close()` method as its last method.
