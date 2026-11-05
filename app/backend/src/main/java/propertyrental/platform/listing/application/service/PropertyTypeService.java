package propertyrental.platform.listing.application.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propertyrental.platform.listing.api.dto.response.PropertyTypeResponse;
import propertyrental.platform.listing.application.mapper.PropertyTypeMapper;
import propertyrental.platform.listing.infrastructure.persistence.repository.PropertyTypeRepository;
import propertyrental.platform.listing.domain.PropertyTypeNotFoundException;

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

    // This return the property type or fail with 404 if the id doesn't exist
    @Transactional
    public PropertyTypeResponse getPropertyTypeById(Long id) {

        return propertyTypeRepository.findById(id)
                .map(propertyTypeMapper::toResponse)
                .orElseThrow(() -> new PropertyTypeNotFoundException(id));
    }
}