package propertyrental.platform.media.application.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import propertyrental.platform.media.api.dto.request.CreatePropertyImageRequest;
import propertyrental.platform.media.api.dto.request.UpdatePropertyImageRequest;
import propertyrental.platform.media.api.dto.response.PropertyImageResponse;
import propertyrental.platform.media.infrastructure.persistence.entity.PropertyImageEntity;

@Component
public class PropertyImageMapper {

    // Build a new entity for a property from a create request (the id is generated)
    public PropertyImageEntity toEntity(UUID propertyId, CreatePropertyImageRequest request) {
        PropertyImageEntity image = new PropertyImageEntity();
        image.setPropertyId(propertyId);
        image.setImageUrl(request.imageUrl());
        image.setAltText(request.altText());
        image.setDisplayOrder(request.displayOrder());

        return image;
    }

    // Apply an update request to an existing entity in place
    public void updateEntity(PropertyImageEntity image, UpdatePropertyImageRequest request) {
        image.setImageUrl(request.imageUrl());
        image.setAltText(request.altText());
        image.setDisplayOrder(request.displayOrder());
    }

    // Convert an entity to the response DTO, so the entity is never exposed
    public PropertyImageResponse toResponse(PropertyImageEntity image) {

        return new PropertyImageResponse(
                image.getId(),
                image.getPropertyId(),
                image.getImageUrl(),
                image.getAltText(),
                image.getDisplayOrder()
        );
    }
}
