package propertyrental.platform.identity.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propertyrental.platform.identity.infrastructure.persistence.entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,Long> {

    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsByNameAndIdNot(String name, Long id);

    // These methods are used by create to check if the code or name already taken
    boolean existsByCode(String code);
    boolean existsByName(String name);
}
