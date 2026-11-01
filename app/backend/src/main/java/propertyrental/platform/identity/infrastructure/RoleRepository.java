package propertyrental.platform.identity.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import propertyrental.platform.identity.domain.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Short> {
}

