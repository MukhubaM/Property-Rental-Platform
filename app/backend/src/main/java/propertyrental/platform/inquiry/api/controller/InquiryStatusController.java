package propertyrental.platform.inquiry.api.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import propertyrental.platform.inquiry.api.dto.response.InquiryStatusResponse;
import propertyrental.platform.inquiry.application.service.InquiryStatusService;

@RestController
@RequestMapping("/api/v1/inquiry-statuses")
@RequiredArgsConstructor
@Tag(
        name = "Inquiry Statuses",
        description = "APIs for retrieving the statuses used to track rental inquiries on the platform."
)
public class InquiryStatusController {

    private final InquiryStatusService inquiryStatusService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Get inquiry status by ID",
            description = "Retrieves a specific inquiry status using its unique identifier."
    )
    public InquiryStatusResponse getInquiryStatusById(@PathVariable("id") Long id) {

        return inquiryStatusService.getInquiryStatusById(id);
    }

    @GetMapping
    @Operation(
            summary = "Get all inquiry statuses",
            description = "Retrieves a list of all inquiry statuses available on the platform."
    )
    public List<InquiryStatusResponse> getAllInquiryStatuses() {

        return inquiryStatusService.getAllInquiryStatuses();
    }
}