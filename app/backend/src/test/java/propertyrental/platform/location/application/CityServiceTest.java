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

import propertyrental.platform.location.api.dto.request.CreateCityRequest;
import propertyrental.platform.location.api.dto.request.UpdateCityRequest;
import propertyrental.platform.location.api.dto.response.CityResponse;
import propertyrental.platform.location.application.mapper.CityMapper;
import propertyrental.platform.location.application.service.CityService;
import propertyrental.platform.location.domain.CityAlreadyExistsException;
import propertyrental.platform.location.domain.CityInUseException;
import propertyrental.platform.location.domain.CityNotFoundException;
import propertyrental.platform.location.domain.CityProvinceNotFoundException;
import propertyrental.platform.location.infrastructure.persistence.entity.CityEntity;
import propertyrental.platform.location.infrastructure.persistence.repository.CityRepository;
import propertyrental.platform.location.infrastructure.persistence.repository.ProvinceRepository;

class CityServiceTest {

    private final CityRepository cityRepository = mock(CityRepository.class);
    private final ProvinceRepository provinceRepository = mock(ProvinceRepository.class);
    private final CityService service = new CityService(cityRepository, provinceRepository, new CityMapper());

    private CityEntity city(Long id, Long provinceId, String name) {
        CityEntity entity = new CityEntity();
        entity.setId(id);
        entity.setProvinceId(provinceId);
        entity.setName(name);

        return entity;
    }

    // ---- list/get by id ----
    @Test
    void getAllReturnsAllCitiesWhenNoProvinceFilter() {
        when(cityRepository.findAll()).thenReturn(List.of(city(1L, 3L, "Johannesburg")));

        assertThat(service.getAllCities(null))
                .containsExactly(new CityResponse(1L, 3L, "Johannesburg"));
    }

    @Test
    void getAllReturnsOnlyCitiesOfTheGivenProvince() {
        when(cityRepository.findByProvinceId(3L)).thenReturn(List.of(city(1L, 3L, "Johannesburg")));

        assertThat(service.getAllCities(3L))
                .containsExactly(new CityResponse(1L, 3L, "Johannesburg"));
        verify(cityRepository, never()).findAll();
    }

    @Test
    void getByIdReturnsCityWhenFound() {
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city(1L, 3L, "Johannesburg")));

        assertThat(service.getCityById(1L)).isEqualTo(new CityResponse(1L, 3L, "Johannesburg"));
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(cityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCityById(99L))
                .isInstanceOf(CityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ---- create ----
    @Test
    void createSavesAndReturnsNewCity() {
        when(provinceRepository.existsById(3L)).thenReturn(true);
        when(cityRepository.save(any(CityEntity.class))).thenAnswer(invocation -> {
            CityEntity saved = invocation.getArgument(0);
            saved.setId(10L);

            return saved;
        });

        CityResponse response = service.createCity(new CreateCityRequest(3L, "Temp City"));

        assertThat(response).isEqualTo(new CityResponse(10L, 3L, "Temp City"));
    }

    @Test
    void createThrowsWhenProvinceDoesNotExist() {
        when(provinceRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.createCity(new CreateCityRequest(99L, "Temp City")))
                .isInstanceOf(CityProvinceNotFoundException.class);
        verify(cityRepository, never()).save(any());
    }

    @Test
    void createThrowsWhenNameAlreadyExistsInProvince() {
        when(provinceRepository.existsById(3L)).thenReturn(true);
        when(cityRepository.existsByProvinceIdAndNameIgnoreCase(3L, "Johannesburg")).thenReturn(true);

        assertThatThrownBy(() -> service.createCity(new CreateCityRequest(3L, "Johannesburg")))
                .isInstanceOf(CityAlreadyExistsException.class);
        verify(cityRepository, never()).save(any());
    }

    // ---- update ----
    @Test
    void updateSavesAndReturnsUpdatedCity() {
        CityEntity entity = city(10L, 3L, "Temp City");
        when(cityRepository.findById(10L)).thenReturn(Optional.of(entity));
        when(provinceRepository.existsById(3L)).thenReturn(true);
        when(cityRepository.save(entity)).thenReturn(entity);

        CityResponse response = service.updateCity(10L, new UpdateCityRequest(3L, "Temp City 2"));

        assertThat(response).isEqualTo(new CityResponse(10L, 3L, "Temp City 2"));
    }

    @Test
    void updateThrowsWhenNotFound() {
        when(cityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateCity(99L, new UpdateCityRequest(3L, "X")))
                .isInstanceOf(CityNotFoundException.class);
        verify(cityRepository, never()).save(any());
    }

    @Test
    void updateThrowsWhenProvinceDoesNotExist() {
        when(cityRepository.findById(10L)).thenReturn(Optional.of(city(10L, 3L, "Temp City")));
        when(provinceRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.updateCity(10L, new UpdateCityRequest(99L, "Temp City")))
                .isInstanceOf(CityProvinceNotFoundException.class);
        verify(cityRepository, never()).save(any());
    }

    @Test
    void updateThrowsWhenNameBelongsToAnotherCityInProvince() {
        when(cityRepository.findById(10L)).thenReturn(Optional.of(city(10L, 3L, "Temp City")));
        when(provinceRepository.existsById(3L)).thenReturn(true);
        when(cityRepository.existsByProvinceIdAndNameIgnoreCaseAndIdNot(3L, "Johannesburg", 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateCity(10L, new UpdateCityRequest(3L, "Johannesburg")))
                .isInstanceOf(CityAlreadyExistsException.class);
        verify(cityRepository, never()).save(any());
    }

    // ---- delete ----
    @Test
    void deleteRemovesExistingCity() {
        CityEntity entity = city(10L, 3L, "Temp City");
        when(cityRepository.findById(10L)).thenReturn(Optional.of(entity));

        service.deleteCity(10L);

        verify(cityRepository).delete(entity);
    }

    @Test
    void deleteThrowsWhenNotFound() {
        when(cityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteCity(99L))
                .isInstanceOf(CityNotFoundException.class);
        verify(cityRepository, never()).delete(any());
    }

    @Test
    void deleteThrowsWhenCityIsStillUsed() {
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city(1L, 3L, "Johannesburg")));
        doThrow(new DataIntegrityViolationException("fk_suburbs_city"))
                .when(cityRepository).flush();

        assertThatThrownBy(() -> service.deleteCity(1L))
                .isInstanceOf(CityInUseException.class)
                .hasMessageContaining("1");
    }
}