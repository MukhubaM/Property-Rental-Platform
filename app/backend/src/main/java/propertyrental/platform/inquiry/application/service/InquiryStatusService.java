package propertyrental.platform.inquiry.application.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import propertyrental.platform.inquiry.api.dto.response.InquiryStatusResponse;
import propertyrental.platform.inquiry.application.mapper.InquiryStatusMapper;
import propertyrental.platform.inquiry.domain.InquiryStatusNotFoundException;
import propertyrental.platform.inquiry.infrastructure.persistence.repository.InquiryStatusRepository;
import propertyrental.platform.inquiry.api.dto.request.CreateInquiryStatusRequest;
import propertyrental.platform.inquiry.domain.InquiryStatusAlreadyExistsException;
import propertyrental.platform.inquiry.infrastructure.persistence.entity.InquiryStatusEntity;
import propertyrental.platform.inquiry.api.dto.request.UpdateInquiryStatusRequest;
import propertyrental.platform.inquiry.domain.InquiryStatusInUseException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryStatusService {

    private final InquiryStatusRepository inquiryStatusRepository;
    private final InquiryStatusMapper inquiryStatusMapper;

    // Returns the inquiry status or fail with 404 if the id doesn't exist
    @Transactional
    public InquiryStatusResponse getInquiryStatusById(Long id) {

        return inquiryStatusRepository.findById(id)
                .map(inquiryStatusMapper::toResponse)
                .orElseThrow(() -> new InquiryStatusNotFoundException(id));
    }

    // Returns all inquiry statuses (an empty list if there are none)
    @Transactional
    public List<InquiryStatusResponse> getAllInquiryStatuses() {

        return inquiryStatusRepository.findAll().stream()
                .map(inquiryStatusMapper::toResponse)
                .toList();
    }

    @Transactional
    public InquiryStatusResponse createInquiryStatus(CreateInquiryStatusRequest request) {

        // This reject the request (409) if the code or name is already taken
        if (inquiryStatusRepository.existsByCode(request.code())) {
            throw new InquiryStatusAlreadyExistsException("code", request.code());
        }
        if (inquiryStatusRepository.existsByName(request.name())) {
            throw new InquiryStatusAlreadyExistsException("name", request.name());
        }

        InquiryStatusEntity saved = inquiryStatusRepository.save(inquiryStatusMapper.toEntity(request));

        return inquiryStatusMapper.toResponse(saved);
    }

    @Transactional
    public InquiryStatusResponse updateInquiryStatus(Long id, UpdateInquiryStatusRequest request) {

        // This loads the inquiry status to update or fail with 404 if the id doesn't exist
        InquiryStatusEntity inquiryStatus = inquiryStatusRepository.findById(id)
                .orElseThrow(() -> new InquiryStatusNotFoundException(id));

        // This method rejects the update (409) if a different inquiry status already uses this code or name
        if (inquiryStatusRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new InquiryStatusAlreadyExistsException("code", request.code());
        }
        if (inquiryStatusRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new InquiryStatusAlreadyExistsException("name", request.name());
        }

        // This method applies the new values to the existing entity and save it
        inquiryStatusMapper.updateEntity(inquiryStatus, request);

        return inquiryStatusMapper.toResponse(inquiryStatusRepository.save(inquiryStatus));
    }

    @Transactional
    public void deleteInquiryStatus(Long id) {

        // This method load the inquiry status to delete or fail with 404 if the id doesn't exist
        InquiryStatusEntity inquiryStatus = inquiryStatusRepository.findById(id)
                .orElseThrow(() -> new InquiryStatusNotFoundException(id));

        try {
            inquiryStatusRepository.delete(inquiryStatus);
            inquiryStatusRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new InquiryStatusInUseException(id);
        }
    }
}