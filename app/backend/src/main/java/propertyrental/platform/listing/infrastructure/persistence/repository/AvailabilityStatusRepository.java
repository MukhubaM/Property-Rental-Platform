package propertyrental.platform.listing.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propertyrental.platform.listing.infrastructure.persistence.entity.AvailabilityStatusEntity;

@Repository
public interface AvailabilityStatusRepository extends JpaRepository<AvailabilityStatusEntity, Long> {

    // Used by create (Checks if the codde or name is already taken)
    boolean existsByCode(String code);
    boolean existsByName(String name);
}