package propertyrental.platform.moderation.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propertyrental.platform.moderation.infrastructure.persistence.entity.ReportReasonEntity;

@Repository
public interface ReportReasonRepository extends JpaRepository<ReportReasonEntity, Long> {

    // Used by create (if the code or name already taken)
    boolean existsByCode(String code);
    boolean existsByName(String name);

    // Used by update (if the code or name taken by a different report reason)
    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsByNameAndIdNot(String name, Long id);
}
