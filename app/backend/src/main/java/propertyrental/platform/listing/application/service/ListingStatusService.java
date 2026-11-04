package propertyrental.platform.listing.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propertyrental.platform.listing.api.dto.request.CreateListingStatusRequest;
import propertyrental.platform.listing.api.dto.request.UpdateListingStatusRequest;
import propertyrental.platform.listing.api.dto.response.ListingStatusResponse;
import propertyrental.platform.listing.application.mapper.ListingStatusMapper;
import propertyrental.platform.listing.domain.ListingStatusAlreadyExistsException;
import propertyrental.platform.listing.domain.ListingStatusInUseException;
import propertyrental.platform.listing.domain.ListingStatusNotFoundException;
import propertyrental.platform.listing.infrastructure.persistence.entity.ListingStatusEntity;
import propertyrental.platform.listing.infrastructure.persistence.repository.ListingStatusRepository;

@Service
@RequiredArgsConstructor
public class ListingStatusService {

    private final ListingStatusRepository listingStatusRepository;
    private final ListingStatusMapper listingStatusMapper;

    @Transactional
    public List<ListingStatusResponse> getAllListingStatuses() {

        return listingStatusRepository.findAll().stream()
                .map(listingStatusMapper::toResponse)
                .toList();
    }

    @Transactional
    public ListingStatusResponse getListingStatusById(Long id) {

        return listingStatusMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public ListingStatusResponse createListingStatus(CreateListingStatusRequest request) {

        // Reject the request (409) if the code or name is already taken
        if (listingStatusRepository.existsByCode(request.code())) {
            throw new ListingStatusAlreadyExistsException("code", request.code());
        }
        if (listingStatusRepository.existsByName(request.name())) {
            throw new ListingStatusAlreadyExistsException("name", request.name());
        }

        ListingStatusEntity saved = listingStatusRepository.save(listingStatusMapper.toEntity(request));

        return listingStatusMapper.toResponse(saved);
    }

    @Transactional
    public ListingStatusResponse updateListingStatus(Long id, UpdateListingStatusRequest request) {

        // Load the status to update or fail with 404 if the id doesn't exist
        ListingStatusEntity status = findOrThrow(id);

        // Reject the update (409) if a different status already uses this code or name
        if (listingStatusRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new ListingStatusAlreadyExistsException("code", request.code());
        }
        if (listingStatusRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ListingStatusAlreadyExistsException("name", request.name());
        }

        // Apply the new values to the existing entity and save it
        listingStatusMapper.updateEntity(status, request);

        return listingStatusMapper.toResponse(listingStatusRepository.save(status));
    }

    @Transactional
    public void deleteListingStatus(Long id) {
        ListingStatusEntity status = findOrThrow(id);

        try {
            listingStatusRepository.delete(status);

            listingStatusRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ListingStatusInUseException(id);
        }
    }

    // Returns the entity or throws the 404 exception
    private ListingStatusEntity findOrThrow(Long id) {

        return listingStatusRepository.findById(id)
                .orElseThrow(() -> new ListingStatusNotFoundException(id));
    }
}