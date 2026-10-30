-- V4__alter_lookup_ids_to_bigint.sql
-- Upgrade all SMALLINT lookup table Primary Keys and their referencing Foreign Keys to BIGINT.

-- 1. DROP FOREIGN KEY CONSTRAINTS
ALTER TABLE cities DROP CONSTRAINT fk_cities_province;
ALTER TABLE properties DROP CONSTRAINT fk_properties_city_province;

ALTER TABLE properties DROP CONSTRAINT fk_properties_type;

ALTER TABLE properties DROP CONSTRAINT fk_properties_listing_status;

ALTER TABLE rental_units DROP CONSTRAINT fk_rental_units_type;

ALTER TABLE rental_units DROP CONSTRAINT fk_rental_units_availability;

ALTER TABLE property_facilities DROP CONSTRAINT fk_property_facilities_facility;
ALTER TABLE unit_facilities DROP CONSTRAINT fk_unit_facilities_facility;

ALTER TABLE inquiries DROP CONSTRAINT fk_inquiries_status;

ALTER TABLE listing_reports DROP CONSTRAINT fk_listing_reports_reason;

ALTER TABLE listing_reports DROP CONSTRAINT fk_listing_reports_status;

-- 2. ALTER PRIMARY KEY COLUMNS TO BIGINT
ALTER TABLE provinces ALTER COLUMN id TYPE BIGINT;
ALTER TABLE property_types ALTER COLUMN id TYPE BIGINT;
ALTER TABLE listing_statuses ALTER COLUMN id TYPE BIGINT;
ALTER TABLE unit_types ALTER COLUMN id TYPE BIGINT;
ALTER TABLE availability_statuses ALTER COLUMN id TYPE BIGINT;
ALTER TABLE facilities ALTER COLUMN id TYPE BIGINT;
ALTER TABLE inquiry_statuses ALTER COLUMN id TYPE BIGINT;
ALTER TABLE report_reasons ALTER COLUMN id TYPE BIGINT;
ALTER TABLE report_statuses ALTER COLUMN id TYPE BIGINT;

-- 3. ALTER FOREIGN KEY COLUMNS TO BIGINT
ALTER TABLE cities ALTER COLUMN province_id TYPE BIGINT;
ALTER TABLE properties ALTER COLUMN province_id TYPE BIGINT;
ALTER TABLE properties ALTER COLUMN property_type_id TYPE BIGINT;
ALTER TABLE properties ALTER COLUMN listing_status_id TYPE BIGINT;
ALTER TABLE rental_units ALTER COLUMN unit_type_id TYPE BIGINT;
ALTER TABLE rental_units ALTER COLUMN availability_status_id TYPE BIGINT;
ALTER TABLE property_facilities ALTER COLUMN facility_id TYPE BIGINT;
ALTER TABLE unit_facilities ALTER COLUMN facility_id TYPE BIGINT;
ALTER TABLE inquiries ALTER COLUMN inquiry_status_id TYPE BIGINT;
ALTER TABLE listing_reports ALTER COLUMN reason_id TYPE BIGINT;
ALTER TABLE listing_reports ALTER COLUMN report_status_id TYPE BIGINT;

-- 4. RE-ADD FOREIGN KEY CONSTRAINTS
ALTER TABLE cities
    ADD CONSTRAINT fk_cities_province
        FOREIGN KEY (province_id) REFERENCES provinces(id) ON DELETE RESTRICT;

ALTER TABLE properties
    ADD CONSTRAINT fk_properties_city_province
        FOREIGN KEY (city_id, province_id) REFERENCES cities(id, province_id) ON DELETE RESTRICT;

ALTER TABLE properties
    ADD CONSTRAINT fk_properties_type
        FOREIGN KEY (property_type_id) REFERENCES property_types(id) ON DELETE RESTRICT;

ALTER TABLE properties
    ADD CONSTRAINT fk_properties_listing_status
        FOREIGN KEY (listing_status_id) REFERENCES listing_statuses(id) ON DELETE RESTRICT;

ALTER TABLE rental_units
    ADD CONSTRAINT fk_rental_units_type
        FOREIGN KEY (unit_type_id) REFERENCES unit_types(id) ON DELETE RESTRICT;

ALTER TABLE rental_units
    ADD CONSTRAINT fk_rental_units_availability
        FOREIGN KEY (availability_status_id) REFERENCES availability_statuses(id) ON DELETE RESTRICT;

ALTER TABLE property_facilities
    ADD CONSTRAINT fk_property_facilities_facility
        FOREIGN KEY (facility_id) REFERENCES facilities(id) ON DELETE RESTRICT;

ALTER TABLE unit_facilities
    ADD CONSTRAINT fk_unit_facilities_facility
        FOREIGN KEY (facility_id) REFERENCES facilities(id) ON DELETE RESTRICT;

ALTER TABLE inquiries
    ADD CONSTRAINT fk_inquiries_status
        FOREIGN KEY (inquiry_status_id) REFERENCES inquiry_statuses(id) ON DELETE RESTRICT;

ALTER TABLE listing_reports
    ADD CONSTRAINT fk_listing_reports_reason
        FOREIGN KEY (reason_id) REFERENCES report_reasons(id) ON DELETE RESTRICT;

ALTER TABLE listing_reports
    ADD CONSTRAINT fk_listing_reports_status
        FOREIGN KEY (report_status_id) REFERENCES report_statuses(id) ON DELETE RESTRICT;