package propertyrental.platform.listing.application.mapper;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propertyrental.platform.listing.api.dto.response.AvailabilityStatusResponse;
import propertyrental.platform.listing.application.mapper.AvailabilityStatusMapper;
import propertyrental.platform.listing.infrastructure.persistence.repository.AvailabilityStatusRepository;

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
}
