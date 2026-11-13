package propertyrental.platform.listing.application.mapper;

import org.springframework.stereotype.Component;

import propertyrental.platform.listing.api.dto.response.AvailabilityStatusResponse;
import propertyrental.platform.listing.infrastructure.persistence.entity.AvailabilityStatusEntity;

@Component
public class AvailabilityStatusMapper {

    // Thus convert an entity to the response DTO so that the entity is never exposed
    public AvailabilityStatusResponse toResponse(AvailabilityStatusEntity availabilityStatus) {

        return new AvailabilityStatusResponse(
                availabilityStatus.getId(),
                availabilityStatus.getCode(),
                availabilityStatus.getName()
        );
    }
}
