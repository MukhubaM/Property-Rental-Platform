package propertyrental.platform.moderation.api.controller;

import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import propertyrental.platform.moderation.api.dto.request.CreateReportReasonRequest;
import propertyrental.platform.moderation.api.dto.request.UpdateReportReasonRequest;
import propertyrental.platform.moderation.api.dto.response.ReportReasonResponse;
import propertyrental.platform.moderation.application.service.ReportReasonService;

@RestController
@RequestMapping("/api/v1/report-reasons")
@RequiredArgsConstructor
public class ReportReasonController {

    private final ReportReasonService reportReasonService;

    @GetMapping
    public List<ReportReasonResponse> getAllReportReasons() {

        return reportReasonService.getAllReportReasons();
    }

    @GetMapping("/{id}")
    public ReportReasonResponse getReportReasonById(@PathVariable("id") Long id) {

        return reportReasonService.getReportReasonById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportReasonResponse createReportReason(@Valid @RequestBody CreateReportReasonRequest request) {

        return reportReasonService.createReportReason(request);
    }

    @PutMapping("/{id}")
    public ReportReasonResponse updateReportReason(@PathVariable("id")
                                                   Long id,
                                                   @Valid @RequestBody
                                                   UpdateReportReasonRequest request) {

        return reportReasonService.updateReportReason(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReportReason(@PathVariable("id") Long id) {
        reportReasonService.deleteReportReason(id);
    }
}
