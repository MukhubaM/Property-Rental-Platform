package propertyrental.platform.moderation.api.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import propertyrental.platform.moderation.api.dto.response.ReportStatusResponse;
import propertyrental.platform.moderation.application.service.ReportStatusService;

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
}