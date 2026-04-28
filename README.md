# Advanced Test Automation (Java) with Selenium

## JDBC 

This repository contains a Java application that demonstrates JDBC (Java Database Connectivity) operations with a PostgreSQL database. The application implements a simple data model for managing categories, children, and clubs.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Database Structure](#database-structure)
- [Project Structure](#project-structure)
- [Testing](#testing)
- [Resources for Writing Tests](#resources-for-writing-tests)

## Prerequisites
- Java 17 or higher
- PostgreSQL database
- Maven

## Setup
1. Install PostgreSQL and run it.
2. In the file `src/test/java/utils/DBUtil.java`, replace the connection data with your own:
   ```java
   // Example:
   private static final String URL = "jdbc:postgresql://localhost:5432/your_database";
   private static final String USER = "your_username";
   private static final String PASSWORD = "your_password";
   ```
3. Run Maven to download dependencies:
   ```
   mvn clean install
   ```

## Database Structure
The project uses the following database structure:

<img width="350" alt="image" src="https://github.com/nromanen/speak_ukraine/assets/4123050/6086f17a-8346-475d-a768-3cc4f78e84e3">

## Project Structure

### Model Classes
The data model consists of the following classes in the `model` package:
- `Category`: Represents a category with id, avatar, and title
- `Child`: Represents a child with id, firstName, lastName, birthDate, and a list of clubs
- `Club`: Represents a club with id, title, category, description, imageUrl, and a list of children

### DAO (Data Access Object) Classes
The DAO layer provides an abstraction for database operations:

#### Interfaces
- `CategoryDAO`: Defines operations for Category entities
- `ChildDAO`: Defines operations for Child entities

#### Implementations
- `CategoryDB`: Implements CategoryDAO using JDBC
- `ChildDB`: Implements ChildDAO using JDBC
- `DBUtil`: Utility class for database connections and operations

## Testing
The project includes JUnit tests for the DAO implementations:
- `CategoryDBTest`: Tests for CategoryDB methods
- `ChildDBTest`: Tests for ChildDB methods (you need to implement these)

### Running Tests
To run the tests, use the following Maven command:
```
mvn test
```

### Populating Test Data
For populating initial data in the database, you can use the `executeFile` method from the `utils.DBUtil` class:

```
// Example usage
DBUtil.executeFile("src/test/resources/init.sql");
```

**After creating your tests in GitHub Actions, you should have something like this picture:**
<img width="880" alt="image" src="https://github.com/taqc-java/jdbc/assets/61456363/53fe5bd6-d056-49a8-8442-9b08515dbae8">

## Resources for Writing Tests

### JUnit 5
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [JUnit 5 API Documentation](https://junit.org/junit5/docs/current/api/)
- [Baeldung JUnit 5 Tutorial](https://www.baeldung.com/junit-5)

### JDBC Testing
- [Testing JDBC Code with JUnit](https://www.baeldung.com/mocking-jdbc-unit-testing)
- [H2 Database for Testing](https://www.baeldung.com/spring-boot-h2-database)

### Mocking Frameworks
- [Mockito](https://site.mockito.org/)
- [JMockit](https://jmockit.github.io/)
- [EasyMock](https://easymock.org/)

### Best Practices
- [Unit Testing Best Practices](https://www.baeldung.com/java-unit-testing-best-practices)
