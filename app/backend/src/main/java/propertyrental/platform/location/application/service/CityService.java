package propertyrental.platform.location.application.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propertyrental.platform.location.api.dto.request.CreateCityRequest;
import propertyrental.platform.location.api.dto.request.UpdateCityRequest;
import propertyrental.platform.location.api.dto.response.CityResponse;
import propertyrental.platform.location.application.mapper.CityMapper;
import propertyrental.platform.location.domain.CityAlreadyExistsException;
import propertyrental.platform.location.domain.CityInUseException;
import propertyrental.platform.location.domain.CityNotFoundException;
import propertyrental.platform.location.domain.CityProvinceNotFoundException;
import propertyrental.platform.location.infrastructure.persistence.entity.CityEntity;
import propertyrental.platform.location.infrastructure.persistence.repository.CityRepository;
import propertyrental.platform.location.infrastructure.persistence.repository.ProvinceRepository;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final ProvinceRepository provinceRepository;
    private final CityMapper cityMapper;

    // This return all cities or only the cities of one province when provinceId is given
    @Transactional
    public List<CityResponse> getAllCities(Long provinceId) {
        List<CityEntity> cities = (provinceId == null)
                ? cityRepository.findAll()
                : cityRepository.findByProvinceId(provinceId);

        return cities.stream()
                .map(cityMapper::toResponse)
                .toList();
    }

    @Transactional
    public CityResponse getCityById(Long id) {

        return cityMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public CityResponse createCity(CreateCityRequest request) {

        // This rejects the request (400) if the province does not exist
        if (!provinceRepository.existsById(request.provinceId())) {
            throw new CityProvinceNotFoundException(request.provinceId());
        }
        // This one rejects the request (409) if the name is already used in this province
        if (cityRepository.existsByProvinceIdAndNameIgnoreCase(request.provinceId(), request.name())) {
            throw new CityAlreadyExistsException(request.name(), request.provinceId());
        }

        CityEntity saved = cityRepository.save(cityMapper.toEntity(request));

        return cityMapper.toResponse(saved);
    }

    @Transactional
    public CityResponse updateCity(Long id, UpdateCityRequest request) {

        // This loads the city to update or fail with 404 if the id doesn't exist
        CityEntity city = findOrThrow(id);

        // This reject the update (400) if the province does not exist
        if (!provinceRepository.existsById(request.provinceId())) {
            throw new CityProvinceNotFoundException(request.provinceId());
        }
        // This reject the update (409) if a different city already uses this name in the province
        if (cityRepository.existsByProvinceIdAndNameIgnoreCaseAndIdNot(request.provinceId(), request.name(), id)) {
            throw new CityAlreadyExistsException(request.name(), request.provinceId());
        }

        // This one apply the new values to the existing entity and save it
        cityMapper.updateEntity(city, request);

        return cityMapper.toResponse(cityRepository.save(city));
    }

    @Transactional
    public void deleteCity(Long id) {
        CityEntity city = findOrThrow(id);

        try {
            cityRepository.delete(city);
            cityRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new CityInUseException(id);
        }
    }

    // Shared lookup (returns the entity or throws the 404 exception)
    private CityEntity findOrThrow(Long id) {

        return cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException(id));
    }
}
