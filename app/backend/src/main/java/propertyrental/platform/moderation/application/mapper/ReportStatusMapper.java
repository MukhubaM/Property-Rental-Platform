package propertyrental.platform.moderation.application.mapper;

import org.springframework.stereotype.Component;

import propertyrental.platform.moderation.api.dto.response.ReportStatusResponse;
import propertyrental.platform.moderation.infrastructure.persistence.entity.ReportStatusEntity;

@Component
public class ReportStatusMapper {

    // This convert an entity to the response DTO so that the entity is never exposed
    public ReportStatusResponse toResponse(ReportStatusEntity reportStatus) {

        return new ReportStatusResponse(
                reportStatus.getId(),
                reportStatus.getCode(),
                reportStatus.getName()
        );
    }
}