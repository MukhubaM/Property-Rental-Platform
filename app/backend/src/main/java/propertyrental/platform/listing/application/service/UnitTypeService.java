package propertyrental.platform.listing.application.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propertyrental.platform.listing.api.dto.request.CreateUnitTypeRequest;
import propertyrental.platform.listing.api.dto.request.UpdateUnitTypeRequest;
import propertyrental.platform.listing.api.dto.response.UnitTypeResponse;
import propertyrental.platform.listing.application.mapper.UnitTypeMapper;
import propertyrental.platform.listing.domain.UnitTypeAlreadyExistsException;
import propertyrental.platform.listing.domain.UnitTypeInUseException;
import propertyrental.platform.listing.domain.UnitTypeNotFoundException;
import propertyrental.platform.listing.infrastructure.persistence.entity.UnitTypeEntity;
import propertyrental.platform.listing.infrastructure.persistence.repository.UnitTypeRepository;

@Service
@RequiredArgsConstructor
public class UnitTypeService {

    private final UnitTypeRepository unitTypeRepository;
    private final UnitTypeMapper unitTypeMapper;

    @Transactional
    public List<UnitTypeResponse> getAllUnitTypes() {

        return unitTypeRepository.findAll().stream()
                .map(unitTypeMapper::toResponse)
                .toList();
    }

    @Transactional
    public UnitTypeResponse getUnitTypeById(Long id) {

        return unitTypeMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public UnitTypeResponse createUnitType(CreateUnitTypeRequest request) {

        // Reject the request (409) if the code or name is already taken
        if (unitTypeRepository.existsByCode(request.code())) {
            throw new UnitTypeAlreadyExistsException("code", request.code());
        }
        if (unitTypeRepository.existsByName(request.name())) {
            throw new UnitTypeAlreadyExistsException("name", request.name());
        }

        UnitTypeEntity saved = unitTypeRepository.save(unitTypeMapper.toEntity(request));

        return unitTypeMapper.toResponse(saved);
    }

    @Transactional
    public UnitTypeResponse updateUnitType(Long id, UpdateUnitTypeRequest request) {

        // Load the unit type to update or fail with 404 if the id doesn't exist
        UnitTypeEntity unitType = findOrThrow(id);

        // Reject the update (409) if a different unit type already uses this code or name
        if (unitTypeRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new UnitTypeAlreadyExistsException("code", request.code());
        }
        if (unitTypeRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new UnitTypeAlreadyExistsException("name", request.name());
        }

        // This applies the new values to the existing entity and save it
        unitTypeMapper.updateEntity(unitType, request);

        return unitTypeMapper.toResponse(unitTypeRepository.save(unitType));
    }

    @Transactional
    public void deleteUnitType(Long id) {
        UnitTypeEntity unitType = findOrThrow(id);

        try {
            unitTypeRepository.delete(unitType);
            unitTypeRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new UnitTypeInUseException(id);
        }
    }

    // Shared lookup (returns the entity or throws the 404 exception)
    private UnitTypeEntity findOrThrow(Long id) {

        return unitTypeRepository.findById(id)
                .orElseThrow(() -> new UnitTypeNotFoundException(id));
    }
}