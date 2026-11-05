package propertyrental.platform.location.application.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propertyrental.platform.location.api.dto.request.CreateProvinceRequest;
import propertyrental.platform.location.api.dto.request.UpdateProvinceRequest;
import propertyrental.platform.location.api.dto.response.ProvinceResponse;
import propertyrental.platform.location.application.mapper.ProvinceMapper;
import propertyrental.platform.location.domain.ProvinceAlreadyExistsException;
import propertyrental.platform.location.domain.ProvinceInUseException;
import propertyrental.platform.location.domain.ProvinceNotFoundException;
import propertyrental.platform.location.infrastructure.persistence.entity.ProvinceEntity;
import propertyrental.platform.location.infrastructure.persistence.repository.ProvinceRepository;

@Service
@RequiredArgsConstructor
public class ProvinceService {

    private final ProvinceRepository provinceRepository;
    private final ProvinceMapper provinceMapper;

    @Transactional
    public List<ProvinceResponse> getAllProvinces() {

        return provinceRepository.findAll().stream()
                .map(provinceMapper::toResponse)
                .toList();
    }

    @Transactional
    public ProvinceResponse getProvinceById(Long id) {

        return provinceMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public ProvinceResponse createProvince(CreateProvinceRequest request) {

        // This rejects the request (409) if the code or name is already taken
        if (provinceRepository.existsByCode(request.code())) {
            throw new ProvinceAlreadyExistsException("code", request.code());
        }
        if (provinceRepository.existsByName(request.name())) {
            throw new ProvinceAlreadyExistsException("name", request.name());
        }

        ProvinceEntity saved = provinceRepository.save(provinceMapper.toEntity(request));

        return provinceMapper.toResponse(saved);
    }

    @Transactional
    public ProvinceResponse updateProvince(Long id, UpdateProvinceRequest request) {

        // This one load the province to update or fail with 404 if the id doesn't exist
        ProvinceEntity province = findOrThrow(id);

        // This one rejects the update (409) if a different province already uses this code or name
        if (provinceRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new ProvinceAlreadyExistsException("code", request.code());
        }
        if (provinceRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ProvinceAlreadyExistsException("name", request.name());
        }

        // This applies the new values to the existing entity and save it
        provinceMapper.updateEntity(province, request);

        return provinceMapper.toResponse(provinceRepository.save(province));
    }

    @Transactional
    public void deleteProvince(Long id) {
        ProvinceEntity province = findOrThrow(id);

        try {
            provinceRepository.delete(province);

            provinceRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ProvinceInUseException(id);
        }
    }

    // Shared lookup (returns the entity or throws the 404 exception)
    private ProvinceEntity findOrThrow(Long id) {

        return provinceRepository.findById(id)
                .orElseThrow(() -> new ProvinceNotFoundException(id));
    }
}