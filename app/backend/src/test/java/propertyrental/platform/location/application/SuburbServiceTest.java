package propertyrental.platform.location.application;

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

import propertyrental.platform.location.api.dto.request.CreateSuburbRequest;
import propertyrental.platform.location.api.dto.request.UpdateSuburbRequest;
import propertyrental.platform.location.api.dto.response.SuburbResponse;
import propertyrental.platform.location.application.mapper.SuburbMapper;
import propertyrental.platform.location.application.service.SuburbService;
import propertyrental.platform.location.domain.SuburbAlreadyExistsException;
import propertyrental.platform.location.domain.SuburbCityNotFoundException;
import propertyrental.platform.location.domain.SuburbInUseException;
import propertyrental.platform.location.domain.SuburbNotFoundException;
import propertyrental.platform.location.infrastructure.persistence.entity.SuburbEntity;
import propertyrental.platform.location.infrastructure.persistence.repository.CityRepository;
import propertyrental.platform.location.infrastructure.persistence.repository.SuburbRepository;

class SuburbServiceTest {

    private final SuburbRepository suburbRepository = mock(SuburbRepository.class);
    private final CityRepository cityRepository = mock(CityRepository.class);
    private final SuburbService service = new SuburbService(suburbRepository, cityRepository, new SuburbMapper());

    private SuburbEntity suburb(Long id, Long cityId, String name) {
        SuburbEntity entity = new SuburbEntity();
        entity.setId(id);
        entity.setCityId(cityId);
        entity.setName(name);

        return entity;
    }

    // ---- list/get by id ----
    @Test
    void getAllReturnsAllSuburbsWhenNoCityFilter() {
        when(suburbRepository.findAll()).thenReturn(List.of(suburb(1L, 5L, "Sandton")));

        assertThat(service.getAllSuburbs(null))
                .containsExactly(new SuburbResponse(1L, 5L, "Sandton"));
    }

    @Test
    void getAllReturnsOnlySuburbsOfTheGivenCity() {
        when(suburbRepository.findByCityId(5L)).thenReturn(List.of(suburb(1L, 5L, "Sandton")));

        assertThat(service.getAllSuburbs(5L))
                .containsExactly(new SuburbResponse(1L, 5L, "Sandton"));
        verify(suburbRepository, never()).findAll();
    }

    @Test
    void getByIdReturnsSuburbWhenFound() {
        when(suburbRepository.findById(1L)).thenReturn(Optional.of(suburb(1L, 5L, "Sandton")));

        assertThat(service.getSuburbById(1L)).isEqualTo(new SuburbResponse(1L, 5L, "Sandton"));
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(suburbRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSuburbById(99L))
                .isInstanceOf(SuburbNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ---- create ----
    @Test
    void createSavesAndReturnsNewSuburb() {
        when(cityRepository.existsById(5L)).thenReturn(true);
        when(suburbRepository.save(any(SuburbEntity.class))).thenAnswer(invocation -> {
            SuburbEntity saved = invocation.getArgument(0);
            saved.setId(10L);

            return saved;
        });

        SuburbResponse response = service.createSuburb(new CreateSuburbRequest(5L, "Temp Suburb"));

        assertThat(response).isEqualTo(new SuburbResponse(10L, 5L, "Temp Suburb"));
    }

    @Test
    void createThrowsWhenCityDoesNotExist() {
        when(cityRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.createSuburb(new CreateSuburbRequest(99L, "Temp Suburb")))
                .isInstanceOf(SuburbCityNotFoundException.class);
        verify(suburbRepository, never()).save(any());
    }

    @Test
    void createThrowsWhenNameAlreadyExistsInCity() {
        when(cityRepository.existsById(5L)).thenReturn(true);
        when(suburbRepository.existsByCityIdAndNameIgnoreCase(5L, "Sandton")).thenReturn(true);

        assertThatThrownBy(() -> service.createSuburb(new CreateSuburbRequest(5L, "Sandton")))
                .isInstanceOf(SuburbAlreadyExistsException.class);
        verify(suburbRepository, never()).save(any());
    }

    // ---- update ----
    @Test
    void updateSavesAndReturnsUpdatedSuburb() {
        SuburbEntity entity = suburb(10L, 5L, "Temp Suburb");
        when(suburbRepository.findById(10L)).thenReturn(Optional.of(entity));
        when(cityRepository.existsById(5L)).thenReturn(true);
        when(suburbRepository.save(entity)).thenReturn(entity);

        SuburbResponse response = service.updateSuburb(10L, new UpdateSuburbRequest(5L, "Temp Suburb 2"));

        assertThat(response).isEqualTo(new SuburbResponse(10L, 5L, "Temp Suburb 2"));
    }

    @Test
    void updateThrowsWhenNotFound() {
        when(suburbRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateSuburb(99L, new UpdateSuburbRequest(5L, "X")))
                .isInstanceOf(SuburbNotFoundException.class);
        verify(suburbRepository, never()).save(any());
    }

    @Test
    void updateThrowsWhenCityDoesNotExist() {
        when(suburbRepository.findById(10L)).thenReturn(Optional.of(suburb(10L, 5L, "Temp Suburb")));
        when(cityRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.updateSuburb(10L, new UpdateSuburbRequest(99L, "Temp Suburb")))
                .isInstanceOf(SuburbCityNotFoundException.class);
        verify(suburbRepository, never()).save(any());
    }

    @Test
    void updateThrowsWhenNameBelongsToAnotherSuburbInCity() {
        when(suburbRepository.findById(10L)).thenReturn(Optional.of(suburb(10L, 5L, "Temp Suburb")));
        when(cityRepository.existsById(5L)).thenReturn(true);
        when(suburbRepository.existsByCityIdAndNameIgnoreCaseAndIdNot(5L, "Sandton", 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateSuburb(10L, new UpdateSuburbRequest(5L, "Sandton")))
                .isInstanceOf(SuburbAlreadyExistsException.class);
        verify(suburbRepository, never()).save(any());
    }

    // ---- delete ----
    @Test
    void deleteRemovesExistingSuburb() {
        SuburbEntity entity = suburb(10L, 5L, "Temp Suburb");
        when(suburbRepository.findById(10L)).thenReturn(Optional.of(entity));

        service.deleteSuburb(10L);

        verify(suburbRepository).delete(entity);
    }

    @Test
    void deleteThrowsWhenNotFound() {
        when(suburbRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteSuburb(99L))
                .isInstanceOf(SuburbNotFoundException.class);
        verify(suburbRepository, never()).delete(any());
    }

    @Test
    void deleteThrowsWhenSuburbIsStillUsed() {
        when(suburbRepository.findById(1L)).thenReturn(Optional.of(suburb(1L, 5L, "Sandton")));
        doThrow(new DataIntegrityViolationException("fk_properties_suburb"))
                .when(suburbRepository).flush();

        assertThatThrownBy(() -> service.deleteSuburb(1L))
                .isInstanceOf(SuburbInUseException.class)
                .hasMessageContaining("1");
    }
}