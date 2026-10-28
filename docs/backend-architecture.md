# Backend Architecture Guide

## 1. Overview

The Property Rental Platform backend is a **modular monolith**: one backend application organised into business-focused modules. The main Java package is `propertyrental.platform`, and the entry point is `PlatformApplication.java`.

## 2. Top-level structure

```text
app/backend/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── src/
    ├── main/
    │   ├── java/propertyrental/platform/
    │   │   ├── PlatformApplication.java
    │   │   ├── configuration/
    │   │   ├── identity/
    │   │   ├── inquiry/
    │   │   ├── listing/
    │   │   ├── location/
    │   │   ├── media/
    │   │   ├── moderation/
    │   │   └── shared/
    │   └── resources/
    │       ├── application.properties
    │       └── db/migration/
    └── test/java/propertyrental/platform/
        └── PlatformApplicationTests.java
```

Some folders currently contain placeholders. This is a high-level structure, not a claim that all features have implemented classes.

## 3. Feature modules

| Module | Responsibility | Related ERD tables |
|---|---|---|
| `identity` | User records, roles and external identities | `users`, `roles`, `user_roles`, `external_identities` |
| `listing` | Properties, rental units, listing types and statuses | `properties`, `rental_units`, `property_types`, `listing_statuses`, `unit_types`, `availability_statuses` |
| `location` | Geographic lookup data | `provinces`, `cities`, `suburbs` |
| `media` | Property and unit images | `property_images`, `unit_images` |
| `inquiry` | Enquiries and saved units | `inquiries`, `inquiry_statuses`, `favourites` |
| `moderation` | Reports and review statuses | `listing_reports`, `report_reasons`, `report_statuses` |
| `configuration` | Application-wide Spring configuration | No single feature-specific table |
| `shared` | Truly cross-feature components | No single feature-specific table |

This is a conceptual ownership map. Update it if implementation decisions change.

## 4. Layers within a feature

### `api`
HTTP controllers and request/response DTOs. Receive and validate requests, call application use cases, and return HTTP responses. Avoid complex business logic in controllers.

### `application`
Coordinates use cases, domain behaviour, repositories and transactions. Keep HTTP-specific details out of application workflows where practical.

### `domain`
Core business concepts, rules and invariants. Keep domain rules as independent as practical from web and database implementation details.

### `infrastructure`
Technical implementations such as persistence adapters, Spring Data repositories and external integrations.

Add classes to these layers as functionality is implemented.

## 5. Typical request flow

```text
HTTP request
    ↓
API controller
    ↓
Application use case
    ↓
Domain rules / domain objects
    ↓
Infrastructure persistence adapter
    ↓
PostgreSQL
```

The response returns through the application and API layers. This is the intended flow; confirm implementation details as features are added.

## 6. Property vs rental unit

A **property** represents the parent premises or listing. A **rental unit** represents the rentable space associated with that property. One property can contain multiple units, such as individual rooms. Depending on the supported unit type and business rules, a unit can represent a whole property, flat, room, shared room, studio, cottage or another category.

Property-level and unit-level images and facilities are represented separately. Do not treat a property and rental unit as interchangeable.

## 7. Architecture rules

- Keep code in the feature that owns the business capability.
- Keep controllers thin; coordinate workflows in the application layer.
- Keep persistence-specific code in infrastructure.
- Use `shared` only for genuinely reusable cross-feature functionality.
- Keep application-wide Spring configuration in `configuration`.
- Use Flyway for schema changes.
- Add tests for important business rules and API behaviour.
- Do not assume a feature is implemented simply because its directory exists.

## 8. Database management

Migrations are under `src/main/resources/db/migration/`. The current migrations are `V1__create_initial_schema.sql` and `V2__seed_lookup_data.sql`. Add a new versioned migration for future changes; do not edit migrations already applied to an existing database. Hibernate uses `ddl-auto=validate`.

## 9. Current technology stack

The current `pom.xml` specifies Java 24 and Spring Boot 4.1.2, with Spring Web MVC, Spring Data JPA, PostgreSQL JDBC, Flyway, Lombok, test dependencies and the Maven Wrapper.

Update this guide and the onboarding guide if the team changes the supported Java or Spring Boot version.
