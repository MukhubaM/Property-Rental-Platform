package propertyrental.platform.moderation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import propertyrental.platform.moderation.api.dto.response.ReportStatusResponse;
import propertyrental.platform.moderation.application.mapper.ReportStatusMapper;
import propertyrental.platform.moderation.application.service.ReportStatusService;
import propertyrental.platform.moderation.domain.ReportStatusNotFoundException;
import propertyrental.platform.moderation.infrastructure.persistence.entity.ReportStatusEntity;
import propertyrental.platform.moderation.infrastructure.persistence.repository.ReportStatusRepository;
import propertyrental.platform.moderation.api.dto.request.CreateReportStatusRequest;
import propertyrental.platform.moderation.domain.ReportStatusAlreadyExistsException;
import propertyrental.platform.moderation.api.dto.request.UpdateReportStatusRequest;

class ReportStatusServiceTest {

    private final ReportStatusRepository repository = mock(ReportStatusRepository.class);
    private final ReportStatusService service = new ReportStatusService(repository, new ReportStatusMapper());

    private ReportStatusEntity reportStatus(Long id, String code, String name) {
        ReportStatusEntity entity = new ReportStatusEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setName(name);

        return entity;
    }

    // ---- get all ----
    @Test
    void getAllReturnsMappedReportStatuses() {
        when(repository.findAll()).thenReturn(List.of(
                reportStatus(1L, "OPEN", "Open"),
                reportStatus(2L, "UNDER_REVIEW", "Under Review")));

        assertThat(service.getAllReportStatuses()).containsExactly(
                new ReportStatusResponse(1L, "OPEN", "Open"),
                new ReportStatusResponse(2L, "UNDER_REVIEW", "Under Review"));
    }

    @Test
    void getAllReturnsEmptyListWhenThereAreNoReportStatuses() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.getAllReportStatuses()).isEmpty();
    }

    // ---- get by id ----
    @Test
    void getByIdReturnsReportStatusWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(reportStatus(1L, "OPEN", "Open")));

        assertThat(service.getReportStatusById(1L))
                .isEqualTo(new ReportStatusResponse(1L, "OPEN", "Open"));
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getReportStatusById(99L))
                .isInstanceOf(ReportStatusNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ---- create ----
    @Test
    void createSavesAndReturnsNewReportStatus() {
        when(repository.save(any(ReportStatusEntity.class))).thenAnswer(invocation -> {
            ReportStatusEntity saved = invocation.getArgument(0);
            saved.setId(5L);

            return saved;
        });

        ReportStatusResponse response = service.createReportStatus(
                new CreateReportStatusRequest("TEMP", "Temp Status"));

        assertThat(response).isEqualTo(new ReportStatusResponse(5L, "TEMP", "Temp Status"));
    }

    @Test
    void createThrowsWhenCodeAlreadyExists() {
        when(repository.existsByCode("OPEN")).thenReturn(true);

        assertThatThrownBy(() -> service.createReportStatus(new CreateReportStatusRequest("OPEN", "Other")))
                .isInstanceOf(ReportStatusAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(repository, never()).save(any());
    }

    @Test
    void createThrowsWhenNameAlreadyExists() {
        when(repository.existsByName("Open")).thenReturn(true);

        assertThatThrownBy(() -> service.createReportStatus(new CreateReportStatusRequest("NEW_CODE", "Open")))
                .isInstanceOf(ReportStatusAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(repository, never()).save(any());
    }

    // ---- update ----
    @Test
    void updateSavesAndReturnsUpdatedReportStatus() {
        ReportStatusEntity entity = reportStatus(5L, "TEMP", "Temp Status");
        when(repository.findById(5L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        ReportStatusResponse response = service.updateReportStatus(
                5L, new UpdateReportStatusRequest("TEMP", "Temp Status 2"));

        assertThat(response).isEqualTo(new ReportStatusResponse(5L, "TEMP", "Temp Status 2"));
    }

    @Test
    void updateThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateReportStatus(99L, new UpdateReportStatusRequest("X", "Y")))
                .isInstanceOf(ReportStatusNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void updateThrowsWhenCodeBelongsToAnotherReportStatus() {
        when(repository.findById(5L)).thenReturn(Optional.of(reportStatus(5L, "TEMP", "Temp Status")));
        when(repository.existsByCodeAndIdNot("OPEN", 5L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateReportStatus(
                5L, new UpdateReportStatusRequest("OPEN", "Temp Status")))
                .isInstanceOf(ReportStatusAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(repository, never()).save(any());
    }

    @Test
    void updateThrowsWhenNameBelongsToAnotherReportStatus() {
        when(repository.findById(5L)).thenReturn(Optional.of(reportStatus(5L, "TEMP", "Temp Status")));
        when(repository.existsByNameAndIdNot("Open", 5L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateReportStatus(
                5L, new UpdateReportStatusRequest("TEMP", "Open")))
                .isInstanceOf(ReportStatusAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(repository, never()).save(any());
    }
}