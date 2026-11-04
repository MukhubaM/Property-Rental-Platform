package propertyrental.platform.inquiry.application.mapper;


import org.springframework.stereotype.Component;
import propertyrental.platform.inquiry.api.dto.response.InquiryStatusResponse;
import propertyrental.platform.inquiry.infrastructure.persistence.entity.InquiryStatusEntity;

@Component
public class InquiryStatusMapper {

    // Convert an entity to the response DTO, so the entity is never exposed
    public InquiryStatusResponse toResponse(InquiryStatusEntity status) {
        return new InquiryStatusResponse(
                status.getId(),
                status.getCode(),
                status.getName()
        );
    }
}