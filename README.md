# Property Rental Platform 🇿🇦

**A simple, accessible and free way to discover and list rental properties across South Africa.**

Property Rental Platform is a collaborative software project designed to connect property owners with people looking for rental accommodation. It aims to make finding a place to rent easier by allowing users to explore available properties, compare options and contact property owners through one platform.

Our goal is to make property discovery more accessible, transparent and convenient for South Africans.

## About the Project

Finding suitable rental accommodation can be challenging. Property seekers often need to search across different platforms, compare prices manually and contact multiple property owners to find a place that meets their needs.

Property Rental Platform aims to simplify this process by providing one central place where property owners can publish rental listings and tenants can search for accommodation that suits their budgets and preferences.

The platform is being developed as a collaborative, full-stack application with a focus on usability, maintainability, security and future growth.

> **Project status:** Development is ongoing. Features will be introduced progressively, so planned functionality should not be assumed to be available yet.

## Planned Features

### For Property Seekers

- Browse available rental properties.
- Search by province, city and suburb.
- Filter listings by rental price, property type and facilities.
- View property descriptions, photographs and rental details.
- Contact property owners about available properties.
- Save favourite listings for later.
- Report suspicious or misleading listings.

### For Property Owners

- Create and manage property listings.
- Upload property photographs.
- Update rental prices and property details.
- Mark properties as available or unavailable.
- Receive and manage enquiries from interested tenants.

### Future Enhancements

- Interactive maps and location-based property discovery.
- Notifications for new listings and enquiry updates.
- Student accommodation discovery.
- Listing verification and enhanced moderation.
- Optional featured listings and other sustainable monetisation features.

*Features will be introduced progressively as development continues.*

## Technology Stack

The project currently uses the following technologies and tools:

| Area | Technology |
|---|---|
| Frontend | Angular |
| Frontend Workspace | Nx |
| Backend Language | Java |
| Backend Framework | Spring Boot |
| Database | PostgreSQL |
| Database Migrations | Flyway |
| Data Persistence | Spring Data JPA / Hibernate |
| API Style | REST API |
| Build Tool | Maven Wrapper |
| Version Control | Git and GitHub |

The technology stack may evolve as the project develops. Refer to the relevant application configuration files for the exact versions in use.

## Repository Structure

```text
Property-Rental-Platform/
├── app/
│   ├── frontend/
│   │   └── README.md
│   └── backend/
│       ├── README.md
│       ├── pom.xml
│       └── src/
├── docs/
│   ├── backend-onboarding.md
│   ├── backend-architecture.md
│   └── database-design.md
├── .gitignore
└── README.md
```

The repository separates the frontend and backend to support independent development while keeping the entire platform in one place.

- **`app/frontend/`** - The user interface, application pages and client-side functionality.
- **`app/backend/`** - The REST API, business logic, database access and backend functionality.
- **`docs/`** - Additional technical documentation for developers.
- **`README.md`** - The main project overview and entry point for contributors.

The structure may expand as additional features and components are implemented.

## Database Design

The database model describes the relationships between users, properties, rental units, locations, facilities, images, enquiries, favourites and listing reports.

Explore the [Property Rental Platform ERD](https://mermaid.ai/d/0c5aea50-44c2-427a-9ea2-34ab34ee3657).

For more information, see the [Database Design Guide](docs/database-design.md).

## Getting Started

### Prerequisites

Install the tools required for the part of the project you want to work on.

**General tools**
- Git
- A GitHub account

**Frontend**
- Node.js and npm
- The tooling required by the Nx workspace

**Backend**
- Java Development Kit (JDK) 24, matching the current Maven configuration
- PostgreSQL

The backend includes the Maven Wrapper, so a separate Maven installation is normally unnecessary.

### 1. Clone the Repository

```bash
git clone https://github.com/MukhubaM/Property-Rental-Platform.git
cd Property-Rental-Platform
```

### 2. Set Up the Backend

Navigate to the backend directory:

```bash
cd app/backend
```

Follow the [Backend Onboarding Guide](docs/backend-onboarding.md) for instructions on:

- Creating the local PostgreSQL database.
- Configuring your local database credentials.
- Running Flyway migrations.
- Starting the Spring Boot application.
- Running tests and building the backend.

### 3. Understand the Backend Architecture

Contributors working on the backend should review these guides:

- [Backend Architecture Guide](docs/backend-architecture.md) - explains the package structure, feature modules and architecture layers.
- [Database Design Guide](docs/database-design.md) - explains the database tables, relationships and migration practices.

### 4. Set Up the Frontend

Navigate to the frontend directory from the repository root:

```bash
cd app/frontend
```

Follow the [Frontend README](app/frontend/README.md) for the current frontend setup and development instructions.

## Architecture and Development Principles

The platform follows these design principles:

- **Separation of concerns:** Keep presentation, business logic and data access responsibilities separate.
- **Modular design:** Organise functionality into clear, maintainable feature areas.
- **API-first integration:** Connect the frontend and backend through clearly defined REST API contracts.
- **Security by design:** Protect user information and validate incoming requests.
- **Maintainability:** Prefer readable, testable code over unnecessary complexity.
- **Scalability:** Build a reliable foundation that can evolve as the platform grows.
- **Collaboration:** Use consistent Git practices and communicate changes clearly.

The backend follows a **modular monolith architecture**, keeping related business features organised within one deployable Spring Boot application.

Backend features are organised by business area, including identity, listings, locations, media, enquiries and moderation. Within these areas, code can be separated into API, application, domain and infrastructure layers.

For more detail, see the [Backend Architecture Guide](docs/backend-architecture.md).

## Contributing

Contributions and constructive feedback are welcome.

Before contributing:

1. Review the relevant README and technical documentation.
2. Coordinate with the project maintainers if you need repository access.
3. Create a branch for your changes.
4. Keep changes focused and follow the project's existing conventions.
5. Run relevant tests and check your changes.
6. Open a pull request with a clear description of what changed and why.

Follow the repository's configured branch and pull request rules. Do not assume that a change is approved or ready to merge until the required reviews and checks have passed.

### Suggested Branch Naming

```text
feature/property-search
feature/user-authentication
fix/listing-validation
docs/update-readme
```

### Commit Message Examples

```text
feat: add property search filters
fix: validate rental listing details
docs: update backend setup instructions
test: add listing service tests
```

Please keep changes focused and never commit real passwords, API keys, database credentials or other sensitive information.

## Development Roadmap

The roadmap tracks major project milestones and may change as development progresses.

- [x] Establish the initial backend project structure.
- [x] Configure PostgreSQL and Flyway migrations.
- [x] Create the initial database schema and lookup data.
- [x] Add initial backend architecture and database documentation.
- [ ] Establish and document frontend onboarding.
- [ ] Define and implement API contracts.
- [ ] Implement user registration and authentication.
- [ ] Implement property and rental-unit listing management.
- [ ] Build property browsing, search and filtering.
- [ ] Add property images and owner enquiries.
- [ ] Introduce favourites and listing reports.
- [ ] Expand automated testing and security controls.
- [ ] Prepare deployment and production configuration.

*Checklist items should be updated as the corresponding work is completed and verified in the repository.*

## Project Vision

We want to build more than a property listing website. We want to create a practical platform that helps people find suitable accommodation while giving property owners a straightforward way to reach potential tenants.

By keeping the platform accessible, maintaining a clean technical foundation and developing around real user needs, we aim to build something useful for communities across South Africa.

## License

The project's licence will be specified here once the maintainers have selected and added a licence file. Until then, please do not assume that the source code is licensed for unrestricted reuse.

## Project Team

Built collaboratively by developers working toward a more accessible rental-property experience in South Africa.

---

**Made for South Africa 🇿🇦**
