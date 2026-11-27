package propertyrental.platform.media.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propertyrental.platform.media.infrastructure.persistence.entity.PropertyImageEntity;

@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImageEntity, UUID> {

    // This is used by the list endpoint (a property's images in display order)
    List<PropertyImageEntity> findByPropertyIdOrderByDisplayOrderAsc(UUID propertyId);

    //This is used by update and delete (the image only counts if it belongs to this property)
    Optional<PropertyImageEntity> findByIdAndPropertyId(UUID id, UUID propertyId);

    // This is used by add (checks if the display order is already used by the property)
    boolean existsByPropertyIdAndDisplayOrder(UUID propertyId, Integer displayOrder);

    // This is used by update (checks if the display order is used by a different image of the property)
    boolean existsByPropertyIdAndDisplayOrderAndIdNot(UUID propertyId, Integer displayOrder, UUID id);
}