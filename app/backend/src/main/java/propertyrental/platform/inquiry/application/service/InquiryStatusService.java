package propertyrental.platform.inquiry.application.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propertyrental.platform.inquiry.api.dto.response.InquiryStatusResponse;
import propertyrental.platform.inquiry.application.mapper.InquiryStatusMapper;
import propertyrental.platform.inquiry.domain.InquiryStatusNotFoundException;
import propertyrental.platform.inquiry.infrastructure.persistence.repository.InquiryStatusRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryStatusService {

    private final InquiryStatusRepository inquiryStatusRepository;
    private final InquiryStatusMapper inquiryStatusMapper;

    // Return the inquiry status or fail with 404 if the id doesn't exist
    @Transactional
    public InquiryStatusResponse getInquiryStatusById(Long id) {

        return inquiryStatusRepository.findById(id)
                .map(inquiryStatusMapper::toResponse)
                .orElseThrow(() -> new InquiryStatusNotFoundException(id));
    }

    // Return all inquiry statuses (an empty list if there are none)
    @Transactional
    public List<InquiryStatusResponse> getAllInquiryStatuses() {

        return inquiryStatusRepository.findAll().stream()
                .map(inquiryStatusMapper::toResponse)
                .toList();
    }
}