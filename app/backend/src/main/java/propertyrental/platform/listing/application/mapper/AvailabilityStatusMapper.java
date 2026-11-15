package propertyrental.platform.listing.application.mapper;

import org.springframework.stereotype.Component;

import propertyrental.platform.listing.api.dto.response.AvailabilityStatusResponse;
import propertyrental.platform.listing.infrastructure.persistence.entity.AvailabilityStatusEntity;
import propertyrental.platform.listing.api.dto.request.UpdateAvailabilityStatusRequest;

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

    // This applies an update request to an existing entity in place
    public void updateEntity(AvailabilityStatusEntity availabilityStatus, UpdateAvailabilityStatusRequest request) {
        availabilityStatus.setCode(request.code());
        availabilityStatus.setName(request.name());
    }
}
