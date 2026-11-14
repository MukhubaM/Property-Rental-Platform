
package propertyrental.platform.moderation.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(
        name = "Report Reasons",
        description = "APIs for managing the predefined reasons used when reporting properties or rental units."
)
public class ReportReasonController {

    private final ReportReasonService reportReasonService;

    @GetMapping
    @Operation(
            summary = "Get all report reasons",
            description = "Retrieves a list of all available report reasons that users can select when reporting a property or rental unit."
    )
    public List<ReportReasonResponse> getAllReportReasons() {
        return reportReasonService.getAllReportReasons();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get report reason by ID",
            description = "Retrieves a specific report reason using its unique identifier."
    )
    public ReportReasonResponse getReportReasonById(@PathVariable("id") Long id) {
        return reportReasonService.getReportReasonById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a report reason",
            description = "Creates a new report reason that can be used when submitting property or rental unit reports."
    )
    public ReportReasonResponse createReportReason(@Valid @RequestBody CreateReportReasonRequest request) {
        return reportReasonService.createReportReason(request);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a report reason",
            description = "Updates an existing report reason identified by its ID using the information provided in the request body."
    )
    public ReportReasonResponse updateReportReason(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateReportReasonRequest request) {
        return reportReasonService.updateReportReason(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a report reason",
            description = "Deletes an existing report reason using its unique identifier."
    )
    public void deleteReportReason(@PathVariable("id") Long id) {
        reportReasonService.deleteReportReason(id);
    }
}
