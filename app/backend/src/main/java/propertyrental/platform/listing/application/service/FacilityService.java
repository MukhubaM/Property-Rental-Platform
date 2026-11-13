package propertyrental.platform.listing.application.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propertyrental.platform.listing.api.dto.request.CreateFacilityRequest;
import propertyrental.platform.listing.api.dto.request.UpdateFacilityRequest;
import propertyrental.platform.listing.api.dto.response.FacilityResponse;
import propertyrental.platform.listing.application.mapper.FacilityMapper;
import propertyrental.platform.listing.domain.FacilityAlreadyExistsException;
import propertyrental.platform.listing.domain.FacilityInUseException;
import propertyrental.platform.listing.domain.FacilityNotFoundException;
import propertyrental.platform.listing.infrastructure.persistence.entity.FacilityEntity;
import propertyrental.platform.listing.infrastructure.persistence.repository.FacilityRepository;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;

    @Transactional
    public List<FacilityResponse> getAllFacilities() {

        return facilityRepository.findAll().stream()
                .map(facilityMapper::toResponse)
                .toList();
    }

    @Transactional
    public FacilityResponse getFacilityById(Long id) {

        return facilityMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public FacilityResponse createFacility(CreateFacilityRequest request) {

        // Reject the request (409) if the code or name is already taken
        if (facilityRepository.existsByCode(request.code())) {
            throw new FacilityAlreadyExistsException("code", request.code());
        }
        if (facilityRepository.existsByName(request.name())) {
            throw new FacilityAlreadyExistsException("name", request.name());
        }

        FacilityEntity saved = facilityRepository.save(facilityMapper.toEntity(request));

        return facilityMapper.toResponse(saved);
    }

    @Transactional
    public FacilityResponse updateFacility(Long id, UpdateFacilityRequest request) {

        // This load the facility to update or fail with 404 if the id doesn't exist
        FacilityEntity facility = findOrThrow(id);

        // This reject the update (409) if a different facility already uses this code or name
        if (facilityRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new FacilityAlreadyExistsException("code", request.code());
        }
        if (facilityRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new FacilityAlreadyExistsException("name", request.name());
        }

        // The applies the new values to the existing entity and save it
        facilityMapper.updateEntity(facility, request);

        return facilityMapper.toResponse(facilityRepository.save(facility));
    }

    @Transactional
    public void deleteFacility(Long id) {
        FacilityEntity facility = findOrThrow(id);

        try {
            facilityRepository.delete(facility);
            facilityRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new FacilityInUseException(id);
        }
    }

    // Shared lookup (This returns the entity or throws the 404 exception)
    private FacilityEntity findOrThrow(Long id) {

        return facilityRepository.findById(id)
                .orElseThrow(() -> new FacilityNotFoundException(id));
    }
}