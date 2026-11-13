package propertyrental.platform.listing.application;

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

import propertyrental.platform.listing.api.dto.request.CreateFacilityRequest;
import propertyrental.platform.listing.api.dto.request.UpdateFacilityRequest;
import propertyrental.platform.listing.api.dto.response.FacilityResponse;
import propertyrental.platform.listing.application.mapper.FacilityMapper;
import propertyrental.platform.listing.application.service.FacilityService;
import propertyrental.platform.listing.domain.FacilityAlreadyExistsException;
import propertyrental.platform.listing.domain.FacilityInUseException;
import propertyrental.platform.listing.domain.FacilityNotFoundException;
import propertyrental.platform.listing.infrastructure.persistence.entity.FacilityEntity;
import propertyrental.platform.listing.infrastructure.persistence.repository.FacilityRepository;

class FacilityServiceTest {

    private final FacilityRepository repository = mock(FacilityRepository.class);
    private final FacilityService service = new FacilityService(repository, new FacilityMapper());

    private FacilityEntity facility(Long id, String code, String name) {
        FacilityEntity entity = new FacilityEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setName(name);

        return entity;
    }

    // ---- get all / get by id ----
    @Test
    void getAllReturnsMappedFacilities() {
        when(repository.findAll()).thenReturn(List.of(facility(1L, "WIFI", "Wi-Fi")));

        assertThat(service.getAllFacilities())
                .containsExactly(new FacilityResponse(1L, "WIFI", "Wi-Fi"));
    }

    @Test
    void getByIdReturnsFacilityWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(facility(1L, "WIFI", "Wi-Fi")));

        assertThat(service.getFacilityById(1L))
                .isEqualTo(new FacilityResponse(1L, "WIFI", "Wi-Fi"));
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getFacilityById(99L))
                .isInstanceOf(FacilityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ---- create ----
    @Test
    void createSavesAndReturnsNewFacility() {
        when(repository.save(any(FacilityEntity.class))).thenAnswer(invocation -> {
            FacilityEntity saved = invocation.getArgument(0);
            saved.setId(20L);

            return saved;
        });

        FacilityResponse response = service.createFacility(new CreateFacilityRequest("TEMP", "Temp Facility"));

        assertThat(response).isEqualTo(new FacilityResponse(20L, "TEMP", "Temp Facility"));
    }

    @Test
    void createThrowsWhenCodeAlreadyExists() {
        when(repository.existsByCode("WIFI")).thenReturn(true);

        assertThatThrownBy(() -> service.createFacility(new CreateFacilityRequest("WIFI", "Another")))
                .isInstanceOf(FacilityAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(repository, never()).save(any());
    }

    @Test
    void createThrowsWhenNameAlreadyExists() {
        when(repository.existsByName("Wi-Fi")).thenReturn(true);

        assertThatThrownBy(() -> service.createFacility(new CreateFacilityRequest("NEW_CODE", "Wi-Fi")))
                .isInstanceOf(FacilityAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(repository, never()).save(any());
    }

    // ---- update ----
    @Test
    void updateSavesAndReturnsUpdatedFacility() {
        FacilityEntity entity = facility(20L, "TEMP", "Temp Facility");
        when(repository.findById(20L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        FacilityResponse response = service.updateFacility(20L, new UpdateFacilityRequest("TEMP", "Temp Facility 2"));

        assertThat(response).isEqualTo(new FacilityResponse(20L, "TEMP", "Temp Facility 2"));
    }

    @Test
    void updateThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateFacility(99L, new UpdateFacilityRequest("X", "Y")))
                .isInstanceOf(FacilityNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void updateThrowsWhenCodeBelongsToAnotherFacility() {
        when(repository.findById(20L)).thenReturn(Optional.of(facility(20L, "TEMP", "Temp Facility")));
        when(repository.existsByCodeAndIdNot("WIFI", 20L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateFacility(20L, new UpdateFacilityRequest("WIFI", "Temp Facility")))
                .isInstanceOf(FacilityAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(repository, never()).save(any());
    }

    @Test
    void updateThrowsWhenNameBelongsToAnotherFacility() {
        when(repository.findById(20L)).thenReturn(Optional.of(facility(20L, "TEMP", "Temp Facility")));
        when(repository.existsByNameAndIdNot("Wi-Fi", 20L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateFacility(20L, new UpdateFacilityRequest("TEMP", "Wi-Fi")))
                .isInstanceOf(FacilityAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(repository, never()).save(any());
    }

    // ---- delete ----
    @Test
    void deleteRemovesExistingFacility() {
        FacilityEntity entity = facility(20L, "TEMP", "Temp Facility");
        when(repository.findById(20L)).thenReturn(Optional.of(entity));

        service.deleteFacility(20L);

        verify(repository).delete(entity);
    }

    @Test
    void deleteThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteFacility(99L))
                .isInstanceOf(FacilityNotFoundException.class);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteThrowsWhenFacilityIsStillUsedByPropertiesOrRentalUnits() {
        when(repository.findById(1L)).thenReturn(Optional.of(facility(1L, "WIFI", "Wi-Fi")));
        doThrow(new DataIntegrityViolationException("fk_property_facilities_facility"))
                .when(repository).flush();

        assertThatThrownBy(() -> service.deleteFacility(1L))
                .isInstanceOf(FacilityInUseException.class)
                .hasMessageContaining("1");
    }
}
