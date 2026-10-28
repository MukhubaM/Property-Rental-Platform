# Backend Developer Onboarding Guide

## 1. Purpose

This guide helps contributors set up and run the Property Rental Platform backend locally. The backend uses Java, Spring Boot, PostgreSQL, Spring Data JPA, and Flyway. It is organised as a modular monolith with code grouped by business feature.

## 2. Prerequisites

Install Git, Java JDK 24 (matching the current `pom.xml`), and PostgreSQL. An IDE such as IntelliJ IDEA or VS Code is recommended. The repository includes the Maven Wrapper, so a separate Maven installation is normally unnecessary.

Check installations in PowerShell:

```powershell
java -version
git --version
```

Make sure PostgreSQL is running.

## 3. Clone the repository

```powershell
git clone https://github.com/MukhubaM/Property-Rental-Platform.git
cd Property-Rental-Platform/app/backend
```

If you already cloned the repository, navigate to its `app/backend` directory.

## 4. Create the PostgreSQL database

The application expects a local database named `property_rental`, normally on `localhost:5432`.

### Using pgAdmin
1. Connect to your local PostgreSQL server.
2. Right-click **Databases** and choose **Create → Database**.
3. Enter `property_rental`.
4. Choose the appropriate owner, such as `postgres`, and save.

### Using SQL
Connect to PostgreSQL through pgAdmin's Query Tool or `psql`, then run:

```sql
CREATE DATABASE property_rental;
```

Do not run this if the database already exists.

## 5. Configure local credentials

Open `src/main/resources/application.properties`. The current datasource settings are:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/property_rental
spring.datasource.username=postgres
spring.datasource.password=password
```

Replace `password` with the password for your own local PostgreSQL account. The shown value is only the current repository configuration; it works only if it matches your local account.

**Do not commit your real password.** This properties file is tracked by Git, so check your changes before committing. A future improvement is to load credentials from environment variables. If PostgreSQL uses another port, update the JDBC URL.

## 6. Database migrations

Flyway manages schema setup. Migration files are in `src/main/resources/db/migration/`:

- `V1__create_initial_schema.sql` - creates the initial schema.
- `V2__seed_lookup_data.sql` - inserts initial lookup data.

When the application starts, Flyway runs pending migrations. You do not need to create application tables manually.

Hibernate is configured with `spring.jpa.hibernate.ddl-auto=validate`, so it validates entity mappings against the schema instead of automatically creating or changing tables.

Migration rules:
- Add a new versioned migration for each schema or seed-data change.
- Do not edit migrations already applied to an environment.
- Use the next available version, e.g. `V3__add_listing_feature.sql`.
- Test migrations locally.
- Flyway records applied migrations in `flyway_schema_history`.

## 7. Start the backend

From `app/backend`, run:

```powershell
.\mvnw.cmd spring-boot:run
```

The server is configured for port `8080`. On startup, Spring Boot connects to PostgreSQL, runs pending Flyway migrations, validates the schema through Hibernate, and starts the web server. A successful startup does not mean every planned feature is already implemented.

Stop the application with `Ctrl+C`.

## 8. Run tests and build

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

Run tests and check the build before submitting changes.

## 9. Package overview

Java source is under `src/main/java/propertyrental/platform/`.

| Package | Responsibility |
|---|---|
| `configuration` | Application-wide Spring configuration |
| `identity` | Users, roles and external identities |
| `inquiry` | Enquiries about rental units |
| `listing` | Properties, rental units and listing workflows |
| `location` | Provinces, cities and suburbs |
| `media` | Property and unit images |
| `moderation` | Listing reports and moderation workflows |
| `shared` | Components genuinely shared across features |

Feature packages may contain:
- `api` - HTTP controllers and request/response DTOs.
- `application` - use cases and workflows.
- `domain` - business concepts and rules.
- `infrastructure` - database access and external integrations.

Add classes as features are implemented. Avoid placing feature-specific logic in `shared`.

## 10. Troubleshooting

- **Cannot connect to PostgreSQL:** check that the service is running and the host/port are correct.
- **Password authentication failed:** verify your local username and password.
- **Database does not exist:** create `property_rental`.
- **Migration failed:** inspect the Flyway error and migration. Do not edit an already-applied migration to fix an existing database; add a new migration for changes.
- **Port 8080 is in use:** stop the process using it or temporarily change `server.port`.
- **Java version error:** compare `java -version` with `<java.version>` in `pom.xml`.

## 11. Before submitting changes

1. Use an appropriate feature branch.
2. Keep the change focused.
3. Add or update tests where appropriate.
4. Run ` .\mvnw.cmd test`.
5. Check for generated files, unrelated changes and secrets.
6. Open a pull request according to the repository's contribution workflow.

## 12. Setup checklist

- [ ] Java and Git are installed.
- [ ] PostgreSQL is running.
- [ ] Repository is cloned.
- [ ] `property_rental` database exists.
- [ ] Local credentials are configured.
- [ ] Backend starts successfully.
- [ ] Flyway migrations complete.
- [ ] Tests pass.
- [ ] No real credentials are committed.
