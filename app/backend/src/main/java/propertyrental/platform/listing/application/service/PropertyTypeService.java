package propertyrental.platform.listing.application.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propertyrental.platform.listing.api.dto.response.PropertyTypeResponse;
import propertyrental.platform.listing.application.mapper.PropertyTypeMapper;
import propertyrental.platform.listing.infrastructure.persistence.repository.PropertyTypeRepository;
import propertyrental.platform.listing.api.dto.request.CreatePropertyTypeRequest;
import propertyrental.platform.listing.domain.PropertyTypeAlreadyExistsException;
import propertyrental.platform.listing.infrastructure.persistence.entity.PropertyTypeEntity;
import propertyrental.platform.listing.domain.PropertyTypeNotFoundException;
import propertyrental.platform.listing.api.dto.request.UpdatePropertyTypeRequest;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class PropertyTypeService {

    private final PropertyTypeRepository propertyTypeRepository;
    private final PropertyTypeMapper propertyTypeMapper;

    // This return all property types (an empty list if there are none)
    @Transactional
    public List<PropertyTypeResponse> getAllPropertyTypes() {

        return propertyTypeRepository.findAll().stream()
                .map(propertyTypeMapper::toResponse)
                .toList();
    }

    @Transactional
    public PropertyTypeResponse createPropertyType(CreatePropertyTypeRequest request) {

        // This reject the request (409) if the code or name is already taken
        if (propertyTypeRepository.existsByCode(request.code())) {
            throw new PropertyTypeAlreadyExistsException("code", request.code());
        }
        if (propertyTypeRepository.existsByName(request.name())) {
            throw new PropertyTypeAlreadyExistsException("name", request.name());
        }

        PropertyTypeEntity saved = propertyTypeRepository.save(propertyTypeMapper.toEntity(request));

        return propertyTypeMapper.toResponse(saved);
    }

    // This return the property type or fail with 404 if the id doesn't exist
    @Transactional
    public PropertyTypeResponse getPropertyTypeById(Long id) {

        return propertyTypeRepository.findById(id)
                .map(propertyTypeMapper::toResponse)
                .orElseThrow(() -> new PropertyTypeNotFoundException(id));
    }

    @Transactional
    public PropertyTypeResponse updatePropertyType(Long id, UpdatePropertyTypeRequest request) {

        // Load the property type to update or fail with 404 if the id doesn't exist
        PropertyTypeEntity propertyType = propertyTypeRepository.findById(id)
                .orElseThrow(() -> new PropertyTypeNotFoundException(id));

        // Reject the update (409) if a different property type already uses this code or name
        if (propertyTypeRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new PropertyTypeAlreadyExistsException("code", request.code());
        }
        if (propertyTypeRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new PropertyTypeAlreadyExistsException("name", request.name());
        }

        // Apply the new values to the existing entity and save it
        propertyTypeMapper.updateEntity(propertyType, request);

        return propertyTypeMapper.toResponse(propertyTypeRepository.save(propertyType));
    }
}