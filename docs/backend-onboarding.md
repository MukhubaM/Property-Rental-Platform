# Backend Developer Onboarding Guide

## 1. Overview

The Property Rental Platform backend is built with Java and Spring Boot. It uses PostgreSQL for data storage and Flyway to manage database migrations.

The backend follows a **modular monolith architecture**, organised by business feature. Each feature can contain API, application, domain and infrastructure layers.

This guide explains how to set up the backend on a new developer's local machine.

## 2. Prerequisites

Install the following software before continuing:

| Software | Requirement |
|---|---|
| Git | To clone the repository and manage changes |
| Java JDK | Java 24, matching the current `pom.xml` |
| PostgreSQL | A local PostgreSQL server |
| Maven | Not required separately; the project includes the Maven Wrapper |
| IDE | IntelliJ IDEA, VS Code or another Java-compatible IDE |

Verify your Java installation in PowerShell:

```powershell
java -version
```

Verify Git:

```powershell
git --version
```

Make sure PostgreSQL is installed and its database server is running.

## 3. Clone the repository

Clone the project repository:

```powershell
git clone https://github.com/MukhubaM/Property-Rental-Platform.git
```

Navigate to the backend directory:

```powershell
cd Property-Rental-Platform/app/backend
```

The backend contains the Maven build file, source code, application configuration and database migrations.

## 4. Create the PostgreSQL database

The backend expects a database named `property_rental`.

You do not need to create the application's tables manually. Flyway creates them from the project's migration files.

### Option A: Using pgAdmin

1. Open pgAdmin and connect to your local PostgreSQL server.
2. Expand **Servers** and locate your PostgreSQL server.
3. Right-click **Databases**.
4. Select **Create → Database**.
5. Enter `property_rental` as the database name.
6. Set the owner to `postgres`, if that is the account you intend to use.
7. Save the database.

### Option B: Using SQL

Connect to your local PostgreSQL server using pgAdmin's Query Tool on an existing database, or use `psql`. Run:

```sql
CREATE DATABASE property_rental;
```

If the database already exists, do not create it again.

## 5. Configure the database connection

Open:

`src/main/resources/application.properties`

The current configuration uses these connection settings:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/property_rental
spring.datasource.username=postgres
spring.datasource.password=password
```

Update the password to the one you created when installing PostgreSQL.

For example, if your local PostgreSQL password is different, replace the example value with your own password. Do not copy another developer's credentials.

**Security note:** `application.properties` is tracked by Git. Do not commit your real password. Before committing, make sure your local credential changes are not included. A future improvement is to load database credentials from environment variables so developers do not need to edit the shared configuration file.

The default connection assumes PostgreSQL is running on `localhost` using port `5432`. If your installation uses a different port, update the JDBC URL accordingly.

## 6. Understand database migrations

The project uses Flyway to create and maintain the database schema.

Migration files are located at:

`src/main/resources/db/migration/`

Current migrations:

- `V1__create_initial_schema.sql` - creates the initial database schema.
- `V2__seed_lookup_data.sql` - inserts initial lookup data.

When the Spring Boot application starts, Flyway checks which migrations have already been applied and executes pending migrations.

Hibernate is configured with:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

This means Hibernate validates the database schema against the entity mappings rather than automatically creating or modifying tables.

### Migration rules

- Create a new versioned migration for every schema or seed-data change.
- Never edit an already-applied migration.
- Use the naming convention `V3__describe_the_change.sql` for the next migration, increasing the version as required.
- Test migrations locally before submitting a pull request.

Flyway records migration history in the `flyway_schema_history` table.

## 7. Run the backend

Make sure the PostgreSQL server is running and the `property_rental` database exists.

From `app/backend`, run:

```powershell
.\mvnw.cmd spring-boot:run
```

On startup, Spring Boot loads the application configuration, connects to PostgreSQL, runs pending Flyway migrations and starts the web server.

The configured server port is `8080`.

If startup succeeds, the backend is running locally. A successful startup does not necessarily mean every business feature or API endpoint has been implemented.

To stop the application, press `Ctrl+C` in the terminal.

## 8. Run tests and build the project

Run the test suite:

```powershell
.\mvnw.cmd test
```

Build the project:

```powershell
.\mvnw.cmd clean package
```

Run tests and check the build before submitting your changes.

## 9. Backend folder structure

The backend source code is located under:

`src/main/java/propertyrental/platform/`

| Package | Responsibility |
|---|---|
| `configuration` | Application-wide Spring configuration |
| `identity` | User identity, roles and authentication |
| `inquiry` | Enquiries between tenants and property owners |
| `listing` | Properties, rental units and listing functionality |
| `location` | Provinces, cities and suburbs |
| `media` | Property and rental-unit images |
| `moderation` | Listing reports and moderation workflows |
| `shared` | Common components shared across features |

Feature packages can use the following layers:

- `api` - HTTP controllers and request/response DTOs.
- `application` - use cases and application workflows.
- `domain` - business concepts and rules.
- `infrastructure` - database access and external integrations.

Keep new code inside the relevant feature. Avoid placing feature-specific logic in `shared` or `configuration`.

## 10. Troubleshooting

### Cannot connect to PostgreSQL

Check that the PostgreSQL service is running, the database exists, and the configured host and port are correct.

### Password authentication failed

Check the username and password in your local configuration. Use the password for your own PostgreSQL installation.

### Database does not exist

Create a database named `property_rental`, then restart the application.

### Migration failed

Read the error message and inspect the relevant migration. Do not edit migrations that have already been applied to a shared or existing database. Create a new migration when a schema change is needed.

### Port 8080 is already in use

Stop the other application using port `8080`, or temporarily configure a different port for your local run.

### Java version error

Check `java -version` and make sure the installed JDK matches the version configured in `pom.xml`.

## 11. Before submitting a change

1. Work on an appropriate feature branch.
2. Keep changes focused on the feature you are implementing.
3. Add or update tests where appropriate.
4. Run `.\mvnw.cmd test`.
5. Review your changes for accidental credentials, generated files and unrelated modifications.
6. Commit your changes and open a pull request according to the repository's contribution workflow.

## 12. Final checklist

- [ ] Java and Git are installed.
- [ ] PostgreSQL is installed and running.
- [ ] The repository has been cloned.
- [ ] The `property_rental` database exists.
- [ ] Local database credentials are configured.
- [ ] The backend starts successfully.
- [ ] Flyway migrations complete successfully.
- [ ] Tests run successfully.
- [ ] No real passwords or other secrets are included in the commit.
