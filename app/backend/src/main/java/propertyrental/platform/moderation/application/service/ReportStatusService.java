package propertyrental.platform.moderation.application.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propertyrental.platform.moderation.api.dto.response.ReportStatusResponse;
import propertyrental.platform.moderation.application.mapper.ReportStatusMapper;
import propertyrental.platform.moderation.infrastructure.persistence.repository.ReportStatusRepository;
import propertyrental.platform.moderation.domain.ReportStatusNotFoundException;
import propertyrental.platform.moderation.api.dto.request.CreateReportStatusRequest;
import propertyrental.platform.moderation.domain.ReportStatusAlreadyExistsException;
import propertyrental.platform.moderation.infrastructure.persistence.entity.ReportStatusEntity;
import propertyrental.platform.moderation.api.dto.request.UpdateReportStatusRequest;

@Service
@RequiredArgsConstructor
public class ReportStatusService {

    private final ReportStatusRepository reportStatusRepository;
    private final ReportStatusMapper reportStatusMapper;

    // This returns all report statuses
    @Transactional
    public List<ReportStatusResponse> getAllReportStatuses() {

        return reportStatusRepository.findAll().stream()
                .map(reportStatusMapper::toResponse)
                .toList();
    }

    // This return the report status or fail with 404 if the id doesn't exist
    @Transactional
    public ReportStatusResponse getReportStatusById(Long id) {

        return reportStatusRepository.findById(id)
                .map(reportStatusMapper::toResponse)
                .orElseThrow(() -> new ReportStatusNotFoundException(id));
    }

    @Transactional
    public ReportStatusResponse createReportStatus(CreateReportStatusRequest request) {

        // This reject the request (409) if the code or name is already taken
        if (reportStatusRepository.existsByCode(request.code())) {
            throw new ReportStatusAlreadyExistsException("code", request.code());
        }
        if (reportStatusRepository.existsByName(request.name())) {
            throw new ReportStatusAlreadyExistsException("name", request.name());
        }

        ReportStatusEntity saved = reportStatusRepository.save(reportStatusMapper.toEntity(request));

        return reportStatusMapper.toResponse(saved);
    }

    @Transactional
    public ReportStatusResponse updateReportStatus(Long id, UpdateReportStatusRequest request) {

        // This load the report status to update or fail with 404 if the id doesn't exist
        ReportStatusEntity reportStatus = reportStatusRepository.findById(id)
                .orElseThrow(() -> new ReportStatusNotFoundException(id));

        // This one reject the update (409) if a different report status already uses this code or name
        if (reportStatusRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new ReportStatusAlreadyExistsException("code", request.code());
        }
        if (reportStatusRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ReportStatusAlreadyExistsException("name", request.name());
        }

        // This method applies the new values to the existing entity and save it
        reportStatusMapper.updateEntity(reportStatus, request);

        return reportStatusMapper.toResponse(reportStatusRepository.save(reportStatus));
    }
}