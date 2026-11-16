package propertyrental.platform.listing.application.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propertyrental.platform.listing.api.dto.response.AvailabilityStatusResponse;
import propertyrental.platform.listing.application.mapper.AvailabilityStatusMapper;
import propertyrental.platform.listing.infrastructure.persistence.entity.AvailabilityStatusEntity;
import propertyrental.platform.listing.infrastructure.persistence.repository.AvailabilityStatusRepository;
import propertyrental.platform.listing.domain.AvailabilityStatusNotFoundException;
import propertyrental.platform.listing.api.dto.request.UpdateAvailabilityStatusRequest;
import propertyrental.platform.listing.domain.AvailabilityStatusAlreadyExistsException;
import propertyrental.platform.listing.api.dto.request.CreateAvailabilityStatusRequest;

@Service
@RequiredArgsConstructor
public class AvailabilityStatusService {

    private final AvailabilityStatusRepository availabilityStatusRepository;
    private final AvailabilityStatusMapper availabilityStatusMapper;

    // This return all availability statuses (an empty list if there are none)
    @Transactional
    public List<AvailabilityStatusResponse> getAllAvailabilityStatuses() {

        return availabilityStatusRepository.findAll().stream()
                .map(availabilityStatusMapper::toResponse)
                .toList();
    }

    // This returns the availability status or fail with 404 if the id doesn't exist
    @Transactional
    public AvailabilityStatusResponse getAvailabilityStatusById(Long id) {

        return availabilityStatusRepository.findById(id)
                .map(availabilityStatusMapper::toResponse)
                .orElseThrow(() -> new AvailabilityStatusNotFoundException(id));
    }

    @Transactional
    public AvailabilityStatusResponse updateAvailabilityStatus(Long id, UpdateAvailabilityStatusRequest request) {

        // Load the availability status to update or fail with 404 if the id doesn't exist
        AvailabilityStatusEntity availabilityStatus = availabilityStatusRepository.findById(id)
                .orElseThrow(() -> new AvailabilityStatusNotFoundException(id));

        // Reject the update (409) if a different availability status already uses this code or name
        if (availabilityStatusRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new AvailabilityStatusAlreadyExistsException("code", request.code());
        }
        if (availabilityStatusRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new AvailabilityStatusAlreadyExistsException("name", request.name());
        }

        // Applies new values to the existing entity and save it
        availabilityStatusMapper.updateEntity(availabilityStatus, request);

        return availabilityStatusMapper.toResponse(availabilityStatusRepository.save(availabilityStatus));
    }

    @Transactional
    public AvailabilityStatusResponse createAvailabilityStatus(CreateAvailabilityStatusRequest request) {

        // This method reject the request (409) if the code or name is already taken
        if (availabilityStatusRepository.existsByCode(request.code())) {
            throw new AvailabilityStatusAlreadyExistsException("code", request.code());
        }
        if (availabilityStatusRepository.existsByName(request.name())) {
            throw new AvailabilityStatusAlreadyExistsException("name", request.name());
        }

        AvailabilityStatusEntity saved = availabilityStatusRepository.save(availabilityStatusMapper.toEntity(request));

        return availabilityStatusMapper.toResponse(saved);
    }
}
