package propertyrental.platform.media.api.dto.response;

import java.util.UUID;

public record PropertyImageResponse(
        UUID id,
        UUID propertyId,
        String imageUrl,
        String altText,
        Integer displayOrder
) {}