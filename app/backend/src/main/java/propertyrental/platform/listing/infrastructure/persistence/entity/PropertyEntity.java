package propertyrental.platform.listing.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
public class PropertyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Plain id columns instead of entity links (the service checks that each reference exists)
    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "property_type_id", nullable = false)
    private Long propertyTypeId;

    @Column(name = "listing_status_id", nullable = false)
    private Long listingStatusId;

    @Column(name = "province_id", nullable = false)
    private Long provinceId;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Column(name = "suburb_id")
    private Long suburbId;

    // This sets the lengths to match the migration
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Column(name = "street_address", nullable = false, length = 255)
    private String streetAddress;

    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @Column(name = "latitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}