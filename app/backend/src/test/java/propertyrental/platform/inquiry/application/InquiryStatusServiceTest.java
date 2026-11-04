package propertyrental.platform.inquiry.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import propertyrental.platform.inquiry.api.dto.response.InquiryStatusResponse;
import propertyrental.platform.inquiry.application.mapper.InquiryStatusMapper;
import propertyrental.platform.inquiry.application.service.InquiryStatusService;
import propertyrental.platform.inquiry.domain.InquiryStatusNotFoundException;
import propertyrental.platform.inquiry.infrastructure.persistence.entity.InquiryStatusEntity;
import propertyrental.platform.inquiry.infrastructure.persistence.repository.InquiryStatusRepository;

class InquiryStatusServiceTest {

    private final InquiryStatusRepository repository = mock(InquiryStatusRepository.class);
    private final InquiryStatusService service = new InquiryStatusService(repository, new InquiryStatusMapper());

    private InquiryStatusEntity status(Long id, String code, String name) {
        InquiryStatusEntity entity = new InquiryStatusEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setName(name);
        return entity;
    }

    // ---- get all ----
    @Test
    void getAllReturnsMappedStatuses() {
        when(repository.findAll()).thenReturn(List.of(
                status(1L, "NEW", "New"),
                status(2L, "RESPONDED", "Responded")));

        assertThat(service.getAllInquiryStatuses()).containsExactly(
                new InquiryStatusResponse(1L, "NEW", "New"),
                new InquiryStatusResponse(2L, "RESPONDED", "Responded"));
    }

    @Test
    void getAllReturnsEmptyListWhenThereAreNoStatuses() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.getAllInquiryStatuses()).isEmpty();
    }

    // ---- get by id ----
    @Test
    void getByIdReturnsStatusWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(status(1L, "NEW", "New")));

        assertThat(service.getInquiryStatusById(1L))
                .isEqualTo(new InquiryStatusResponse(1L, "NEW", "New"));
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getInquiryStatusById(99L))
                .isInstanceOf(InquiryStatusNotFoundException.class)
                .hasMessageContaining("99");
    }
}