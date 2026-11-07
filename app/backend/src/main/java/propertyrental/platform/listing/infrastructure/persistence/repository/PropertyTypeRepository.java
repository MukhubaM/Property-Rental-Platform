package propertyrental.platform.listing.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propertyrental.platform.listing.infrastructure.persistence.entity.PropertyTypeEntity;

@Repository
public interface PropertyTypeRepository extends JpaRepository<PropertyTypeEntity, Long> {

    // Used by create (checks if the code or name already taken)
    boolean existsByCode(String code);
    boolean existsByName(String name);

    // Used by update (checks if that code or name is taken by a different property type)
    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsByNameAndIdNot(String name, Long id);
}