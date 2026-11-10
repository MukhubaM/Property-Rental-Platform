package propertyrental.platform.moderation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.dao.DataIntegrityViolationException;

import propertyrental.platform.moderation.api.dto.request.CreateReportReasonRequest;
import propertyrental.platform.moderation.api.dto.request.UpdateReportReasonRequest;
import propertyrental.platform.moderation.api.dto.response.ReportReasonResponse;
import propertyrental.platform.moderation.application.mapper.ReportReasonMapper;
import propertyrental.platform.moderation.application.service.ReportReasonService;
import propertyrental.platform.moderation.domain.ReportReasonAlreadyExistsException;
import propertyrental.platform.moderation.domain.ReportReasonInUseException;
import propertyrental.platform.moderation.domain.ReportReasonNotFoundException;
import propertyrental.platform.moderation.infrastructure.persistence.entity.ReportReasonEntity;
import propertyrental.platform.moderation.infrastructure.persistence.repository.ReportReasonRepository;

class ReportReasonServiceTest {

    private final ReportReasonRepository repository = mock(ReportReasonRepository.class);
    private final ReportReasonService service = new ReportReasonService(repository, new ReportReasonMapper());

    private ReportReasonEntity reportReason(Long id, String code, String name) {
        ReportReasonEntity entity = new ReportReasonEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setName(name);

        return entity;
    }

    // ---- get all / get by id ----
    @Test
    void getAllReturnsMappedReportReasons() {
        when(repository.findAll()).thenReturn(List.of(reportReason(1L, "SUSPECTED_SCAM", "Suspected Scam")));

        assertThat(service.getAllReportReasons())
                .containsExactly(new ReportReasonResponse(1L, "SUSPECTED_SCAM", "Suspected Scam"));
    }

    @Test
    void getByIdReturnsReportReasonWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(reportReason(1L, "SUSPECTED_SCAM", "Suspected Scam")));

        assertThat(service.getReportReasonById(1L))
                .isEqualTo(new ReportReasonResponse(1L, "SUSPECTED_SCAM", "Suspected Scam"));
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getReportReasonById(99L))
                .isInstanceOf(ReportReasonNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ---- create ----
    @Test
    void createSavesAndReturnsNewReportReason() {
        when(repository.save(any(ReportReasonEntity.class))).thenAnswer(invocation -> {
            ReportReasonEntity saved = invocation.getArgument(0);
            saved.setId(10L);

            return saved;
        });

        ReportReasonResponse response = service.createReportReason(new CreateReportReasonRequest("TEMP", "Temp Reason"));

        assertThat(response).isEqualTo(new ReportReasonResponse(10L, "TEMP", "Temp Reason"));
    }

    @Test
    void createThrowsWhenCodeAlreadyExists() {
        when(repository.existsByCode("OTHER")).thenReturn(true);

        assertThatThrownBy(() -> service.createReportReason(new CreateReportReasonRequest("OTHER", "Another")))
                .isInstanceOf(ReportReasonAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(repository, never()).save(any());
    }

    @Test
    void createThrowsWhenNameAlreadyExists() {
        when(repository.existsByName("Other")).thenReturn(true);

        assertThatThrownBy(() -> service.createReportReason(new CreateReportReasonRequest("NEW_CODE", "Other")))
                .isInstanceOf(ReportReasonAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(repository, never()).save(any());
    }

    // ---- update ----
    @Test
    void updateSavesAndReturnsUpdatedReportReason() {
        ReportReasonEntity entity = reportReason(10L, "TEMP", "Temp Reason");
        when(repository.findById(10L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        ReportReasonResponse response = service.updateReportReason(10L, new UpdateReportReasonRequest("TEMP", "Temp Reason 2"));

        assertThat(response).isEqualTo(new ReportReasonResponse(10L, "TEMP", "Temp Reason 2"));
    }

    @Test
    void updateThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateReportReason(99L, new UpdateReportReasonRequest("X", "Y")))
                .isInstanceOf(ReportReasonNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void updateThrowsWhenCodeBelongsToAnotherReportReason() {
        when(repository.findById(10L)).thenReturn(Optional.of(reportReason(10L, "TEMP", "Temp Reason")));
        when(repository.existsByCodeAndIdNot("OTHER", 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateReportReason(10L, new UpdateReportReasonRequest("OTHER", "Temp Reason")))
                .isInstanceOf(ReportReasonAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(repository, never()).save(any());
    }

    @Test
    void updateThrowsWhenNameBelongsToAnotherReportReason() {
        when(repository.findById(10L)).thenReturn(Optional.of(reportReason(10L, "TEMP", "Temp Reason")));
        when(repository.existsByNameAndIdNot("Other", 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateReportReason(10L, new UpdateReportReasonRequest("TEMP", "Other")))
                .isInstanceOf(ReportReasonAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(repository, never()).save(any());
    }

    // ---- delete ----
    @Test
    void deleteRemovesExistingReportReason() {
        ReportReasonEntity entity = reportReason(10L, "TEMP", "Temp Reason");
        when(repository.findById(10L)).thenReturn(Optional.of(entity));

        service.deleteReportReason(10L);

        verify(repository).delete(entity);
    }

    @Test
    void deleteThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteReportReason(99L))
                .isInstanceOf(ReportReasonNotFoundException.class);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteThrowsWhenReportReasonIsStillUsedByListingReports() {
        when(repository.findById(1L)).thenReturn(Optional.of(reportReason(1L, "SUSPECTED_SCAM", "Suspected Scam")));
        doThrow(new DataIntegrityViolationException("fk_listing_reports_reason"))
                .when(repository).flush();

        assertThatThrownBy(() -> service.deleteReportReason(1L))
                .isInstanceOf(ReportReasonInUseException.class)
                .hasMessageContaining("1");
    }
}
