package propertyrental.platform.moderation.application.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propertyrental.platform.moderation.api.dto.request.CreateReportReasonRequest;
import propertyrental.platform.moderation.api.dto.request.UpdateReportReasonRequest;
import propertyrental.platform.moderation.api.dto.response.ReportReasonResponse;
import propertyrental.platform.moderation.application.mapper.ReportReasonMapper;
import propertyrental.platform.moderation.domain.ReportReasonAlreadyExistsException;
import propertyrental.platform.moderation.domain.ReportReasonInUseException;
import propertyrental.platform.moderation.domain.ReportReasonNotFoundException;
import propertyrental.platform.moderation.infrastructure.persistence.entity.ReportReasonEntity;
import propertyrental.platform.moderation.infrastructure.persistence.repository.ReportReasonRepository;

@Service
@RequiredArgsConstructor
public class ReportReasonService {

    private final ReportReasonRepository reportReasonRepository;
    private final ReportReasonMapper reportReasonMapper;

    @Transactional
    public List<ReportReasonResponse> getAllReportReasons() {

        return reportReasonRepository.findAll().stream()
                .map(reportReasonMapper::toResponse)
                .toList();
    }

    @Transactional
    public ReportReasonResponse getReportReasonById(Long id) {

        return reportReasonMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public ReportReasonResponse createReportReason(CreateReportReasonRequest request) {

        // Reject the request (409) if the code or name is already taken
        if (reportReasonRepository.existsByCode(request.code())) {
            throw new ReportReasonAlreadyExistsException("code", request.code());
        }
        if (reportReasonRepository.existsByName(request.name())) {
            throw new ReportReasonAlreadyExistsException("name", request.name());
        }

        ReportReasonEntity saved = reportReasonRepository.save(reportReasonMapper.toEntity(request));

        return reportReasonMapper.toResponse(saved);
    }

    @Transactional
    public ReportReasonResponse updateReportReason(Long id, UpdateReportReasonRequest request) {

        // Load the report reason to update or fail with 404 if the id doesn't exist
        ReportReasonEntity reportReason = findOrThrow(id);

        // Reject the update (409) if a different report reason already uses this code or name
        if (reportReasonRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new ReportReasonAlreadyExistsException("code", request.code());
        }
        if (reportReasonRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ReportReasonAlreadyExistsException("name", request.name());
        }

        // This applies the new values to the existing entity and save it
        reportReasonMapper.updateEntity(reportReason, request);

        return reportReasonMapper.toResponse(reportReasonRepository.save(reportReason));
    }

    @Transactional
    public void deleteReportReason(Long id) {
        ReportReasonEntity reportReason = findOrThrow(id);

        try {
            reportReasonRepository.delete(reportReason);
            reportReasonRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ReportReasonInUseException(id);
        }
    }

    // Shared lookup (returns the entity or throws the 404 exception)
    private ReportReasonEntity findOrThrow(Long id) {

        return reportReasonRepository.findById(id)
                .orElseThrow(() -> new ReportReasonNotFoundException(id));
    }
}
