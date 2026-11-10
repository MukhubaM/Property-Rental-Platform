package propertyrental.platform.listing.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propertyrental.platform.listing.infrastructure.persistence.entity.UnitTypeEntity;

@Repository
public interface UnitTypeRepository extends JpaRepository<UnitTypeEntity, Long> {

    // Used by create (checks if the code or name is already taken)
    boolean existsByCode(String code);
    boolean existsByName(String name);

    // Used by update (checks if the code or name taken by a different unit type)
    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsByNameAndIdNot(String name, Long id);
}