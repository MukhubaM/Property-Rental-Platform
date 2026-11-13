package propertyrental.platform.listing.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import propertyrental.platform.listing.api.dto.response.AvailabilityStatusResponse;
import propertyrental.platform.listing.application.mapper.AvailabilityStatusMapper;
import propertyrental.platform.listing.application.service.AvailabilityStatusService;
import propertyrental.platform.listing.domain.AvailabilityStatusNotFoundException;
import propertyrental.platform.listing.infrastructure.persistence.entity.AvailabilityStatusEntity;
import propertyrental.platform.listing.infrastructure.persistence.repository.AvailabilityStatusRepository;

class AvailabilityStatusServiceTest {

    private final AvailabilityStatusRepository repository = mock(AvailabilityStatusRepository.class);
    private final AvailabilityStatusService service =
            new AvailabilityStatusService(repository, new AvailabilityStatusMapper());

    private AvailabilityStatusEntity availabilityStatus(Long id, String code, String name) {
        AvailabilityStatusEntity entity = new AvailabilityStatusEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setName(name);

        return entity;
    }

    // ---- get all ----
    @Test
    void getAllReturnsMappedAvailabilityStatuses() {
        when(repository.findAll()).thenReturn(List.of(
                availabilityStatus(1L, "AVAILABLE", "Available"),
                availabilityStatus(2L, "OCCUPIED", "Occupied")));

        assertThat(service.getAllAvailabilityStatuses()).containsExactly(
                new AvailabilityStatusResponse(1L, "AVAILABLE", "Available"),
                new AvailabilityStatusResponse(2L, "OCCUPIED", "Occupied"));
    }

    @Test
    void getAllReturnsEmptyListWhenThereAreNoAvailabilityStatuses() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.getAllAvailabilityStatuses()).isEmpty();
    }

    // ---- get by id ----
    @Test
    void getByIdReturnsAvailabilityStatusWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(availabilityStatus(1L, "AVAILABLE", "Available")));

        assertThat(service.getAvailabilityStatusById(1L))
                .isEqualTo(new AvailabilityStatusResponse(1L, "AVAILABLE", "Available"));
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAvailabilityStatusById(99L))
                .isInstanceOf(AvailabilityStatusNotFoundException.class)
                .hasMessageContaining("99");
    }
}