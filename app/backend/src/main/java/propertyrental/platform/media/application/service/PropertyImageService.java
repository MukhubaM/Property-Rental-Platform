package propertyrental.platform.media.application.service;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propertyrental.platform.listing.infrastructure.persistence.repository.PropertyRepository;
import propertyrental.platform.media.api.dto.request.CreatePropertyImageRequest;
import propertyrental.platform.media.api.dto.request.UpdatePropertyImageRequest;
import propertyrental.platform.media.api.dto.response.PropertyImageResponse;
import propertyrental.platform.media.application.mapper.PropertyImageMapper;
import propertyrental.platform.media.domain.PropertyImageNotFoundException;
import propertyrental.platform.media.domain.PropertyImageOrderTakenException;
import propertyrental.platform.media.domain.PropertyImagePropertyNotFoundException;
import propertyrental.platform.media.infrastructure.persistence.entity.PropertyImageEntity;
import propertyrental.platform.media.infrastructure.persistence.repository.PropertyImageRepository;

@Service
@RequiredArgsConstructor
public class PropertyImageService {

    private final PropertyImageRepository propertyImageRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyImageMapper propertyImageMapper;

    // A property's images in display order (an empty list if there are none)
    @Transactional
    public List<PropertyImageResponse> getImages(UUID propertyId) {
        requireProperty(propertyId);

        return propertyImageRepository.findByPropertyIdOrderByDisplayOrderAsc(propertyId).stream()
                .map(propertyImageMapper::toResponse)
                .toList();
    }

    @Transactional
    public PropertyImageResponse addImage(UUID propertyId, CreatePropertyImageRequest request) {

        // Fail with 404 if the property doesn't exist
        requireProperty(propertyId);

        // This rejects the request (409) if another image of the property already uses this display order
        if (propertyImageRepository.existsByPropertyIdAndDisplayOrder(propertyId, request.displayOrder())) {
            throw new PropertyImageOrderTakenException(request.displayOrder());
        }

        PropertyImageEntity saved = propertyImageRepository.save(propertyImageMapper.toEntity(propertyId, request));

        return propertyImageMapper.toResponse(saved);
    }

    @Transactional
    public PropertyImageResponse updateImage(UUID propertyId, UUID imageId, UpdatePropertyImageRequest request) {
        requireProperty(propertyId);

        // The image must exist and belong to this property, otherwise 404
        PropertyImageEntity image = findImageOrThrow(propertyId, imageId);

        // This reject the update (409) if a different image of the property already uses this display order
        if (propertyImageRepository.existsByPropertyIdAndDisplayOrderAndIdNot(
                propertyId, request.displayOrder(), imageId)) {
            throw new PropertyImageOrderTakenException(request.displayOrder());
        }

        propertyImageMapper.updateEntity(image, request);

        return propertyImageMapper.toResponse(propertyImageRepository.save(image));
    }

    @Transactional
    public void deleteImage(UUID propertyId, UUID imageId) {
        requireProperty(propertyId);
        propertyImageRepository.delete(findImageOrThrow(propertyId, imageId));
    }

    // Shared check (fails with the 404 exception when the property does not exist)
    private void requireProperty(UUID propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            throw new PropertyImagePropertyNotFoundException(propertyId);
        }
    }

    // Shared lookup (the image must belong to the property in the URL)
    private PropertyImageEntity findImageOrThrow(UUID propertyId, UUID imageId) {

        return propertyImageRepository.findByIdAndPropertyId(imageId, propertyId)
                .orElseThrow(() -> new PropertyImageNotFoundException(imageId));
    }
}