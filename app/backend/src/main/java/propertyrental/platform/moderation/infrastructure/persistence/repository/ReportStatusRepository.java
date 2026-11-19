package propertyrental.platform.moderation.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propertyrental.platform.moderation.infrastructure.persistence.entity.ReportStatusEntity;

@Repository
public interface ReportStatusRepository extends JpaRepository<ReportStatusEntity, Long> {

    // This is used by create (checks if the code or name already taken)
    boolean existsByCode(String code);
    boolean existsByName(String name);

    // This is used by update (it checks if the code or name is taken by a different report status)
    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsByNameAndIdNot(String name, Long id);
}
