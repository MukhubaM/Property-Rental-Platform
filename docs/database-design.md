# Database Design Guide

## 1. Purpose and source of truth

This guide describes the database model shown in the Property Rental Platform ERD. The database uses PostgreSQL, and Flyway migration files define the schema and initial lookup data:

- `app/backend/src/main/resources/db/migration/V1__create_initial_schema.sql`
- `app/backend/src/main/resources/db/migration/V2__seed_lookup_data.sql`

If the ERD, this guide and the SQL migrations differ, inspect the migrations and live schema before changing anything. Update the ERD and documentation to match the agreed schema.

## 2. Main areas

The schema supports user identity and roles, properties and rentable units, geographic lookup data, images and facilities, favourites and enquiries, and user reports/moderation.

## 3. Table reference

### Identity and access

| Table | Purpose |
|---|---|
| `users` | User profile and account fields such as name, email, phone number, active status and timestamps. The ERD shows `password_hash` as nullable. |
| `roles` | Lookup table for application roles. |
| `user_roles` | Join table between users and roles; the ERD shows a composite primary key. |
| `external_identities` | Associates a user with an external identity provider using provider and provider-subject details. |

### Location

| Table | Purpose |
|---|---|
| `provinces` | Province lookup data. |
| `cities` | Cities associated with provinces. |
| `suburbs` | Suburbs associated with cities. |

The hierarchy is `province → city → suburb`. The ERD shows property province and city references, with suburb optional.

### Listings and rental units

| Table | Purpose |
|---|---|
| `properties` | Parent premises/listing, including owner, type, listing status, location, title, description and address/location details. |
| `property_types` | Lookup values for property categories. |
| `listing_statuses` | Lookup values for property listing lifecycle/status. |
| `rental_units` | Individual rentable spaces associated with a property, including unit type, availability, name, description, monthly rent, available-from date and furnishing flag. |
| `unit_types` | Lookup values for rental-unit categories. |
| `availability_statuses` | Lookup values for unit availability. |

**Property vs rental unit:** a property represents the parent premises; a rental unit represents a rentable space. A property may contain multiple rental units. Keep property-level and unit-level information distinct.

### Facilities and images

| Table | Purpose |
|---|---|
| `facilities` | Lookup catalogue of amenities/facilities. |
| `property_facilities` | Join table between properties and facilities. |
| `unit_facilities` | Join table between rental units and facilities. |
| `property_images` | Property image references and metadata, including URL, alt text and display order. |
| `unit_images` | Rental-unit image references and metadata, including URL, alt text and display order. |

Separate join and image tables allow property-level information to differ from unit-level information.

### Enquiries and favourites

| Table | Purpose |
|---|---|
| `inquiries` | User enquiries about rental units, including sender, status, message and timestamps. |
| `inquiry_statuses` | Lookup values for enquiry workflow status. |
| `favourites` | Join table recording which users have saved which rental units, with a creation timestamp. |

### Moderation

| Table | Purpose |
|---|---|
| `listing_reports` | User-submitted reports, including reporter, optional property target, optional unit target, reason, status, description and timestamps. |
| `report_reasons` | Lookup values for report reasons. |
| `report_statuses` | Lookup values for report review progress. |

## 4. Main relationships

The ERD shows these important relationships:

- One user can own multiple properties.
- One property can contain multiple rental units.
- Properties and units can each have multiple images.
- Users and roles have a many-to-many relationship through `user_roles`.
- Users can be associated with external identity records.
- Provinces contain cities; cities contain suburbs.
- Properties reference an owner, property type, listing status and location.
- Rental units reference their parent property, unit type and availability status.
- Properties and rental units can each be associated with multiple facilities.
- Users can save rental units through `favourites`.
- Users can submit enquiries about rental units through `inquiries`.
- Users can submit reports through `listing_reports`.

Confirm exact cardinality, optionality, uniqueness and delete behaviour against `V1__create_initial_schema.sql` before changing constraints.

## 5. Integrity rules to preserve

### Lookup values
Use the existing lookup tables for property types, listing statuses, unit types, availability statuses, enquiry statuses, report reasons, report statuses and facilities. Add new values through a migration.

### Report target
The ERD suggests that a report targets either a property or a rental unit. Confirm that the SQL migration enforces exactly one target with a database constraint.

### Location consistency
A city should belong to a province, and a suburb should belong to a city. Check the migration's foreign keys and any composite constraints before changing location relationships.

### Join-table duplicates
Join tables such as `user_roles`, `property_facilities`, `unit_facilities` and `favourites` should have appropriate composite primary keys or unique constraints to prevent duplicate links.

### Property/unit availability
A property can contain multiple units. Rules preventing conflicts between an entire-property rental and a separately rented room may need application-level validation unless explicitly enforced by the database.

### Migration discipline
Use a new versioned Flyway migration for each schema change. Do not edit migrations already applied to an existing database. Use the next available version, such as `V3__describe_the_change.sql`.

## 6. Initial lookup data

`V2__seed_lookup_data.sql` is intended to insert initial lookup data, including South African provinces, user roles, property types, listing statuses, unit types, availability statuses, facilities, enquiry statuses, report reasons and report statuses. Refer to the SQL migration for the authoritative codes and names.

## 7. Updating the database model

When changing the schema:

1. Identify the feature and tables affected.
2. Review the existing migration and live schema.
3. Determine whether the change needs a table, column, index, foreign key, constraint or lookup value.
4. Add a new Flyway migration with the next available version.
5. Update entity mappings and application logic where needed.
6. Test the migration locally.
7. Update the ERD and this guide.
8. Include the migration and relevant tests in the pull request.

## 8. Scope notes

The ERD describes the database model, not necessarily every implemented endpoint or UI feature. Do not assume a feature is complete merely because its table exists. The current model focuses on listings, rental units, locations, enquiries, favourites, images, facilities and moderation; lease management and rental payments should not be assumed to exist unless added to the schema and implementation.
