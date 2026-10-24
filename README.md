# Property Rental Platform 🇿🇦

**A simple, accessible, and free way to discover and list rental properties across South Africa.**

Property Rental Platform is a collaborative software project designed to connect property owners with people looking for rental accommodation. It aims to make finding a place to rent easier by allowing users to explore available properties, compare options, and contact property owners through one platform.

Our goal is to make property discovery more accessible, transparent, and convenient for South Africans.

## About the Project

Finding suitable rental accommodation can be challenging. Property seekers often need to search across different platforms, compare prices manually, and contact multiple property owners to find a place that meets their needs.

Property Rental Platform aims to simplify this process by providing one central place where property owners can publish rental listings and tenants can search for accommodation that suits their budgets and preferences.

The platform is being developed as a collaborative, full-stack application with a focus on usability, maintainability, security, and future growth.

## Planned Features

### For Property Seekers
- Browse available rental properties.
- Search by province, city, and suburb.
- Filter listings by rental price, property type, and facilities.
- View property descriptions, photographs, and rental details.
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

The project is being developed using the following technologies:

| Area | Technology |
|---|---|
| Frontend | Angular |
| Frontend Workspace | Nx |
| Backend | Java and Spring Boot |
| Database | PostgreSQL |
| Database Migrations | Flyway |
| API Style | REST API |
| Version Control | Git and GitHub |

The technology stack may evolve as the project develops.

## Repository Structure

```text
Property-Rental-Platform/
├── app/
│   ├── frontend/
│   │   └── README.md
│   └── backend/
│       └── README.md
├── .gitignore
└── README.md
```

The repository separates the frontend and backend to support independent development while keeping the entire platform in one place.

- **`app/frontend/`** - The user interface, application pages, and client-side functionality.
- **`app/backend/`** - The REST API, business logic, data persistence, and backend services.
- **`README.md`** - The main project overview and entry point for contributors.

Additional source folders and configuration files will be added as development progresses.

## ERD
https://mermaid.ai/d/0c5aea50-44c2-427a-9ea2-34ab34ee3657

## Getting Started

### Prerequisites

Before contributing, install the tools required for the part of the project you want to work on.

**General tools**
- Git
- A GitHub account

**Frontend**
- Node.js and npm
- Angular CLI / Nx tooling as required by the project

**Backend**
- Java Development Kit (JDK)
- Maven
- PostgreSQL

### 1. Clone the Repository

```bash
git clone https://github.com/MukhubaM/Property-Rental-Platform.git
cd Property-Rental-Platform
```

### 2. Choose Your Application

**Frontend**

```bash
cd app/frontend
```

See the [Frontend README](app/frontend/README.md) for frontend setup instructions.

**Backend**

```bash
cd app/backend
```

See the [Backend README](app/backend/README.md) for backend setup, database configuration, and development instructions.

> Setup instructions will be expanded as the application components become available.

## Architecture and Development Principles

The platform is intended to follow these principles:

- **Separation of concerns:** Keep presentation, business logic, and data access responsibilities separate.
- **Modular design:** Organise functionality into clear, maintainable feature areas.
- **API-first integration:** Connect the frontend and backend through clearly defined REST API contracts.
- **Security by design:** Protect user information and validate incoming requests.
- **Maintainability:** Prefer readable, testable code over unnecessary complexity.
- **Scalability:** Build a reliable foundation that can evolve as the platform grows.
- **Collaboration:** Use consistent Git practices and communicate changes clearly.

The backend will initially follow a modular monolith architecture, keeping related features organised within one deployable application.

## Contributing

Contributions and constructive feedback are welcome.

To contribute:

1. Fork the repository or coordinate with the project maintainers for direct access.
2. Create a branch for your changes.
3. Make focused changes that follow the project's existing conventions.
4. Test your changes before submitting them.
5. Open a pull request with a clear description of what you changed and why.

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
```

Please keep changes focused and avoid committing passwords, API keys, database credentials, or other sensitive information.

## Development Roadmap

- [ ] Establish the frontend and backend project structures.
- [ ] Configure the backend and PostgreSQL database.
- [ ] Define the initial database schema and API contracts.
- [ ] Implement user registration and authentication.
- [ ] Implement property listing creation and management.
- [ ] Build property browsing, search, and filtering.
- [ ] Add property images and owner enquiries.
- [ ] Introduce favourites and listing reports.
- [ ] Improve testing, security, and deployment.
- [ ] Explore future enhancements based on user feedback.

Roadmap items may change as the project evolves.

## Project Vision

We want to build more than a property listing website. We want to create a practical platform that helps people find suitable accommodation while giving property owners a straightforward way to reach potential tenants.

By keeping the platform accessible, maintaining a clean technical foundation, and developing around real user needs, we aim to build something useful for communities across South Africa.

## License

The project's licence will be specified here once the maintainers have selected and added a licence file. Until then, please do not assume that the source code is licensed for unrestricted reuse.

## Project Team

Built collaboratively by developers working toward a more accessible rental-property experience in South Africa.

---

**Made for South Africa 🇿🇦**
