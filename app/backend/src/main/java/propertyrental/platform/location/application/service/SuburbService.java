package propertyrental.platform.location.application.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propertyrental.platform.location.api.dto.request.CreateSuburbRequest;
import propertyrental.platform.location.api.dto.request.UpdateSuburbRequest;
import propertyrental.platform.location.api.dto.response.SuburbResponse;
import propertyrental.platform.location.application.mapper.SuburbMapper;
import propertyrental.platform.location.domain.SuburbAlreadyExistsException;
import propertyrental.platform.location.domain.SuburbCityNotFoundException;
import propertyrental.platform.location.domain.SuburbInUseException;
import propertyrental.platform.location.domain.SuburbNotFoundException;
import propertyrental.platform.location.infrastructure.persistence.entity.SuburbEntity;
import propertyrental.platform.location.infrastructure.persistence.repository.CityRepository;
import propertyrental.platform.location.infrastructure.persistence.repository.SuburbRepository;

@Service
@RequiredArgsConstructor
public class SuburbService {

    private final SuburbRepository suburbRepository;
    private final CityRepository cityRepository;
    private final SuburbMapper suburbMapper;

    // This returns all suburbs or only the suburbs of one city when cityId is given
    @Transactional
    public List<SuburbResponse> getAllSuburbs(Long cityId) {
        List<SuburbEntity> suburbs = (cityId == null)
                ? suburbRepository.findAll()
                : suburbRepository.findByCityId(cityId);

        return suburbs.stream()
                .map(suburbMapper::toResponse)
                .toList();
    }

    @Transactional
    public SuburbResponse getSuburbById(Long id) {

        return suburbMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public SuburbResponse createSuburb(CreateSuburbRequest request) {

        // This rejects the request (400) if the city does not exist
        if (!cityRepository.existsById(request.cityId())) {
            throw new SuburbCityNotFoundException(request.cityId());
        }
        // This one reject the request (409) if the name is already used in this city
        if (suburbRepository.existsByCityIdAndNameIgnoreCase(request.cityId(), request.name())) {
            throw new SuburbAlreadyExistsException(request.name(), request.cityId());
        }

        SuburbEntity saved = suburbRepository.save(suburbMapper.toEntity(request));

        return suburbMapper.toResponse(saved);
    }

    @Transactional
    public SuburbResponse updateSuburb(Long id, UpdateSuburbRequest request) {

        // This loads the suburb to update or fail with 404 if the id doesn't exist
        SuburbEntity suburb = findOrThrow(id);

        // This one reject the update (400) if the city does not exist
        if (!cityRepository.existsById(request.cityId())) {
            throw new SuburbCityNotFoundException(request.cityId());
        }
        // This reject the update (409) if a different suburb already uses this name in the city
        if (suburbRepository.existsByCityIdAndNameIgnoreCaseAndIdNot(request.cityId(), request.name(), id)) {
            throw new SuburbAlreadyExistsException(request.name(), request.cityId());
        }

        // This applies the new values to the existing entity and save it
        suburbMapper.updateEntity(suburb, request);

        return suburbMapper.toResponse(suburbRepository.save(suburb));
    }

    @Transactional
    public void deleteSuburb(Long id) {
        SuburbEntity suburb = findOrThrow(id);

        try {
            suburbRepository.delete(suburb);
            suburbRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new SuburbInUseException(id);
        }
    }

    // Shared lookup (this returns the entity or throws the 404 exception)
    private SuburbEntity findOrThrow(Long id) {

        return suburbRepository.findById(id)
                .orElseThrow(() -> new SuburbNotFoundException(id));
    }
}