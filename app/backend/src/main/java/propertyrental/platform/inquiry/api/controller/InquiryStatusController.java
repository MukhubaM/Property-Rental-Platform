package propertyrental.platform.inquiry.api.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import propertyrental.platform.inquiry.api.dto.response.InquiryStatusResponse;
import propertyrental.platform.inquiry.application.service.InquiryStatusService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inquiry-statuses")
@RequiredArgsConstructor
public class InquiryStatusController {

    private final InquiryStatusService inquiryStatusService;

    @GetMapping("/{id}")
    public InquiryStatusResponse getInquiryStatusById(@PathVariable("id") Long id) {

        return inquiryStatusService.getInquiryStatusById(id);
    }

    @GetMapping
    public List<InquiryStatusResponse> getAllInquiryStatuses() {

        return inquiryStatusService.getAllInquiryStatuses();
    }
}