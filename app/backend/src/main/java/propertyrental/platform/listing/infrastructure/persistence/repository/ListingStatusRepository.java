package propertyrental.platform.listing.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propertyrental.platform.listing.infrastructure.persistence.entity.ListingStatusEntity;

@Repository
public interface ListingStatusRepository extends JpaRepository<ListingStatusEntity, Long> {

    // Used by create
    boolean existsByCode(String code);
    boolean existsByName(String name);

    // Used by update
    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsByNameAndIdNot(String name, Long id);
}
