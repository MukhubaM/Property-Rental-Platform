package propertyrental.platform.listing.application.mapper;

import org.springframework.stereotype.Component;

import propertyrental.platform.listing.api.dto.response.PropertyTypeResponse;
import propertyrental.platform.listing.infrastructure.persistence.entity.PropertyTypeEntity;

@Component
public class PropertyTypeMapper {

    // This convert an entity to the response DTO so that the entity is never exposed
    public PropertyTypeResponse toResponse(PropertyTypeEntity propertyType) {

        return new PropertyTypeResponse(
                propertyType.getId(),
                propertyType.getCode(),
                propertyType.getName()
        );
    }
}