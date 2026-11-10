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

import propertyrental.platform.listing.api.dto.request.CreateUnitTypeRequest;
import propertyrental.platform.listing.api.dto.request.UpdateUnitTypeRequest;
import propertyrental.platform.listing.api.dto.response.UnitTypeResponse;
import propertyrental.platform.listing.application.mapper.UnitTypeMapper;
import propertyrental.platform.listing.application.service.UnitTypeService;
import propertyrental.platform.listing.domain.UnitTypeAlreadyExistsException;
import propertyrental.platform.listing.domain.UnitTypeInUseException;
import propertyrental.platform.listing.domain.UnitTypeNotFoundException;
import propertyrental.platform.listing.infrastructure.persistence.entity.UnitTypeEntity;
import propertyrental.platform.listing.infrastructure.persistence.repository.UnitTypeRepository;

class UnitTypeServiceTest {

    private final UnitTypeRepository repository = mock(UnitTypeRepository.class);
    private final UnitTypeService service = new UnitTypeService(repository, new UnitTypeMapper());

    private UnitTypeEntity unitType(Long id, String code, String name) {
        UnitTypeEntity entity = new UnitTypeEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setName(name);
        return entity;
    }

    // ---- get all / get by id ----
    @Test
    void getAllReturnsMappedUnitTypes() {
        when(repository.findAll()).thenReturn(List.of(unitType(3L, "ROOM", "Room")));

        assertThat(service.getAllUnitTypes())
                .containsExactly(new UnitTypeResponse(3L, "ROOM", "Room"));
    }

    @Test
    void getByIdReturnsUnitTypeWhenFound() {
        when(repository.findById(3L)).thenReturn(Optional.of(unitType(3L, "ROOM", "Room")));

        assertThat(service.getUnitTypeById(3L))
                .isEqualTo(new UnitTypeResponse(3L, "ROOM", "Room"));
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUnitTypeById(99L))
                .isInstanceOf(UnitTypeNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ---- create ----
    @Test
    void createSavesAndReturnsNewUnitType() {
        when(repository.save(any(UnitTypeEntity.class))).thenAnswer(invocation -> {
            UnitTypeEntity saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        UnitTypeResponse response = service.createUnitType(new CreateUnitTypeRequest("TEMP", "Temp Type"));

        assertThat(response).isEqualTo(new UnitTypeResponse(10L, "TEMP", "Temp Type"));
    }

    @Test
    void createThrowsWhenCodeAlreadyExists() {
        when(repository.existsByCode("ROOM")).thenReturn(true);

        assertThatThrownBy(() -> service.createUnitType(new CreateUnitTypeRequest("ROOM", "Other")))
                .isInstanceOf(UnitTypeAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(repository, never()).save(any());
    }

    @Test
    void createThrowsWhenNameAlreadyExists() {
        when(repository.existsByName("Room")).thenReturn(true);

        assertThatThrownBy(() -> service.createUnitType(new CreateUnitTypeRequest("OTHER_CODE", "Room")))
                .isInstanceOf(UnitTypeAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(repository, never()).save(any());
    }

    // ---- update ----
    @Test
    void updateSavesAndReturnsUpdatedUnitType() {
        UnitTypeEntity entity = unitType(10L, "TEMP", "Temp Type");
        when(repository.findById(10L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        UnitTypeResponse response = service.updateUnitType(10L, new UpdateUnitTypeRequest("TEMP", "Temp Type 2"));

        assertThat(response).isEqualTo(new UnitTypeResponse(10L, "TEMP", "Temp Type 2"));
    }

    @Test
    void updateThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateUnitType(99L, new UpdateUnitTypeRequest("X", "Y")))
                .isInstanceOf(UnitTypeNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void updateThrowsWhenCodeBelongsToAnotherUnitType() {
        when(repository.findById(10L)).thenReturn(Optional.of(unitType(10L, "TEMP", "Temp Type")));
        when(repository.existsByCodeAndIdNot("ROOM", 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateUnitType(10L, new UpdateUnitTypeRequest("ROOM", "Temp Type")))
                .isInstanceOf(UnitTypeAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(repository, never()).save(any());
    }

    @Test
    void updateThrowsWhenNameBelongsToAnotherUnitType() {
        when(repository.findById(10L)).thenReturn(Optional.of(unitType(10L, "TEMP", "Temp Type")));
        when(repository.existsByNameAndIdNot("Room", 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateUnitType(10L, new UpdateUnitTypeRequest("TEMP", "Room")))
                .isInstanceOf(UnitTypeAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(repository, never()).save(any());
    }

    // ---- delete ----
    @Test
    void deleteRemovesExistingUnitType() {
        UnitTypeEntity entity = unitType(10L, "TEMP", "Temp Type");
        when(repository.findById(10L)).thenReturn(Optional.of(entity));

        service.deleteUnitType(10L);

        verify(repository).delete(entity);
    }

    @Test
    void deleteThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteUnitType(99L))
                .isInstanceOf(UnitTypeNotFoundException.class);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteThrowsWhenUnitTypeIsStillUsedByRentalUnits() {
        when(repository.findById(3L)).thenReturn(Optional.of(unitType(3L, "ROOM", "Room")));
        doThrow(new DataIntegrityViolationException("fk_rental_units_type"))
                .when(repository).flush();

        assertThatThrownBy(() -> service.deleteUnitType(3L))
                .isInstanceOf(UnitTypeInUseException.class)
                .hasMessageContaining("3");
    }
}