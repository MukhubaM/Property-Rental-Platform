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

import propertyrental.platform.location.api.dto.request.CreateProvinceRequest;
import propertyrental.platform.location.api.dto.request.UpdateProvinceRequest;
import propertyrental.platform.location.api.dto.response.ProvinceResponse;
import propertyrental.platform.location.application.mapper.ProvinceMapper;
import propertyrental.platform.location.application.service.ProvinceService;
import propertyrental.platform.location.domain.ProvinceAlreadyExistsException;
import propertyrental.platform.location.domain.ProvinceInUseException;
import propertyrental.platform.location.domain.ProvinceNotFoundException;
import propertyrental.platform.location.infrastructure.persistence.entity.ProvinceEntity;
import propertyrental.platform.location.infrastructure.persistence.repository.ProvinceRepository;

class ProvinceServiceTest {

    private final ProvinceRepository repository = mock(ProvinceRepository.class);
    private final ProvinceService service = new ProvinceService(repository, new ProvinceMapper());

    private ProvinceEntity province(Long id, String code, String name) {
        ProvinceEntity entity = new ProvinceEntity();
        entity.setId(id);
        entity.setCode(code);
        entity.setName(name);

        return entity;
    }

    // ---- get all / get by id ----

    @Test
    void getAllReturnsMappedProvinces() {
        when(repository.findAll()).thenReturn(List.of(province(3L, "GP", "Gauteng")));

        assertThat(service.getAllProvinces())
                .containsExactly(new ProvinceResponse(3L, "GP", "Gauteng"));
    }

    @Test
    void getByIdReturnsProvinceWhenFound() {
        when(repository.findById(3L)).thenReturn(Optional.of(province(3L, "GP", "Gauteng")));

        assertThat(service.getProvinceById(3L))
                .isEqualTo(new ProvinceResponse(3L, "GP", "Gauteng"));
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getProvinceById(99L))
                .isInstanceOf(ProvinceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ---- create ----

    @Test
    void createSavesAndReturnsNewProvince() {
        when(repository.save(any(ProvinceEntity.class))).thenAnswer(invocation -> {
            ProvinceEntity saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        ProvinceResponse response = service.createProvince(new CreateProvinceRequest("TST", "Test Province"));

        assertThat(response).isEqualTo(new ProvinceResponse(10L, "TST", "Test Province"));
    }

    @Test
    void createThrowsWhenCodeAlreadyExists() {
        when(repository.existsByCode("GP")).thenReturn(true);

        assertThatThrownBy(() -> service.createProvince(new CreateProvinceRequest("GP", "Other")))
                .isInstanceOf(ProvinceAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(repository, never()).save(any());
    }

    @Test
    void createThrowsWhenNameAlreadyExists() {
        when(repository.existsByName("Gauteng")).thenReturn(true);

        assertThatThrownBy(() -> service.createProvince(new CreateProvinceRequest("OTH", "Gauteng")))
                .isInstanceOf(ProvinceAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(repository, never()).save(any());
    }

    // ---- update ----

    @Test
    void updateSavesAndReturnsUpdatedProvince() {
        ProvinceEntity entity = province(10L, "TST", "Test Province");
        when(repository.findById(10L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        ProvinceResponse response = service.updateProvince(10L, new UpdateProvinceRequest("TST", "Test Province 2"));

        assertThat(response).isEqualTo(new ProvinceResponse(10L, "TST", "Test Province 2"));
    }

    @Test
    void updateThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProvince(99L, new UpdateProvinceRequest("X", "Y")))
                .isInstanceOf(ProvinceNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void updateThrowsWhenCodeBelongsToAnotherProvince() {
        when(repository.findById(10L)).thenReturn(Optional.of(province(10L, "TST", "Test Province")));
        when(repository.existsByCodeAndIdNot("GP", 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateProvince(10L, new UpdateProvinceRequest("GP", "Test Province")))
                .isInstanceOf(ProvinceAlreadyExistsException.class)
                .hasMessageContaining("code");
        verify(repository, never()).save(any());
    }

    @Test
    void updateThrowsWhenNameBelongsToAnotherProvince() {
        when(repository.findById(10L)).thenReturn(Optional.of(province(10L, "TST", "Test Province")));
        when(repository.existsByNameAndIdNot("Gauteng", 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.updateProvince(10L, new UpdateProvinceRequest("TST", "Gauteng")))
                .isInstanceOf(ProvinceAlreadyExistsException.class)
                .hasMessageContaining("name");
        verify(repository, never()).save(any());
    }

    // ---- delete ----

    @Test
    void deleteRemovesExistingProvince() {
        ProvinceEntity entity = province(10L, "TST", "Test Province");
        when(repository.findById(10L)).thenReturn(Optional.of(entity));

        service.deleteProvince(10L);

        verify(repository).delete(entity);
    }

    @Test
    void deleteThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteProvince(99L))
                .isInstanceOf(ProvinceNotFoundException.class);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteThrowsWhenProvinceIsStillUsed() {
        when(repository.findById(3L)).thenReturn(Optional.of(province(3L, "GP", "Gauteng")));
        doThrow(new DataIntegrityViolationException("fk_cities_province"))
                .when(repository).flush();

        assertThatThrownBy(() -> service.deleteProvince(3L))
                .isInstanceOf(ProvinceInUseException.class)
                .hasMessageContaining("3");
    }
}