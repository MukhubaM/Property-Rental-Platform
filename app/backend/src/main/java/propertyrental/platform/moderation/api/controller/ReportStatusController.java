package propertyrental.platform.moderation.api.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import propertyrental.platform.moderation.api.dto.response.ReportStatusResponse;
import propertyrental.platform.moderation.application.service.ReportStatusService;
import propertyrental.platform.moderation.api.dto.request.CreateReportStatusRequest;
import propertyrental.platform.moderation.api.dto.request.UpdateReportStatusRequest;

@RestController
@RequestMapping("/api/v1/report-statuses")
@RequiredArgsConstructor
public class ReportStatusController {

    private final ReportStatusService reportStatusService;

    @GetMapping
    public List<ReportStatusResponse> getAllReportStatuses() {

        return reportStatusService.getAllReportStatuses();
    }

    @GetMapping("/{id}")
    public ReportStatusResponse getReportStatusById(@PathVariable("id") Long id) {

        return reportStatusService.getReportStatusById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportStatusResponse createReportStatus(@Valid @RequestBody CreateReportStatusRequest request) {

        return reportStatusService.createReportStatus(request);
    }

    @PutMapping("/{id}")
    public ReportStatusResponse updateReportStatus(@PathVariable("id")
                                                   Long id,
                                                   @Valid @RequestBody
                                                   UpdateReportStatusRequest request) {

        return reportStatusService.updateReportStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReportStatus(@PathVariable("id") Long id) {
        reportStatusService.deleteReportStatus(id);
    }
}

