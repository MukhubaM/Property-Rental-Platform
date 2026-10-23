# Property Rental Platform - Backend

A scalable, modular monolith backend for a property rental platform serving users across South Africa. The platform will allow property owners to advertise rooms and properties for rent, while helping prospective tenants discover suitable accommodation.

**Repository:** [Property-Rental-Platform](https://github.com/MukhubaM/Property-Rental-Platform)

## Table of Contents

1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Architecture](#architecture)
4. [Project Structure](#project-structure)
5. [Understanding the Layers](#understanding-the-layers)
6. [Where Traditional Spring Boot Concepts Fit](#where-traditional-spring-boot-concepts-fit)
7. [Request Lifecycle](#request-lifecycle)
8. [Module Responsibilities](#module-responsibilities)
9. [Architecture and Dependency Rules](#architecture-and-dependency-rules)
10. [Database and Migrations](#database-and-migrations)
11. [API Design Conventions](#api-design-conventions)
12. [Testing Strategy](#testing-strategy)
13. [Configuration and Environment Variables](#configuration-and-environment-variables)
14. [Getting Started](#getting-started)
15. [Git and Contribution Guidelines](#git-and-contribution-guidelines)
16. [Future Scalability](#future-scalability)

---

## Project Overview

The backend provides the business logic and REST APIs used by the platform's frontend.

Its main responsibilities include:

- User registration, authentication and authorization.
- Creating and managing rental property listings.
- Searching and filtering available accommodation.
- Managing property locations and amenities.
- Handling property photos and media integrations.
- Supporting enquiries between prospective tenants and property owners.
- Reporting and moderating suspicious listings.
- Persisting data securely in PostgreSQL.

The initial implementation will be a **modular monolith**.

This means the application runs as one Spring Boot application, but its business capabilities are organised into clearly defined modules.

Our goal is to maintain a codebase that is easy to understand, test, extend and potentially split into independent services in the future.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming language |
| Spring Boot | Application framework |
| Spring Web | REST API development |
| Spring Data JPA | Database persistence |
| PostgreSQL | Relational database |
| Jakarta Bean Validation | Request and data validation |
| Spring Security | Authentication and authorization |
| Flyway | Database schema migrations |
| Maven | Dependency and build management |
| JUnit and Spring Boot Test | Automated testing |
| OpenAPI / Swagger | API documentation |
| Spring Boot Actuator | Health and operational monitoring |

The exact Java and Spring Boot versions will be defined by the generated project configuration. Dependencies should remain compatible with the selected Spring Boot release.

## Architecture

The backend follows **package-by-feature with layered separation of concerns**.

Instead of grouping every controller, service and repository into global folders, we group code by business capability and separate responsibilities within each capability.

For example, the property listing feature owns its controllers, use cases, domain model and persistence implementation.

This approach combines the familiarity of traditional Spring Boot development with stronger boundaries between business modules.

### High-level architecture

```text
Angular Frontend
       |
       | HTTP / JSON
       v
REST API Layer
       |
       v
Application Layer
       |
       v
Domain Layer
       ^
       |
Infrastructure Layer
       |
       v
PostgreSQL / External Systems
```

The application layer coordinates use cases, the domain layer contains business rules, and the infrastructure layer implements technical details such as database access.

Infrastructure components can be called through interfaces defined by the appropriate inner layer. The domain should not depend on Spring MVC, database repositories or external service implementations.

## Project Structure

The following represents the intended structure as modules are implemented. Not every package needs to exist on day one.

```text
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/propertyrental/platform/
│   │   │       ├── PlatformApplication.java
│   │   │       │
│   │   │       ├── identity/
│   │   │       │   ├── api/
│   │   │       │   ├── application/
│   │   │       │   ├── domain/
│   │   │       │   └── infrastructure/
│   │   │       │
│   │   │       ├── listing/
│   │   │       │   ├── api/
│   │   │       │   ├── application/
│   │   │       │   ├── domain/
│   │   │       │   └── infrastructure/
│   │   │       │
│   │   │       ├── location/
│   │   │       │   ├── api/
│   │   │       │   ├── application/
│   │   │       │   ├── domain/
│   │   │       │   └── infrastructure/
│   │   │       │
│   │   │       ├── media/
│   │   │       │   ├── api/
│   │   │       │   ├── application/
│   │   │       │   ├── domain/
│   │   │       │   └── infrastructure/
│   │   │       │
│   │   │       ├── inquiry/
│   │   │       │   ├── api/
│   │   │       │   ├── application/
│   │   │       │   ├── domain/
│   │   │       │   └── infrastructure/
│   │   │       │
│   │   │       ├── moderation/
│   │   │       │   ├── api/
│   │   │       │   ├── application/
│   │   │       │   ├── domain/
│   │   │       │   └── infrastructure/
│   │   │       │
│   │   │       ├── configuration/
│   │   │       └── shared/
│   │   │           ├── exception/
│   │   │           ├── response/
│   │   │           └── validation/
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   │           └── migration/
│   │
│   └── test/
│       └── java/
│           └── za/co/propertyrental/
│               ├── identity/
│               ├── listing/
│               ├── location/
│               ├── media/
│               ├── inquiry/
│               └── moderation/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
└── README.md
```

### Root application class

`PropertyRentalApplication.java` is the entry point for Spring Boot.

It starts the application and enables Spring's component scanning and auto-configuration.

Keep this class in the root package, above the feature packages, so Spring can discover the application's components.

### `api/`

Contains the HTTP-facing part of a feature.

Typical contents:

- REST controllers.
- Request DTOs.
- Response DTOs.
- API-specific mappers, when needed.

Controllers handle HTTP requests, validate incoming input and delegate work to application services.

Controllers should not contain business workflows or database queries.

### `application/`

Contains the use cases and orchestration for a feature.

Typical contents:

- Application services or use-case classes.
- Commands and queries, where useful.
- Transaction boundaries.
- Interfaces required to access infrastructure.
- Coordination between domain objects and persistence.

Examples include creating a property listing, updating a listing's availability and retrieving a paginated search result.

An application service coordinates the operation; it should not become a dumping ground for every business rule.

### `domain/`

Contains the core business model and rules.

Typical contents:

- Domain entities.
- Value objects.
- Domain enums.
- Business rules and domain services, where needed.
- Repository interfaces, if using domain-owned repository abstractions.

Examples include a property listing, its rental price, its status and the rules governing valid status transitions.

Domain classes should not depend on controllers, HTTP request objects or Spring Data JPA implementation classes.

### `infrastructure/`

Contains the technical implementations required by a feature.

Typical contents:

- Spring Data JPA repositories.
- Persistence adapters.
- Database-specific queries.
- JPA mapping code, if separate persistence models are required.
- External service adapters.
- Storage and integration implementations.

For a straightforward feature, a domain entity may also serve as a JPA entity. We should introduce separate persistence models only when there is a clear reason.

### `configuration/`

Contains application-wide technical configuration, such as:

- Security configuration.
- OpenAPI configuration.
- CORS configuration.
- Jackson configuration.
- Other Spring configuration beans.

Configuration should remain focused. Feature-specific business logic belongs in the relevant feature module.

### `shared/`

Contains genuinely cross-cutting components used by multiple modules.

Examples:

- Common exception handling.
- Standard API error responses.
- Shared validation utilities.
- Small, stable technical abstractions.

Do not place all DTOs, entities, services or business logic here. Each feature should own its own code.

### `resources/`

Contains non-Java application resources.

- `application.yml`: default application configuration.
- `db/migration/`: versioned Flyway SQL migrations.
- Other resource files required by the application.

Environment-specific secrets should not be committed to this directory.

### `src/test/`

Contains automated tests corresponding to the application's modules.

Test code should follow the same package organisation as the production code where practical.

## Understanding the Layers

### 1. Controller

A controller is the HTTP entry point.

It maps an HTTP request to an application operation and returns an appropriate HTTP response.

Example:

`ListingController.java`

```java
@RestController
@RequestMapping("/api/v1/listings")
public class ListingController {

    private final CreateListingService createListingService;

    public ListingController(
            CreateListingService createListingService) {
        this.createListingService = createListingService;
    }

    @PostMapping
    public ResponseEntity<ListingResponse> create(
            @Valid @RequestBody CreateListingRequest request) {

        ListingResponse response =
                createListingService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
```

This is an illustrative example. The actual implementation will depend on the chosen application-service and DTO design.

The controller should not decide how a listing is persisted or contain the complete listing-creation workflow.

### 2. DTOs

DTO means **Data Transfer Object**.

DTOs define the data exchanged across a boundary, particularly the REST API.

Examples:

- `CreateListingRequest`
- `UpdateListingRequest`
- `ListingResponse`
- `ListingSummaryResponse`

Request DTOs represent incoming data. Response DTOs represent outgoing data.

They help us control which fields clients can submit or see, instead of exposing database entities directly.

DTOs generally belong in the feature's `api/` package when they represent HTTP contracts. Internal application commands and results can be placed in `application/` if they serve a distinct purpose.

### 3. Application services

Application services implement or coordinate use cases.

Examples:

- `CreateListingService`
- `UpdateListingService`
- `GetListingService`
- `SearchListingsService`

They coordinate validation, domain operations, repository calls and other required components.

Use-case classes should have clear responsibilities. We do not need to create an interface and implementation pair for every service unless it provides a real benefit.

### 4. Entities

An entity represents an object with identity and a lifecycle.

For example, a property listing has an identifier that remains the same when its description or rental price changes.

Entities generally belong in `domain/` when they represent business concepts. If we use JPA annotations directly on these classes, they can also serve as persistence entities.

Keep the distinction clear:

- **Domain entity:** represents a business concept and its rules.
- **JPA entity:** represents an object mapped to database tables.

A class can serve both roles in a pragmatic design, but they are conceptually different responsibilities.

### 5. Repositories

Repositories provide access to stored data.

For example, a listing repository may support operations to save a listing, retrieve it by ID and execute database queries.

With Spring Data JPA, an implementation can often be generated automatically:

```java
public interface ListingJpaRepository
        extends JpaRepository<ListingEntity, UUID> {
}
```

The exact package and type depend on whether we use a JPA entity directly as the domain entity or introduce a separate persistence model.

In this architecture, Spring Data repository implementations belong in `infrastructure/`. If a domain-owned repository interface is needed, it belongs in `domain/`, with its implementation in `infrastructure/`.

We should not create unnecessary repository abstractions for simple CRUD operations.

### 6. Validation

Validation occurs at more than one level.

- **Request validation:** checks incoming fields, such as required descriptions and positive rental prices.
- **Business validation:** enforces rules that must remain true regardless of how the operation is called.
- **Database constraints:** protect data integrity at the persistence level.

Use Jakarta Bean Validation for request constraints and enforce important business invariants within the domain or application logic.

### 7. Exception handling

Expected failures should be translated into consistent API responses.

Examples include:

- Invalid request: `400 Bad Request`.
- Unauthenticated request: `401 Unauthorized`.
- Insufficient permission: `403 Forbidden`.
- Listing not found: `404 Not Found`.
- Conflict with the current resource state: `409 Conflict`.

A global exception handler can use Spring's `@RestControllerAdvice` to produce a consistent error format.

Never return stack traces, database credentials or sensitive internal details to clients.

## Where Traditional Spring Boot Concepts Fit

Developers familiar with the traditional structure can use this mapping:

| Traditional structure | Location in this architecture |
|---|---|
| `controllers/` | Feature-specific `api/` |
| `services/` | Feature-specific `application/` |
| `repositories/` | `infrastructure/`, or a domain interface plus infrastructure implementation |
| `entities/` | Usually feature-specific `domain/`; separate JPA models may live in `infrastructure/` |
| `dtos/` | Feature-specific `api/` for HTTP DTOs; `application/` for internal commands/results |
| `config/` | `configuration/` |
| `exceptions/` | `shared/exception/` for cross-cutting errors; feature-specific exceptions where appropriate |
| `mappers/` | Near the feature and boundary they map, such as `api/` or `infrastructure/` |
| `validators/` | Request validation in `api/`; business rules in the domain/application layer |

The main difference is organisation: **feature first, responsibility second**.

This makes the property listing feature easier to navigate without forcing developers to search through application-wide folders.

## Request Lifecycle

A typical request to create a property listing follows this path:

1. The client sends a `POST /api/v1/listings` request.
2. `ListingController` receives the request.
3. Spring validates the request DTO.
4. The application service coordinates the create-listing use case.
5. Domain rules are applied.
6. The persistence component saves the listing to PostgreSQL.
7. The application returns the result.
8. The controller returns an HTTP response with the appropriate status and response DTO.

```text
HTTP Request
     |
     v
ListingController
     |
     v
CreateListingService
     |
     v
Domain Rules
     |
     v
Listing Repository
     |
     v
PostgreSQL
     |
     v
ListingResponse
     |
     v
HTTP Response
```

The actual order can vary depending on the operation. For example, ownership and authorisation checks must happen before allowing a user to modify a listing.

## Module Responsibilities

### `identity/`

Responsible for user accounts and access control.

Potential contents include registration, authentication, roles, account status and current-user access.

Authentication and authorisation are related but different: authentication establishes who the user is; authorisation determines what that user may do.

### `listing/`

Responsible for the rental listing lifecycle.

Potential contents include property descriptions, rent, deposits, amenities, listing status, publication and owner-specific updates.

This is one of the core MVP modules.

### `location/`

Responsible for location information used to describe and search for properties.

Potential contents include provinces, cities, suburbs and location-related queries.

We should choose the level of location normalisation based on actual search and data-quality requirements rather than creating an unnecessarily complicated geographic model.

### `media/`

Responsible for property photos and related media metadata.

The database can store metadata and storage references, while image files are stored in an appropriate file or object-storage service.

### `inquiry/`

Responsible for enquiries from prospective tenants to property owners.

The initial version may simply record or facilitate enquiries. Full internal messaging can be introduced later if required.

### `moderation/`

Responsible for reporting and reviewing suspicious or inappropriate listings, moderation decisions and relevant administrative actions.

## Architecture and Dependency Rules

To keep the application maintainable:

1. Controllers should delegate to application operations rather than implement business workflows.
2. Domain logic should not depend on HTTP or database implementation details.
3. Modules should not access another module's repositories or internal classes directly.
4. Cross-module interaction should use explicit interfaces or well-defined application operations.
5. API DTOs should not be used as JPA entities.
6. Database entities should not be returned directly from REST controllers.
7. Transactions should be defined around appropriate application use cases.
8. Configuration and secrets must not be hardcoded.
9. Avoid introducing abstractions that do not solve a real problem.
10. Keep the application deployable as one unit until independent deployment becomes a genuine requirement.

These rules are conventions we should reinforce through code review and, where worthwhile, automated architecture tests.

## Database and Migrations

PostgreSQL is the primary relational database.

Flyway will manage schema changes using versioned migration scripts stored in:

`src/main/resources/db/migration/`

Example:

```text
db/migration/
├── V1__create_initial_tables.sql
├── V2__create_listing_tables.sql
└── V3__add_listing_status.sql
```

These filenames are examples; actual migrations should reflect the schema changes we implement.

Important rules:

- Never edit an already-applied migration in a shared environment; create a new migration instead.
- Use migrations to create and change the database schema.
- Do not rely on Hibernate automatic schema creation for production deployments.
- Use appropriate primary keys, foreign keys, indexes and database constraints.
- Keep credentials outside source control.
- Agree on ownership of tables between modules before implementing complex cross-module relationships.

Although the initial modules may use one PostgreSQL database, they should retain clear ownership of their data and schema changes.

## API Design Conventions

REST endpoints should use predictable resource-oriented URLs.

Examples:

| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/api/v1/listings` | Search or list properties |
| `POST` | `/api/v1/listings` | Create a listing |
| `GET` | `/api/v1/listings/{id}` | Retrieve a listing |
| `PATCH` | `/api/v1/listings/{id}` | Partially update a listing |
| `DELETE` | `/api/v1/listings/{id}` | Remove a listing |

These are proposed endpoints, not a claim that they have already been implemented.

API conventions should include:

- A consistent JSON response format.
- Standard error responses.
- Pagination for listing searches.
- Input validation.
- Authentication and authorisation for protected operations.
- API documentation through OpenAPI.
- Appropriate HTTP status codes.
- Versioning when needed to manage breaking changes.

Do not expose internal Java class names or database implementation details as public API contracts.

## Testing Strategy

Testing should be organised around the responsibility being tested.

### Unit tests

Test domain rules and application operations without requiring a running PostgreSQL instance wherever practical.

### Web/API tests

Verify request validation, HTTP status codes, response formats, security rules and exception handling.

### Persistence tests

Verify JPA mappings, queries, constraints and database interactions.

### Integration tests

Test important workflows across the application, including PostgreSQL integration and authentication-protected listing operations.

Use a real PostgreSQL test environment, such as Testcontainers, for tests where database-specific behaviour matters.

Tests should cover both successful operations and failure cases, especially ownership checks, invalid data and unauthorised access.

## Configuration and Environment Variables

Keep configuration separate from source code.

For local development, configure database settings through environment variables or an ignored local configuration file.

Typical variables might include:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

The actual names will be defined in `application.yml`.

Never commit production credentials, signing secrets or real user data. Provide an `.env.example` or documented placeholder configuration when it helps developers set up their local environment.

Production secrets should be supplied through the deployment environment or a suitable secrets manager.

## Getting Started

### Prerequisites

Install:

- A supported Java LTS version compatible with the selected Spring Boot release.
- Git.
- PostgreSQL, either locally or through a development container.
- An IDE such as IntelliJ IDEA or VS Code.

Maven Wrapper scripts should be used to build the project consistently.

### Run locally

Once the Spring Boot project and database configuration have been created:

1. Clone the repository.
2. Navigate to `app/backend`.
3. Configure the local database and environment variables.
4. Run the Maven tests.
5. Start the application.

Example commands:

```bash
cd app/backend
./mvnw test
./mvnw spring-boot:run
```

On Windows PowerShell, use:

```powershell
cd app/backend
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

The application port, database configuration and API documentation URL will be confirmed in the project configuration.

## Git and Contribution Guidelines

- Keep changes focused on one feature or responsibility.
- Use clear branch names, such as `feature/listing-search` or `fix/listing-validation`.
- Write descriptive commit messages.
- Add or update tests when changing behaviour.
- Update API documentation when changing public endpoints.
- Never commit credentials or environment-specific secrets.
- Request review for changes that affect shared architecture, database schema or API contracts.

Because the backend and frontend are developed by different contributors, API changes should be documented and communicated before integration.

## Future Scalability

The modular monolith is the starting point, not a commitment to remain monolithic forever.

If the platform grows, individual business capabilities may eventually become separate services. That decision should be based on real requirements such as independent scaling, deployment needs, team ownership or operational constraints.

To prepare without over-engineering:

- Keep module responsibilities clear.
- Minimise dependencies between feature internals.
- Define explicit integration boundaries.
- Avoid sharing database access indiscriminately across modules.
- Keep API contracts stable and documented.
- Add messaging or distributed infrastructure only when a real use case requires it.

We may eventually support additional applications under the same business umbrella. Each application should have clear domain boundaries and integrate through defined contracts rather than sharing internal implementation code.

## Guiding Principle

**Build simple, maintainable software today while preserving the ability to evolve tomorrow.**

Our priority is a reliable rental platform with clear code ownership, well-defined APIs, meaningful tests and a structure that new contributors can understand without unnecessary complexity.