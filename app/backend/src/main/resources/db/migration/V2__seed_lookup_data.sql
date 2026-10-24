-- V2__seed_lookup_data.sql
-- Initial reference data for Property Rental Platform.

-- SOUTH AFRICAN PROVINCES
INSERT INTO provinces (code, name)
    VALUES
       ('EC', 'Eastern Cape'),
       ('FS', 'Free State'),
       ('GP', 'Gauteng'),
       ('KZN', 'KwaZulu-Natal'),
       ('LP', 'Limpopo'),
       ('MP', 'Mpumalanga'),
       ('NC', 'Northern Cape'),
       ('NW', 'North West'),
       ('WC', 'Western Cape')
    ON CONFLICT (code) DO UPDATE
    SET name = EXCLUDED.name;

-- USER ROLES
INSERT INTO roles (code, name)
    VALUES
        ('TENANT', 'Tenant'),
        ('PROPERTY_OWNER', 'Property Owner'),
        ('MODERATOR', 'Moderator'),
        ('ADMIN', 'Administrator')
    ON CONFLICT (code) DO UPDATE
    SET name = EXCLUDED.name;

-- PROPERTY TYPES
-- Describes the overall physical property.
INSERT INTO property_types (code, name)
    VALUES
        ('HOUSE', 'House'),
        ('APARTMENT_BUILDING', 'Apartment Building'),
        ('FLAT', 'Flat'),
        ('TOWNHOUSE', 'Townhouse'),
        ('GUESTHOUSE', 'Guesthouse'),
        ('COTTAGE_PROPERTY', 'Cottage Property'),
        ('OTHER', 'Other')
    ON CONFLICT (code) DO UPDATE
    SET name = EXCLUDED.name;

-- LISTING STATUSES
-- Controls the publication state of a property listing.
INSERT INTO listing_statuses (code, name)
    VALUES
        ('DRAFT', 'Draft'),
        ('PUBLISHED', 'Published'),
        ('PAUSED', 'Paused'),
        ('ARCHIVED', 'Archived')
    ON CONFLICT (code) DO UPDATE
    SET name = EXCLUDED.name;

-- RENTAL UNIT TYPES
-- Describes what the owner is offering for rent.
INSERT INTO unit_types (code, name)
    VALUES
        ('WHOLE_PROPERTY', 'Whole Property'),
        ('FLAT', 'Flat'),
        ('ROOM', 'Room'),
        ('SHARED_ROOM', 'Shared Room'),
        ('STUDIO', 'Studio'),
        ('COTTAGE', 'Cottage'),
        ('OTHER', 'Other')
    ON CONFLICT (code) DO UPDATE
    SET name = EXCLUDED.name;


-- RENTAL UNIT AVAILABILITY
INSERT INTO availability_statuses (code, name)
    VALUES
        ('AVAILABLE', 'Available'),
        ('OCCUPIED', 'Occupied'),
        ('RESERVED', 'Reserved'),
        ('UNAVAILABLE', 'Unavailable')
    ON CONFLICT (code) DO UPDATE
    SET name = EXCLUDED.name;

-- FACILITIES
-- Facilities may be assigned to properties or units.
INSERT INTO facilities (code, name)
    VALUES
        ('WIFI', 'Wi-Fi'),
        ('PARKING', 'Parking'),
        ('FURNISHED', 'Furnished'),
        ('SWIMMING_POOL', 'Swimming Pool'),
        ('GARDEN', 'Garden'),
        ('SECURITY', 'Security'),
        ('CCTV', 'CCTV'),
        ('ELECTRIC_FENCE', 'Electric Fence'),
        ('BACKUP_POWER', 'Backup Power'),
        ('WATER_TANK', 'Water Tank'),
        ('LAUNDRY', 'Laundry Facilities'),
        ('DISHWASHER', 'Dishwasher'),
        ('AIR_CONDITIONING', 'Air Conditioning'),
        ('PRIVATE_BATHROOM', 'Private Bathroom'),
        ('SHARED_BATHROOM', 'Shared Bathroom'),
        ('PRIVATE_KITCHEN', 'Private Kitchen'),
        ('SHARED_KITCHEN', 'Shared Kitchen'),
        ('BALCONY', 'Balcony'),
        ('PET_FRIENDLY', 'Pet Friendly'),
        ('WHEELCHAIR_ACCESS', 'Wheelchair Accessible')
    ON CONFLICT (code) DO UPDATE
    SET name = EXCLUDED.name;


-- INQUIRY STATUSES
INSERT INTO inquiry_statuses (code, name)
    VALUES
        ('NEW', 'New'),
        ('IN_PROGRESS', 'In Progress'),
        ('RESPONDED', 'Responded'),
        ('CLOSED', 'Closed')
    ON CONFLICT (code) DO UPDATE
    SET name = EXCLUDED.name;


-- LISTING REPORT REASONS
INSERT INTO report_reasons (code, name)
    VALUES
        ('SUSPECTED_SCAM', 'Suspected Scam'),
        ('INCORRECT_INFORMATION', 'Incorrect Information'),
        ('DUPLICATE_LISTING', 'Duplicate Listing'),
        ('PROHIBITED_CONTENT', 'Prohibited Content'),
        ('DISCRIMINATION', 'Discrimination'),
        ('OTHER', 'Other')
    ON CONFLICT (code) DO UPDATE
    SET name = EXCLUDED.name;


-- LISTING REPORT STATUSES
INSERT INTO report_statuses (code, name)
    VALUES
        ('OPEN', 'Open'),
        ('UNDER_REVIEW', 'Under Review'),
        ('ACTION_TAKEN', 'Action Taken'),
        ('DISMISSED', 'Dismissed')
    ON CONFLICT (code) DO UPDATE
    SET name = EXCLUDED.name;
