package propertyrental.platform.inquiry.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import propertyrental.platform.inquiry.api.dto.response.InquiryStatusResponse;
import propertyrental.platform.inquiry.application.mapper.InquiryStatusMapper;
import propertyrental.platform.inquiry.application.service.InquiryStatusService;
import propertyrental.platform.inquiry.domain.InquiryStatusNotFoundException;
import propertyrental.platform.inquiry.infrastructure.persistence.entity.InquiryStatusEntity;
import propertyrental.platform.inquiry.infrastructure.persistence.repository.InquiryStatusRepository;
import propertyrental.platform.inquiry.api.dto.request.CreateInquiryStatusRequest;
import propertyrental.platform.inquiry.domain.InquiryStatusAlreadyExistsException;
import propertyrental.platform.inquiry.api.dto.request.UpdateInquiryStatusRequest;
import propertyrental.platform.inquiry.domain.InquiryStatusInUseException;

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

    // ---- create ----
    @Test
    void createSavesAndReturnsNewInquiryStatus() {
        when(repository.save(any(InquiryStatusEntity.class))).thenAnswer(invocation -> {
            InquiryStatusEntity saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });

        InquiryStatusResponse response = service.createInquiryStatus(
                new CreateInquiryStatusRequest("TEMP", "Temp Status"));

        assertThat(response).isEqualTo(new InquiryStatusResponse(5L, "TEMP", "Temp Status"));
    }

    @Test
    void createThrowsWhenCodeAlreadyExists() {
        when(repository.existsByCode("NEW")).thenReturn(true);

        assertThatThrownBy(() -> service.createInquiryStatus(new CreateInquiryStatusRequest("NEW", "Other")))
                .isInstanceOf(InquiryStatusAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(repository, never()).save(any());
    }

    @Test
    void createThrowsWhenNameAlreadyExists() {
        when(repository.existsByName("New")).thenReturn(true);

        assertThatThrownBy(() -> service.createInquiryStatus(new CreateInquiryStatusRequest("NEW_CODE", "New")))
                .isInstanceOf(InquiryStatusAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(repository, never()).save(any());
    }

    // ---- update ----
    private InquiryStatusEntity updateTarget() {
        InquiryStatusEntity entity = new InquiryStatusEntity();
        entity.setId(5L);
        entity.setCode("TEMP");
        entity.setName("Temp Status");

        return entity;
    }

    @Test
    void updateSavesAndReturnsUpdatedInquiryStatus() {
        InquiryStatusEntity entity = updateTarget();
        when(repository.findById(5L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        InquiryStatusResponse response = service.updateInquiryStatus(
                5L, new UpdateInquiryStatusRequest("TEMP", "Temp Status 2"));

        assertThat(response).isEqualTo(new InquiryStatusResponse(5L, "TEMP", "Temp Status 2"));
    }

    @Test
    void updateThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateInquiryStatus(99L, new UpdateInquiryStatusRequest("X", "Y")))
                .isInstanceOf(InquiryStatusNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void updateThrowsWhenCodeBelongsToAnotherInquiryStatus() {
        when(repository.findById(5L)).thenReturn(Optional.of(updateTarget()));
        when(repository.existsByCodeAndIdNot("NEW", 5L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateInquiryStatus(
                5L, new UpdateInquiryStatusRequest("NEW", "Temp Status")))
                .isInstanceOf(InquiryStatusAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(repository, never()).save(any());
    }

    @Test
    void updateThrowsWhenNameBelongsToAnotherInquiryStatus() {
        when(repository.findById(5L)).thenReturn(Optional.of(updateTarget()));
        when(repository.existsByNameAndIdNot("New", 5L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateInquiryStatus(
                5L, new UpdateInquiryStatusRequest("TEMP", "New")))
                .isInstanceOf(InquiryStatusAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(repository, never()).save(any());
    }

    // ---- delete ----
    private InquiryStatusEntity deletableStatus(Long id, String code, String name) {
        InquiryStatusEntity entity = new InquiryStatusEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setName(name);

        return entity;
    }

    @Test
    void deleteRemovesExistingInquiryStatus() {
        InquiryStatusEntity entity = deletableStatus(5L, "TEMP", "Temp Status");
        when(repository.findById(5L)).thenReturn(Optional.of(entity));

        service.deleteInquiryStatus(5L);

        verify(repository).delete(entity);
    }

    @Test
    void deleteThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteInquiryStatus(99L))
                .isInstanceOf(InquiryStatusNotFoundException.class)
                .hasMessageContaining("99");
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteThrowsWhenInquiryStatusIsStillUsedByInquiries() {
        when(repository.findById(1L)).thenReturn(Optional.of(deletableStatus(1L, "NEW", "New")));
        doThrow(new DataIntegrityViolationException("fk_inquiries_status"))
                .when(repository).flush();

        assertThatThrownBy(() -> service.deleteInquiryStatus(1L))
                .isInstanceOf(InquiryStatusInUseException.class)
                .hasMessageContaining("1");
    }
}