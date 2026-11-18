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
}