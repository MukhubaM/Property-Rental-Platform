package propertyrental.platform.listing.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propertyrental.platform.listing.infrastructure.persistence.entity.PropertyTypeEntity;

@Repository
public interface PropertyTypeRepository extends JpaRepository<PropertyTypeEntity, Long> {
}