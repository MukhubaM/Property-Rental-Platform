package propertyrental.platform.moderation.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propertyrental.platform.moderation.infrastructure.persistence.entity.ReportStatusEntity;

@Repository
public interface ReportStatusRepository extends JpaRepository<ReportStatusEntity, Long> {}
