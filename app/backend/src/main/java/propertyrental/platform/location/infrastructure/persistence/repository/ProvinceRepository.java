package propertyrental.platform.location.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propertyrental.platform.location.infrastructure.persistence.entity.ProvinceEntity;

@Repository
public interface ProvinceRepository extends JpaRepository<ProvinceEntity, Long> {

    // Used by create (checks if the code or name already taken)
    boolean existsByCode(String code);

    boolean existsByName(String name);

    // Used by update (this one checks if the code or name is taken by a different province)
    boolean existsByCodeAndIdNot(String code, Long id);

    boolean existsByNameAndIdNot(String name, Long id);
}