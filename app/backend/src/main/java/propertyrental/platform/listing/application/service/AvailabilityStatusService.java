package propertyrental.platform.listing.application.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import propertyrental.platform.listing.api.dto.response.AvailabilityStatusResponse;
import propertyrental.platform.listing.application.mapper.AvailabilityStatusMapper;
import propertyrental.platform.listing.infrastructure.persistence.repository.AvailabilityStatusRepository;
import propertyrental.platform.listing.domain.AvailabilityStatusNotFoundException;
import propertyrental.platform.listing.api.dto.request.CreateAvailabilityStatusRequest;
import propertyrental.platform.listing.domain.AvailabilityStatusAlreadyExistsException;
import propertyrental.platform.listing.infrastructure.persistence.entity.AvailabilityStatusEntity;
import propertyrental.platform.listing.domain.AvailabilityStatusInUseException;

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

    @Transactional
    public void deleteAvailabilityStatus(Long id) {

        // This loads the availability status to delete or fail with 404 if the id doesn't exist
        AvailabilityStatusEntity availabilityStatus = availabilityStatusRepository.findById(id)
                .orElseThrow(() -> new AvailabilityStatusNotFoundException(id));

        try {
            availabilityStatusRepository.delete(availabilityStatus);
            availabilityStatusRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new AvailabilityStatusInUseException(id);
        }
    }
}
