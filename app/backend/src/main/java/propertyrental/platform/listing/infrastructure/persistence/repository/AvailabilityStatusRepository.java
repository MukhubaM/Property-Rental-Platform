package propertyrental.platform.listing.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propertyrental.platform.listing.infrastructure.persistence.entity.AvailabilityStatusEntity;

@Repository
public interface AvailabilityStatusRepository extends JpaRepository<AvailabilityStatusEntity, Long> {

    // Used by create (checks if the code or name already taken)
    boolean existsByCode(String code);
    boolean existsByName(String name);

    // Used by update (this one checks if the code or name taken by a different availability status)
    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsByNameAndIdNot(String name, Long id);
}